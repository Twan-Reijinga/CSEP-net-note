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
import commons.NoteTitle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.List;

public class MainCtrl {

    private Stage primaryStage;
    private NoteEditorCtrl noteEditorCtrl;
    private Scene noteEditor;

    private MarkdownEditorCtrl markdownEditorCtrl;
    private Scene markdownEditor;

    private SidebarCtrl sidebarCtrl;
    private Scene sidebar;
    private ServerUtils serverUtils;

    public void initialize(
            Stage primaryStage,
            Pair<NoteEditorCtrl, Parent> noteEditor,
            Pair<MarkdownEditorCtrl, Parent> markdownEditor,
            Pair<SidebarCtrl, Parent> sidebarEditor
    )
    {
        this.primaryStage = primaryStage;

        this.serverUtils = new ServerUtils();

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

    /** This method sends data for the creation of a get request to the server and passes the returned data
     *  to the sidebar, forcing it to update itself.
     *
     * @param text  text for the search request
     * @param collectionId id of collection to search
     * @param matchAll option to match all keywords or not
     * @param whereToSearch option to search specific parts of a note
     */
    public void sendSearchRequest(String text, long collectionId, boolean matchAll, String whereToSearch){
        List<NoteTitle> results = serverUtils.searchNotesInCollection(collectionId, text, matchAll, whereToSearch);
        updateSideBar(results);
    }

    public void updateSideBar(List<NoteTitle> titles){
        sidebarCtrl.loadSideBar(titles);
    }

    public void refreshSideBar(){ sidebarCtrl.refresh(); }
}