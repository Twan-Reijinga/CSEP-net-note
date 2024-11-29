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

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.ext.footnotes.FootnotesExtension;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MarkdownEditorCtrl {

    public long getRefreshThreshold() {
        return refreshThreshold;
    }

    public void setRefreshThreshold(long refreshThreshold) {
        this.refreshThreshold = refreshThreshold;
    }

    public synchronized boolean getTimeState() {
        return timeState;
    }

    public synchronized void setTimeState(boolean timeState) {
        this.timeState = timeState;
    }


    private long refreshThreshold = 500;
    private final Timer refreshTimer;
    private boolean timeState = false;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextArea noteText;

    @FXML
    private WebView markdownPreview;

    @FXML
    private SplitPane divider;

    private final Parser parser;
    private final HtmlRenderer renderer;

    @Inject
    public MarkdownEditorCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

        this.refreshTimer = new java.util.Timer();

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
    }

    public synchronized void requestRefresh(KeyEvent e) {
        if(getTimeState()) return;

        setTimeState(true);
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() { refreshView(); }
        }, refreshThreshold);
    }

    public String convertMarkdownToHtml(String text) {
        return renderer.render(parser.parse(text));
    }

    public synchronized void refreshView() {
        setTimeState(false);
        String html = convertMarkdownToHtml(noteText.getText());

        // Use the jfx thread to update the text
        Platform.runLater(() -> markdownPreview.getEngine().loadContent(html));
    }

    public void setDivider(double percent) {
        divider.setDividerPositions(percent);
    }
}