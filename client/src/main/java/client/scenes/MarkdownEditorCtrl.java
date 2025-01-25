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
import com.google.inject.Inject;
import commons.Note;
import commons.NoteTitle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import netscape.javascript.JSObject;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.ext.footnotes.FootnotesExtension;
import java.util.HashMap;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commons.Collection;

public class MarkdownEditorCtrl {
    @FXML
    private TextArea noteText;

    @FXML
    private HBox topControlsContainer;

    @FXML
    private TextField titleField;

    @FXML
    private ComboBox<Pair<UUID, String>> collectionDropdown;

    @FXML
    private WebView markdownPreview;

    // TODO: does it need to be moved to config? and is it actually necessary in the code?
    private final long REFRESH_THRESHOLD = 500;

    private boolean timeState = false;
    private boolean isContentsSynced = true;
    private List<String> forbiddenTitles = new ArrayList<>();

    private final Timer refreshTimer;
    private final Parser parser;
    private final HtmlRenderer renderer;
    private final ScheduledExecutorService scheduler;

    private final ServerUtils serverUtils;
    private final Config config;
    private final MainCtrl mainCtrl;

    private Note activeNote;
    private SidebarCtrl sidebarCtrl;
    private boolean titleChanged = false;
    private String initialTitle = "";

    @Inject
    public MarkdownEditorCtrl(ServerUtils serverUtils, Config config, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.config = config;
        this.refreshTimer = new Timer();
        this.mainCtrl = mainCtrl;

        var ext = List.of(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                AutolinkExtension.create(),
                InsExtension.create(),
                HeadingAnchorExtension.create(),
                TaskListItemsExtension.create(),
                FootnotesExtension.create()
        );

        this.parser = Parser.builder().extensions(ext).build();
        this.renderer = HtmlRenderer.builder().extensions(ext).build();
        this.scheduler = Executors.newScheduledThreadPool(1);

    }

    /**
     * JavaFX method that automatically runs when this controller is initialized.
     * @param sidebarCtrl The sidebar controller.
     */
    @FXML
    public void initialize(SidebarCtrl sidebarCtrl) {
        this.sidebarCtrl = sidebarCtrl;

        scheduler.scheduleAtFixedRate(
                this::syncNoteContents,
                0,
                5000,
                TimeUnit.MILLISECONDS
        );

        // let title field fill 100% width of left plane //
        AnchorPane.setLeftAnchor(topControlsContainer, 0.0);
        AnchorPane.setRightAnchor(topControlsContainer, 0.0);

        collectionDropdown.setCellFactory(_ -> createCollectionDropdownOption());
        collectionDropdown.setButtonCell(createCollectionDropdownOption());
        serverUtils.connection.subscribe(update -> {
            if(update.note == null) {
                // If note is null then the update was for collections
                Platform.runLater(this::loadCollectionDropdown);
                return;
            }
            if(update.note.id != activeNote.id){
                this.mainCtrl.updateValidLinks();
                requestRefresh();
                return;
            }
            Platform.runLater(this::handleWebsocketUpdate);
        });
    }

    private void handleWebsocketUpdate() {
        if(serverUtils.existsNoteById(activeNote.id)){
            clearInvalidTitleStyle();
            activeNote = serverUtils.getNoteById(activeNote.id);
            var pos = noteText.getCaretPosition();
            noteText.setText(activeNote.content);
            noteText.positionCaret(pos);
            titleField.setText(activeNote.title);

            this.titleChanged = false;
            this.initialTitle = activeNote.title;
            this.mainCtrl.updateValidLinks();

            requestRefresh();
            loadCollectionDropdown();
            updateForbiddenTitles();
        }
        else{
            String errorMessage = "The note you were editing was deleted from another client!";
            this.sidebarCtrl.showMessage(errorMessage,true);
            this.sidebarCtrl.changeSelectedNote();
        }
    }

