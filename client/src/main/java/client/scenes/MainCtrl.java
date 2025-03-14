/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.Main;
import client.config.Config;
import client.utils.DialogBoxUtils;
import client.utils.ServerUtils;
import client.handlers.TagFilteringHandler;
import client.utils.*;
import com.google.inject.Inject;
import commons.NoteTitle;
import client.handlers.ShortcutHandler;
import commons.Note;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Pair;
import commons.Collection;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

public class MainCtrl {

    private Stage primaryStage;
    private NoteEditorCtrl noteEditorCtrl;
    private Scene noteEditor;

    private MarkdownEditorCtrl markdownEditorCtrl;
    private SidebarCtrl sidebarCtrl;
    private FilesCtrl filesCtrl;

    private ShortcutHandler shortcutHandler;
    private TagFilteringHandler tagFilteringHandler;
    private NoteLinkHandler noteLinkHandler;

    private final Config config;
    private final ServerUtils serverUtils;
    private boolean isWaiting;

    @Inject
    public MainCtrl(Config config,
                    ServerUtils serverUtils,
                    NoteLinkHandler noteLinkHandler,
                    TagFilteringHandler tagFilteringHandler) {
        this.config = config;
        this.serverUtils = serverUtils;
        this.noteLinkHandler = noteLinkHandler;
        this.tagFilteringHandler = tagFilteringHandler;
        try {
            this.serverUtils.connection.subscribe(update -> {
                if(update.defaultId != null) this.config.setDefaultCollectionId(update.defaultId);
            });
            this.serverUtils.connection.connect(new java.net.URI(this.serverUtils.server).getHost());
        } catch (Exception e) {
            System.out.println("Could not connect to server. Are you sure the server is running?");
        }
    }

    private void initializeDefaultCollection() {
        if (config.getDefaultCollectionId() == null) {
            System.out.println("Requesting default collection...");
            Collection defaultCollection = serverUtils.getDefaultCollection();
            if (defaultCollection == null) {
                throw new IllegalStateException("Default collection cannot be null");
            }
            config.setDefaultCollectionId(defaultCollection.id);
        } else {
            try {
                // Verify if collection still exists on server
                serverUtils.getCollectionById(config.getDefaultCollectionId());
            } catch (Exception e) {
                Collection defaultCollection = serverUtils.getDefaultCollection();
                config.setDefaultCollectionId(defaultCollection.id);
            }
        }
    }


    public void initialize(
            Stage primaryStage,
            Pair<MarkdownEditorCtrl, Parent> markdownEditor,
            Pair<NoteEditorCtrl, Parent> noteEditor,
            Pair<SidebarCtrl, Parent> sidebarEditor,
            Pair<FilesCtrl, Parent> filesEditor,
            ResourceBundle bundle
    ) {
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(_ -> {serverUtils.connection.close(); System.exit(0);});
        this.tagFilteringHandler = new TagFilteringHandler(this.serverUtils);

        this.noteEditorCtrl = noteEditor.getKey();
        this.noteEditor = new Scene(noteEditor.getValue());

        this.markdownEditorCtrl = markdownEditor.getKey();
        this.sidebarCtrl = sidebarEditor.getKey();
        this.filesCtrl = filesEditor.getKey();

        if (!serverUtils.isServerAvailable()) {
            handleServerUnreachable();
            return;
        }

        initializeDefaultCollection();

        noteEditorCtrl.initialize(sidebarEditor, markdownEditor, filesEditor, bundle);
        markdownEditorCtrl.initialize(sidebarCtrl);
        sidebarCtrl.initialize(this, filesCtrl);

        this.shortcutHandler = new ShortcutHandler(this, sidebarCtrl);
        shortcutHandler.attach(this.noteEditor);
        this.noteLinkHandler.initialize();

        showNoteEditor();
        primaryStage.show();
    }

    public void handleServerUnreachable() {
        primaryStage.close();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("HTTP Server Unreachable");
        alert.setHeaderText("Connection Error");
        alert.setContentText("Server is unreachable. Try restarting the server and the application.");
        alert.setHeight(300);

        alert.showAndWait();
    }

