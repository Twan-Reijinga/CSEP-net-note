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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownEditorCtrl {
    @FXML
    private TextArea noteText;

    @FXML
    private TextField titleField;

    @FXML
    private WebView markdownPreview;

    // TODO: does it need to be moved to config? and is it actually necessary in the code?
    private final long REFRESH_THRESHOLD = 500;

    private boolean timeState = false;
    private boolean isContentsSynced = true;

    private final Timer refreshTimer;
    private final Parser parser;
    private final HtmlRenderer renderer;
    private final ScheduledExecutorService scheduler;

    private final ServerUtils serverUtils;
    private final Config config;
    private final MainCtrl mainCtrl;

    private Note activeNote;
    private SidebarCtrl sidebarCtrl;

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

    @FXML
    public void initialize(SidebarCtrl sidebarCtrl) {
        this.sidebarCtrl = sidebarCtrl;
        activeNote = serverUtils.mockGetDefaultNote();

        noteText.setText(activeNote.content);
        titleField.setText(activeNote.title);
        requestRefresh();

        scheduler.scheduleAtFixedRate(
                this::syncNoteContents,
                0,
                config.getSyncThresholdMs(),
                TimeUnit.MILLISECONDS
        );

        // let title field fill 100% width of left plane //
        AnchorPane.setLeftAnchor(titleField, 0.0);
        AnchorPane.setRightAnchor(titleField, 0.0);
    }

    /**
     * Updating the active note view to display a new note given by the specified ID.
     * @param newId The database ID of the note that need to be displayed.
     */
    public void updateNote(long newId) {
        activeNote.content = noteText.getText();
        if (serverUtils.existsNoteById(activeNote.id)) {    // Filtering removed notes
            serverUtils.updateNote(activeNote);
            mainCtrl.updateTags(activeNote);
        }
        activeNote = serverUtils.getNoteById(newId);
        noteText.setText(activeNote.content);
        titleField.setText(activeNote.title);
        requestRefresh();
    }

    public synchronized void onKeyTyped(KeyEvent e) {
        isContentsSynced = false;
        requestRefresh();
    }

    public synchronized void onTitleEdit() {
        activeNote.title = titleField.getText();
        isContentsSynced = false;
        requestRefresh();
        sidebarCtrl.updateTitle(activeNote.id, activeNote.title);
    }

    public synchronized void requestRefresh() {
        if(getTimeState()) return;

        setTimeState(true);
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() { refreshView(); }
        }, REFRESH_THRESHOLD);
    }

    private synchronized void refreshView() {
        setTimeState(false);
        String convertedTags = convertTagsToLinks(noteText.getText());
        String titleMarkdown = "# " + titleField.getText() + "\n\n";
        String html = convertMarkdownToHtml(titleMarkdown + convertedTags);


        // FIXME (edited): intuition: hangs the application when UI is closed; maybe that's not the problem
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
        return engine;
    }

    private synchronized void syncNoteContents() {
        if (isContentsSynced) return;

        // FIXME: do something meaningful?
        if (activeNote == null) return;

        // TODO: lazy implementation of threading (not sure of the performance)
        // https://openjfx.io/javadoc/23/javafx.graphics/javafx/application/Platform.html#runLater(java.lang.Runnable)
        Platform.runLater(() -> {
            activeNote.content = noteText.getText();
            serverUtils.updateNote(activeNote);
            mainCtrl.updateTags(activeNote);
            isContentsSynced = true;
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

    private String convertTagsToLinks(String text){
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(text);

        StringBuffer textBuffer = new StringBuffer();
        while (matcher.find()) {
            String tag = matcher.group().substring(1);
            String link = "<a href='#' onclick='app.tagClick(\""
                    + tag + "\")' " + getLinkCSS() + ">"
                    + tag + "</a>";
            matcher.appendReplacement(textBuffer, link);
        }
        matcher.appendTail(textBuffer);

        return textBuffer.toString();
    }

    public void tagClick(String tag){
        this.mainCtrl.addTag("#" + tag);
    }

    private String getLinkCSS(){
        return "style='display: inline-block; " +
                "padding: 2px 4px; " +
                "border: 1px solid #333; " +
                "background-color: #ccc; " +
                "border-radius: 8px; " +
                "color: #000; " +
                "text-decoration: none;'";
    }
}
