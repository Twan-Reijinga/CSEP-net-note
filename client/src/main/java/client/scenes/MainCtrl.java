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
    private Scene noteEditorEnglish;

    private MarkdownEditorCtrl markdownEditorCtrl;
    private SidebarCtrl sidebarCtrl;
    private FilesCtrl filesCtrl;


    public void initialize(
            Stage primaryStage,
            Pair<MarkdownEditorCtrl, Parent> markdownEditor,
            Pair<NoteEditorCtrl, Parent> noteEditor,
            Pair<SidebarCtrl, Parent> sidebarEditor,
            Pair<FilesCtrl, Parent> filesEditor
    )
    {
        this.primaryStage = primaryStage;

        this.noteEditorCtrl = noteEditor.getKey();
        this.noteEditorEnglish = new Scene(noteEditor.getValue());

        this.markdownEditorCtrl = markdownEditor.getKey();

        this.sidebarCtrl = sidebarEditor.getKey();
        this.filesCtrl = filesEditor.getKey();

        noteEditorCtrl.initialize(sidebarEditor.getValue(), markdownEditor.getValue(), filesEditor.getValue());
        markdownEditorCtrl.initialize();
        sidebarCtrl.initialize(markdownEditorCtrl, filesCtrl);

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