    /**
     * Updating the active note view to display a new note given by the specified ID.
     * @param newId The database ID of the note that need to be displayed.
     */
    public void updateNote(long newId) {
        // To remove possible error color of having the same title
        clearInvalidTitleStyle();

        // If there was a previous note and if it still exists then save it
        if (activeNote != null && serverUtils.existsNoteById(activeNote.id)) {
            saveActiveNote();
        }

        activeNote = serverUtils.getNoteById(newId);

        this.titleChanged = false;
        this.initialTitle = activeNote.title;

        noteText.setText(activeNote.content);
        titleField.setText(activeNote.title);
        requestRefresh();
        loadCollectionDropdown();

        // Update forbidden titles for a newly chosen note
        updateForbiddenTitles();
    }

    private void updateForbiddenTitles() {
        if (activeNote == null) {
            forbiddenTitles = new ArrayList<>();
            return;
        }

        forbiddenTitles = serverUtils.getNoteTitlesInCollection(activeNote.collection.id).stream()
                .filter(n -> n.getId() != activeNote.id)
                .map(NoteTitle::getTitle)
                .toList();
    }

    private void saveActiveNote() {
        if (activeNote == null) return;
        updateForbiddenTitles();

        if (forbiddenTitles.contains(activeNote.title)) {
            mainCtrl.showMessage("Duplicate title: " + activeNote.title, true);
            // Replace a duplicate title with the original one
            activeNote.title = serverUtils.getNoteTitleById(activeNote.id).getTitle();
        }

        // Note contents can be updated anyway because no validation is required
        activeNote.content = noteText.getText();
        try {
            this.mainCtrl.updateNote(activeNote, titleChanged, initialTitle, activeNote.title);
            this.titleChanged = false;
            this.initialTitle = activeNote.title;

            // Refresh the titles in the sidebar
            sidebarCtrl.refresh();

            // The active note has been synced
            isContentsSynced = true;
        } catch (Exception e) {
            System.out.println("Error updating note: " + activeNote);
            e.printStackTrace();
            mainCtrl.showMessage("Failed to update note: " + activeNote.title, true);
        }
        mainCtrl.updateTags(activeNote);
    }