    /**
     * Sets the minimum window size for the main note editor window
     */
    public void showNoteEditor() {
        primaryStage.setTitle("NoteEditor");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);
        primaryStage.setScene(noteEditor);
    }

    public void showMessage(String message, boolean isError) {
        sidebarCtrl.showMessage(message, isError);
    }

    /**
     * Sets a new UI language based on user selection
     * Builds a new scene but with all components translated
     * and parses the main stage the root node of the scene
     * @param language The chosen language locale: [en, nl, es]
     */
    public void switchLanguage(Locale language) {
        Main.switchLanguage(language);
    }

    public void updateNote(long id) {
        markdownEditorCtrl.updateNote(id);
    }

    /**
     * Getter for the id of the selected note
     * in the sidebar based on which item is clicked last.
     * If there is not yet a specific note selected,
     * -1 will be returned as a default value.
     *
     * @return The id as a Long of the selected note
     * or -1 if nothing is selected.
     */
    public long getSelectedNoteId() {
        return sidebarCtrl.getSelectedNoteId();
    }

    /**
     * Record the action of adding a note so the noteId can be locally stored and reversed with an undo later.
     * @param noteId The noteId of the added note.
     */
    public void recordAdd(Long noteId) {
        shortcutHandler.recordAdd(noteId);
    }

    /**
     * record the action of deleting a note so the note can be locally stored and reversed with an undo later.
     * @param note A copy of the note that can be revered.
     */
    public void recordDelete(Note note) {
        shortcutHandler.recordDelete(note);
    }

    /** This method sends data for the creation of a get request to the server and passes the returned data
     *  to the sidebar, forcing it to update itself.
     *
     * @param text  text for the search request
     * @param collectionId id of collection to search
     * @param matchAll option to match all keywords or not
     * @param whereToSearch option to search specific parts of a note
     */
    public void sendSearchRequest(String text, UUID collectionId, boolean matchAll, int whereToSearch) {
        List<NoteTitle> results = serverUtils.searchNotesInCollection(collectionId, text, matchAll, whereToSearch);
        updateSideBar(results);
    }

    public void updateSideBar(List<NoteTitle> titles){
        sidebarCtrl.loadNoteTitles(titles);
    }

    public void refreshSideBar(){ sidebarCtrl.refresh(); }

    /**
     * Called when a note is updated, checks the new content for tags and updates it if there are any.
     * @param note The note that was changed.
     */
    public void updateTags(Note note){
        List<String> removedTags = this.tagFilteringHandler.updateNoteTags(note);
        if(!removedTags.isEmpty()){
            this.noteEditorCtrl.removeTagsFromHBox(removedTags);
        }
        this.applyFiltersToSideBar();
    }

    /**
     * Called when a note is deleted, deletes it's tags from the list of available tags
     * (unless another note has them too).
     * @param id The id of the note that was deleted.
     */
    public void deleteTags(Long id){
        this.tagFilteringHandler.deleteNoteTags(id);
        this.clearTagFilters();
    }

    /**
     * Called when a note is added, checks for any tags in the new note.
     * @param note The note that was added.
     */
    public void addNewTags(Note note){
        this.tagFilteringHandler.addNoteTags(note);
        this.clearTagFilters();
    }

    /**
     * Used to get the ids of the notes that should be displayed after
     * applying filters and then display them.
     */
    public void applyFiltersToSideBar(){
        this.sidebarCtrl.displayNoteTitles(this.tagFilteringHandler.getNotesToDisplay());
        this.noteEditorCtrl.loadTagOptions();
    }

    /**
     * Called when the sidebar gets loaded with new content.
     * Gives tagFilteringHandler the id's of the new NoteTitles and the handler uses
     * them to request the tags of the corresponding notes from the server.
     * @param noteTagIds a list containing the id's of the new NoteTitles loaded in the sidebar.
     */
    public void loadNewNoteTags(List<Long> noteTagIds){
        this.tagFilteringHandler.loadNewNoteTags(noteTagIds);
        this.persistNoteTags();
    }

    /**
     * This method is used to remove only the tags that are no longer present and
     * keep the others as filters.
     */
    public void persistNoteTags(){
        List<String> removedTags = this.tagFilteringHandler.persistTags();
        this.noteEditorCtrl.removeTagsFromHBox(removedTags);
        this.applyFiltersToSideBar();
    }

    /**
     * This method calls methods from the TagFilteringHandler
     * and noteEditorCtrl, in order to clear all tags selected for filtering
     * and remove them from the display bar. Then it calls applyFiltersToSideBar()
     * which makes the sidebar show all NoteTitles without any filters applied.
     */
    public void clearTagFilters(){
        this.noteEditorCtrl.removeTagsFromHBox(this.tagFilteringHandler.tagsSelected);
        this.tagFilteringHandler.clearTags();
        this.applyFiltersToSideBar();
    }

    /**
     * This method is called when a tag was clicked on in the WebView or
     * selected from the options in the MenuButton. It adds the tag as a filter
     * updates the sideBar to show the newly filtered results and adds it to the
     * tag display bar.
     * @param tag the tag that will be set as a filter.
     */
    public void addTagFilter(String tag){
        this.tagFilteringHandler.addTag(tag);
        this.applyFiltersToSideBar();
        this.noteEditorCtrl.addSelectedTagToHBox(tag);
    }

    /**
     * This method is used to get the text for the items in the MenuButton.
     * @return a list of the tags available (except those already selected).
     */
    public List<String> listAvailableTags(){
        return this.tagFilteringHandler.getAvailableTags();
    }

    /**
     * Focus on the text field of the searchbar you can immediately search when starting to type
     */
    public void focusOnSearch() {
        noteEditorCtrl.focusOnSearch();
    }

    /**
     * Focus on the text field for the title to immediately start editing the title
     */
    public void focusOnTitle() {
        markdownEditorCtrl.focusOnTitle();
    }

    /**
     * Open a popup to ask the user if it wants to delete a specific note.
     * The user can choose between delete and cancel.
     * @param noteTitle The title of the note.
     * @return True for delete and false for cancel.
     */
    public boolean userConfirmDeletion(String noteTitle) {
        // needs to be final for eventHandlers //
        final boolean[] isConfirmed = {false};
        isWaiting = true;

        EventHandler<ActionEvent> deleteAction = _ -> isConfirmed[0] = true; // Confirm deletion
        EventHandler<ActionEvent> cancelAction = _ -> isConfirmed[0] = false; // Cancel deletion

        String title = "Delete Note";
        String content = "Are you sure you want to delete \"" + noteTitle + "\"?";

        DialogBoxUtils.createSimpleDialog(
                title, content,
                "Delete", deleteAction,
                "Cancel", cancelAction
        ).showAndWait();

        isWaiting = false;
        return isConfirmed[0];
    }

    /**
     * Get the default collection ID from the local config file.
     * Propagates a call to a Config entity.
     * @return a UUID of default collection
     */
    public UUID getDefaultCollectionId() {
        return config.getDefaultCollectionId();
    }

    /**
     * Moves to the next item in the dropdown, skipping the last option ("Edit Collections").
     * If the second-to-last item is selected, it wraps around to the first item.
     * The method calculates the next index. If it reaches the last option, it wraps to the
     * first item. Otherwise, it selects the next item. This ensures smooth navigation while
     * avoiding the special last option.
     * The dropdown must be properly set up with items before calling this method.
     * If the dropdown is empty, nothing happens.
     */
    public void selectNextCollection() {
        noteEditorCtrl.selectNextCollection();
    }

    /**
     * Moves to the previous item in the dropdown, skipping the last option ("Edit Collections").
     * If the first item is selected, it wraps around to the second-to-last item.
     * The method checks if the current selection is the first item. If so, it selects the
     * second-to-last item. Otherwise, it moves to the previous item. This ensures smooth
     * backward navigation while avoiding the special last option.
     * The dropdown must be properly set up with items before calling this method.
     * If the dropdown is empty, nothing happens.
     */
    public void selectPreviousCollection() {
        noteEditorCtrl.selectPreviousCollection();
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    /**
     * This method finds all links to notes in the note's content.
     * @param content The text (content of the note) which is scanned for note links
     * @param collectionId the id of the note's collection.
     * @return A map mapping each link to the id of the note in the same collection
     * it represents or null if the link is invalid.
     */
    public HashMap<String, Long> getNoteLinks(String content, UUID collectionId){
        return this.noteLinkHandler.getLinks(content, collectionId);
    }

    /**
     * Called when a note-link is clicked.
     * Selects the id of the clicked as a selectedNote in the sidebar.
     * @param id the id of the note to which the link points to
     */
    public void linkClicked(Long id){
        this.sidebarCtrl.noteLinkClicked(id);
    }

    /**
     * Called when a noteTitle is updated. Forces all notes referencing the one that had it's title
     * updated to update their contents accordingly.
     * @param id id of note updated
     * @param oldTitle the previous title of the note
     * @param newTitle the new title of the note
     */
    public void updateNoteLinks(Long id, String oldTitle, String newTitle){
        this.serverUtils.updateLinksToNote(id, newTitle, oldTitle);
    }

    /**
     * Called when a note is updated. Checks whether the title was updated and
     * updates the note links to this note if necessary.
     * @param note The note that has to be updated
     * @param titleChanged boolean for whether the title was changed
     * @param oldTitle the old title of the note
     * @param newTitle the new title of the note
     */
    public void updateNote(Note note, boolean titleChanged, String oldTitle, String newTitle){
        if(titleChanged){
            this.updateNoteLinks(note.id, oldTitle, newTitle);
            this.updateLink(newTitle, note.id, note.collection.id);
            note.content = this.serverUtils.getNoteById(note.id).content;
        }
        serverUtils.updateNote(note);
    }

    /**
     * Called when a note is deleted, deletes it from the valid links.
     * @param title The title of the note that was deleted
     * @param collectionId The id of the collection that the note is in.
     */
    public void deleteLink(String title, UUID collectionId){
        this.noteLinkHandler.deleteLink(title, collectionId);
    }

    /**
     * Called when a note is added, updates the locally stored links to include its title as a valid link.
     * @param collectionId The id of the collection that the note is in.
     */
    public void addLink(UUID collectionId){
        this.noteLinkHandler.addLink(collectionId);
    }

    /**
     * Called when a note's title is updated.
     * @param title The new title for the note
     * @param id the id of the note that was added.
     * @param collectionId The id of the collection that the note is in.
     */
    public void updateLink(String title, Long id, UUID collectionId){
        this.noteLinkHandler.updateLink(title, id, collectionId);
    }

    public void updateValidLinks(){
        this.noteLinkHandler.updateNoteTitlesInCollection();
    }
}
