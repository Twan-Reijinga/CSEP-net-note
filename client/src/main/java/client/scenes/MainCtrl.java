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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;
    private NoteEditorCtrl noteEditorCtrl;
    private Scene noteEditor;

    private MarkdownEditorCtrl markdownEditorCtrl;
    private Scene markdownEditor;

    private SidebarCtrl sidebarCtrl;
    private Scene sidebar;

    public void initialize(
            Stage primaryStage,
            Pair<NoteEditorCtrl, Parent> noteEditor,
            Pair<MarkdownEditorCtrl, Parent> markdownEditor,
            Pair<SidebarCtrl, Parent> sidebarEditor
    )
    {
        this.primaryStage = primaryStage;

        this.noteEditorCtrl = noteEditor.getKey();
        this.noteEditor = new Scene(noteEditor.getValue());

        this.markdownEditorCtrl = markdownEditor.getKey();
        this.markdownEditor = new Scene(markdownEditor.getValue());

        this.sidebarCtrl = sidebarEditor.getKey();
        this.sidebar = new Scene(sidebarEditor.getValue());

        noteEditorCtrl.initialize(sidebarEditor.getValue(), markdownEditor.getValue());
        markdownEditorCtrl.initialize();
        sidebarCtrl.initialize(markdownEditorCtrl);

        showNoteEditor();
        primaryStage.show();
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
}