    private ListCell<Pair<UUID, String>> createCollectionDropdownOption() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Pair<UUID, String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setText(item.getValue());
                }
            }
        };
    }

    private void loadCollectionDropdown() {
        List<Collection> collections = serverUtils.getAllCollections();
        List<Pair<UUID, String>> titles = collections.stream()
                .map(c -> new Pair<>(c.id, c.title)).toList();

        collectionDropdown.getItems().clear();
        collectionDropdown.getItems().addAll(titles);

        // Select a collection where the note belongs
        for (var pair : titles) {
            if (pair.getKey().equals(activeNote.collection.id)) {
                collectionDropdown.getSelectionModel().select(pair);
                break;
            }
        }
    }

    @FXML
    private void onCollectionClick() {
        Pair<UUID, String> selected = collectionDropdown.getSelectionModel().getSelectedItem();

        if (selected != null && !selected.getKey().equals(activeNote.collection.id)) {
            Collection savedCollection = activeNote.collection;

            try {
                activeNote.collection = serverUtils.getCollectionById(selected.getKey());
                serverUtils.updateNote(activeNote);

                sidebarCtrl.refresh();
                sidebarCtrl.showMessage("Note moved to collection " + activeNote.collection.title, false);
            } catch (Exception e) {
                sidebarCtrl.showMessage(
                        "Failed to move note to collection: %s".formatted(selected.getValue()) +
                        "Make sure that the destination collection doesn't have a note with the same title.",
                        true);
                // Restore to the original collection
                activeNote.collection = savedCollection;

                // Select a correct collection in the dropdown after failure
                for (var pair : collectionDropdown.getItems()) {
                    if (pair.getKey().equals(activeNote.collection.id)) {
                        collectionDropdown.getSelectionModel().select(pair);
                    }
                }
            }
            requestRefresh();
        }
    }

    /**
     * Action when a new key is typed.
     * Requests a refresh.
     * @param e the key event that is typed.
     */
    public synchronized void onKeyTyped(KeyEvent e) {
        isContentsSynced = false;
        requestRefresh();
    }

    /**
     * Action when title is edited.
     * Requests a refresh and updates title immediately in sidebar.
     */
    public synchronized void onTitleEdit() {
        // To remove possible error color of having the same title
        clearInvalidTitleStyle();

        String newTitle = titleField.getText().strip();

        if (newTitle.isEmpty()) {
            mainCtrl.showMessage("Title cannot be empty.", true);
            applyInvalidTitleStyle();
            return;
        }

        if (forbiddenTitles.contains(newTitle)) {
            mainCtrl.showMessage("Duplicate title: " + newTitle, true);
            applyInvalidTitleStyle();
            return;
        }

        if(!titleChanged){
            this.initialTitle = activeNote.title;
            titleChanged = true;
        }

        activeNote.title = newTitle;
        isContentsSynced = false;

        requestRefresh();
        sidebarCtrl.updateTitle(activeNote.id, activeNote.title);
    }

    /**
     * Request to do a refresh if it wasn't refreshed in a while.
     */
    public synchronized void requestRefresh() {
        if(getTimeState()) return;

        setTimeState(true);
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() { refreshView(); }
        }, REFRESH_THRESHOLD);
    }

    /**
     * Focus on the text field for the title to immediately start editing the title.
     */
    public void focusOnTitle() {
        titleField.requestFocus();
    }

    private void applyInvalidTitleStyle() {
        titleField.setStyle("-fx-background-color: #FFA07A;");
    }

    private void clearInvalidTitleStyle() {
        titleField.setStyle("");
    }

    private synchronized void refreshView() {
        setTimeState(false);
        String convertedTagsAndLinks = convertTagsAndLinks(noteText.getText());
        String titleMarkdown = "# " + titleField.getText() + "\n\n";
        String html = convertMarkdownToHtml(titleMarkdown + convertedTagsAndLinks);


        // Use the jfx thread to update the text
        Platform.runLater(() -> setUpEngine(markdownPreview).loadContent(html));
    }

    private WebEngine setUpEngine(WebView webView) {
        WebEngine engine = webView.getEngine();
        engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("app", this);
            }
        });
        String stylesheetUrl = "/stylesheets/webView_styles.css";
        engine.setUserStyleSheetLocation(getClass().getResource(stylesheetUrl).toExternalForm());
        return engine;
    }

    private synchronized void syncNoteContents() {
        if (isContentsSynced) return;

        if (activeNote == null) return;

        // https://openjfx.io/javadoc/23/javafx.graphics/javafx/application/Platform.html#runLater(java.lang.Runnable)
        Platform.runLater(() -> {
            saveActiveNote();
        });
    }

    // TODO: needs to be hooked up to the actual event "onClose"
    private void cleanup() {
        refreshTimer.cancel();
        scheduler.shutdown();

        // Make sure final changes will be synchronized
        syncNoteContents();
    }

    private String convertMarkdownToHtml(String text) {
        return renderer.render(parser.parse(text));
    }

    private synchronized boolean getTimeState() {
        return timeState;
    }

    private synchronized void setTimeState(boolean timeState) {
        this.timeState = timeState;
    }

    /**
     * This method converts the tags that appear in the given array of strings into html links (anchor tags).
     * It also checks whether the lines start with a tab character and if they do, it doesn't input the HTML.
     * @param textLines an array containing the lines from the text that needs to be converted
     * @return the updated array.
     */
    private String[] convertTagsToLinks(String[] textLines){
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher;
        StringBuffer textBuffer;

        for(int i=0; i<textLines.length; i++){
            if(!textLines[i].startsWith("\t")){
                String line = textLines[i];
                boolean[] ignore = ignoreTextInBrackets(line);
                matcher = pattern.matcher(line);
                textBuffer = new StringBuffer();

                while (matcher.find()) {
                    boolean isIgnored = this.ignoreText(matcher.start(), matcher.end(), ignore);
                    if (!isIgnored) {
                        String tag = matcher.group().substring(1);
                        String link = "<a href='#' class='tags' onclick='app.onTagClicked(\""
                                + tag + "\")'>"
                                + tag + "</a>";
                        matcher.appendReplacement(textBuffer, link);
                    }
                }
                matcher.appendTail(textBuffer);
                textLines[i] = textBuffer.toString();
            }
        }
        return textLines;
    }

    private boolean[] ignoreTextInBrackets(String line){
        Pattern bracketPattern = Pattern.compile("\\[\\[.*?]]");
        Matcher bracketMatcher = bracketPattern.matcher(line);
        boolean[] ignore = new boolean[line.length()];

        while (bracketMatcher.find()) {
            for (int j = bracketMatcher.start(); j < bracketMatcher.end(); j++) {
                ignore[j] = true;
            }
        }
        return ignore;
    }

    private boolean ignoreText(int start, int end, boolean[] ignore){
        boolean isIgnored = false;
        for (int j = start; j < end; j++) {
            if (ignore[j]) {
                isIgnored = true;
                break;
            }
        }
        return isIgnored;
    }

    /**
     * Event when a tag is clicked.
     * @param tag the tag that is clicked on.
     */
    public void onTagClicked(String tag){
        this.mainCtrl.addTagFilter("#" + tag);
    }

    /**
     * This method converts the note links that appear in the given array into HTML anchor tags.
     * If a line starts with a tab character, it remains unchanged.
     * @param textLines an array containing the lines from the text that needs to be converted
     * @param fullText the text from the noteText field.
     * @return the updated array.
     */
    private String[] getNoteLinkHtml(String[] textLines, String fullText){
        HashMap<String, Long> links = this.mainCtrl.getNoteLinks(fullText, this.activeNote.collection.id);
        String html = "";

        for(String link: links.keySet()){
            Long linkedId = links.get(link);
            if(linkedId != null){
                html = "<a class='links-valid' href='#' onclick='app.onLinkClicked(" + linkedId + ")'>";
            }
            else{
                html = "<a class='links-invalid'>";
            }
            html +=  link + "</a>";

            for(int i=0; i<textLines.length; i++){
                if(!textLines[i].startsWith("\t")){
                    textLines[i] = textLines[i].replace("[[" + link + "]]", html);
                }
            }
        }

        return textLines;
    }

    /**
     * This method is called when a user clicks on a valid note-link;
     * It selects the note that the link references as the active note for the controller.
     * @param noteId the id of the note referenced.
     */
    public void onLinkClicked(String noteId){
        this.mainCtrl.linkClicked(Long.parseLong(noteId));
    }

    /**
     * This method converts the tags and links in the textField into HTML before passing them to the
     * Markdown parser. It also checks whether the text is a part of code
     * blocks in which case it doesn't convert links/tags it into HTML.
     * @param text the text from the content field.
     * @return the text after making the necessary conversions.
     */
    private String convertTagsAndLinks(String text){
        String[] textLines = text.split("\n");
        String[] filesRendered = this.embeddedFileToLinks(textLines);
        String[] tagsRendered = this.convertTagsToLinks(filesRendered);
        String[] linksRendered = this.getNoteLinkHtml(tagsRendered, text);

        return String.join("\n", linksRendered);
    }

    private String[] embeddedFileToLinks(String[] text) {
        Pattern pattern = Pattern.compile("!\\[(.*)]\\(([\\S\\w]+)\\)(\\{(\\d+), (\\d+)})?");
        Matcher matcher;
        StringBuffer textBuffer;

        for(int i=0; i<text.length; i++) {
            if (!text[i].startsWith("\t")) {
                matcher = pattern.matcher(text[i]);
                textBuffer = new StringBuffer();

                while (matcher.find()) {
                    String alt = matcher.group(1);
                    String img = matcher.group(2);
                    String width = matcher.group(4);
                    String height = matcher.group(5);

                    String imgURL = config.getServerUrl() + "/api/notes/" +
                            mainCtrl.getSelectedNoteId() + "/embedded/title/" + img;

                    String html = "<img src=\"" + imgURL + "\" " +
                            "alt=\"" + alt + " \" style=\"width: " + width + "; height: " + height + ";\" />";

                    matcher.appendReplacement(textBuffer, html);
                }
                matcher.appendTail(textBuffer);
                text[i] = textBuffer.toString();
            }
        }
        return text;
    }
}
