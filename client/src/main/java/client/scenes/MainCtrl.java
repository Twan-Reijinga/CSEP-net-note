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

import client.config.Config;
import client.utils.ServerUtils;
import client.utils.TagFilteringHandler;
import commons.NoteTitle;
import client.utils.ShortcutHandler;
import commons.Note;
import jakarta.inject.Inject;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import commons.Collection;

import java.util.List;
import java.util.UUID;

public class MainCtrl {

    private Stage primaryStage;
    private NoteEditorCtrl noteEditorCtrl;
    private Scene noteEditorEnglish;

    private MarkdownEditorCtrl markdownEditorCtrl;
    private SidebarCtrl sidebarCtrl;
    private Scene sidebar;
    private ShortcutHandler shortcutHandler;
    private TagFilteringHandler tagFilteringHandler;

    private final Config config;
    private final ServerUtils serverUtils;

    @Inject
    public MainCtrl(Config config, ServerUtils serverUtils) {
        this.config = config;
        this.serverUtils = serverUtils;

        // TODO: consider a better place for default collection initialization
        if (config.getDefaultCollectionId() == null) {
            System.out.println("Requesting default collection...");

            Collection defaultCollection = serverUtils.getDefaultCollection();
            config.setDefaultCollectionId(defaultCollection.id);
        }
    }


    public void initialize(
            Stage primaryStage,
            Pair<MarkdownEditorCtrl, Parent> markdownEditor,
            Pair<NoteEditorCtrl, Parent> noteEditor,
            Pair<SidebarCtrl, Parent> sidebarEditor
    )
    {
        this.primaryStage = primaryStage;

        this.tagFilteringHandler = new TagFilteringHandler(this.serverUtils);

        this.noteEditorCtrl = noteEditor.getKey();
        this.noteEditorEnglish = new Scene(noteEditor.getValue());

        this.markdownEditorCtrl = markdownEditor.getKey();
        this.sidebarCtrl = sidebarEditor.getKey();

        noteEditorCtrl.initialize(sidebarEditor, markdownEditor);
        markdownEditorCtrl.initialize(sidebarCtrl);
        sidebarCtrl.initialize(this);

        this.shortcutHandler = new ShortcutHandler(sidebarCtrl);
        shortcutHandler.attach(noteEditorEnglish);

        showNoteEditor();
        primaryStage.show();
        sidebarCtrl.refresh();
    }

    /**
     * Sets the minimum window size for the main note editor window
     */
    public void showNoteEditor() {
        primaryStage.setTitle("NoteEditor");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);
        primaryStage.setScene(noteEditorEnglish);
    }

    /**
     * Sets a new UI language based on user selection
     * Builds a new scene but with all components translated
     * and parses the main stage the root node of the scene
     * @param language the chosen language by the user
     */
    public void changeUILanguage(String language) {
        switch (language){
            case "English":
                break;
            case "Dutch":
                break;
            case "Spanish":
                break;
        }
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
     * record the action of adding a note so the noteId can be locally stored and reversed with an undo later.
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
    public void sendSearchRequest(String text, UUID collectionId, boolean matchAll, String whereToSearch) {
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

    public void loadNewNoteTags(List<Long> noteTagIds){
        this.tagFilteringHandler.loadNewNoteTags(noteTagIds);
        this.clearTagFilters();
    }

    public void clearTagFilters(){
        this.tagFilteringHandler.clearTags();
        this.noteEditorCtrl.clearSelectedTagsFromHBox();
        this.applyFiltersToSideBar();
    }

    public void addTagFilter(String tag){
        this.tagFilteringHandler.addTag(tag);
        this.applyFiltersToSideBar();
        this.noteEditorCtrl.addSelectedTagToHBox(tag);
    }

    public List<String> listAvailableTags(){
        return this.tagFilteringHandler.getAvailableTags();
    }
}
