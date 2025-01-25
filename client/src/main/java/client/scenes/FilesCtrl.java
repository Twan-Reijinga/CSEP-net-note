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
import commons.EmbeddedFile;
import commons.Note;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.Base64;
import java.util.List;

public class FilesCtrl {
    private final ServerUtils server;
    private final MainCtrl main;
    private final NoteEditorCtrl noteEditorCtrl;

    @FXML
    public VBox filesContainer;

    @Inject
    public FilesCtrl(ServerUtils server, MainCtrl main, NoteEditorCtrl noteEditorCtrl) {
        this.server = server;
        this.main = main;
        this.noteEditorCtrl = noteEditorCtrl;
    }

    /**
     * Resets the HBox with all added files
     */
    public void refresh() {
        filesContainer.getChildren().clear();
        Note note = server.getNoteById(main.getSelectedNoteId());
        List<String> files = server.getAllMetadataFromNote(note);
        for (String currentFile : files) {
            Label label = new Label(currentFile.split("/")[2]);
            label.setCursor(Cursor.HAND);
            label.setStyle("-fx-text-fill: blue");
            label.setOnMouseClicked(event -> downloadFile(Long.parseLong(currentFile.split("/")[1]),
                    Long.parseLong(currentFile.split("/")[0])));
            Tooltip tooltip = new Tooltip(noteEditorCtrl.getBundle().getString("download"));
            tooltip.setShowDelay(Duration.millis(50));
            label.setTooltip(tooltip);

            Button remove = new Button(noteEditorCtrl.getBundle().getString("delete"));
            remove.setOnAction(event -> deleteFile(Long.parseLong(currentFile.split("/")[1]),
                    Long.parseLong(currentFile.split("/")[0])));
            Button edit = new Button(noteEditorCtrl.getBundle().getString("edit"));
            edit.setOnAction(event -> editTitle(Long.parseLong(currentFile.split("/")[1]),
                    Long.parseLong(currentFile.split("/")[0])));
            HBox buttons = new HBox(10, edit, remove);
            VBox wrapper = new VBox(10, label, buttons);
            wrapper.setPadding(new Insets(5, 10, 5, 10));
            filesContainer.getChildren().add(wrapper);
        }
    }

    /**
     * Opens a file explorer to select a file from you computer
     */
    public void selectFile() {
        if (main.getSelectedNoteId() == -1) {
            main.showMessage("Please select a note in the sidebar.", true);
            return;
        }
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                addFile(file);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds file to be stored in the database
     *  File is saved in base64, this can store all filetypes and large files (also works with JSON)
     * @param addedFile the selected file out of the file explorer (file name is the title + type)
     */
    public void addFile(File addedFile) {
        Note note = server.getNoteById(main.getSelectedNoteId());
        for (String file : server.getAllMetadataFromNote(note)) {
            if (file.split("/")[2].equals(addedFile.getName())) {
                main.showMessage("File was already added before.", true);
                return;
            }
        }

        String fileString;
        try (FileInputStream input = new FileInputStream(addedFile)) {
            byte[] fileBytes = new byte[(int) addedFile.length()];
            input.read(fileBytes);
            fileString = Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            main.showMessage("File was not found, try to select a valid file.", true);
            throw new RuntimeException(e);
        }
        EmbeddedFile file = new EmbeddedFile(addedFile.getName(), note, fileString);
        server.addFileToNote(file);
        main.showMessage("File was successfully added.\n" + addedFile.getName(), false);
        refresh();
    }

    /**
     * Deletes the selected file
     * @param noteId the id of the selected note
     * @param id the unique id that defines the file
     */
    public void deleteFile(long noteId, long id) {
        try {
            server.deleteFileToNote(noteId, id);
            main.showMessage("File was successfully deleted.", false);
        }catch (Exception e) {
            main.showMessage("File was not deleted, Please try again.", true);
        }
        refresh();
    }

    /**
     * Creates a pop-up to edit the note title
     * @param noteId the selected noteId
     * @param id the id of the selected file
     */
    public void editTitle(long noteId, long id) {
        Stage popupStage = new Stage();
        Label feedback = new Label("");
        Tooltip tooltip = new Tooltip(noteEditorCtrl.getBundle().getString("titleRules"));
        tooltip.setShowDelay(Duration.millis(50));
        feedback.setTooltip(tooltip);

        popupStage.setTitle(noteEditorCtrl.getBundle().getString("edit")
                + " " + noteEditorCtrl.getBundle().getString("title"));
        TextField newTitle = new TextField();
        newTitle.setPromptText(noteEditorCtrl.getBundle().getString("enterNewTitle"));
        newTitle.setOnKeyPressed(event -> {
            if ((event.getCode() == KeyCode.ENTER)) {
                onChangeTitle(noteId, id, popupStage, newTitle, feedback);
            } else {feedback.setText("");}});

        Label oldTitle = new Label(noteEditorCtrl.getBundle().getString("previousTitle")
                + server.getMetadataFromNote(noteId, id).split("/")[2]);
        oldTitle.setMaxWidth(290);
        Button button = new Button(noteEditorCtrl.getBundle().getString("changeTitle"));

        button.setOnAction(event -> {onChangeTitle(noteId, id, popupStage, newTitle, feedback);});
        HBox titleFeedback = new HBox(10,button, feedback);
        VBox layout = new VBox (10, newTitle, oldTitle, titleFeedback);
        Scene popupScene = new Scene(layout, 300, 90);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    /**
     * Creates the button that appears with pop-up
     * @param noteId the selected noteId
     * @param id the selected file id
     * @param popupStage the popup scene that appears
     * @param newTitle the text field where the user enters a new title
     * @param feedback the label that gives user feedback
     */
    private void onChangeTitle(long noteId, long id, Stage popupStage, TextField newTitle, Label feedback) {
        String title = newTitle.getText();
        if (!title.isEmpty() && (!title.contains("/") || !title.contains("(")
                || !title.contains(")") || !title.contains("{")
                || !title.contains("}") || !title.contains("\"") || !title.contains("?")
                || !title.contains("<") || !title.contains(">")
                || !title.contains(":") || !title.contains(";"))) {
            String prevTitle = server.getMetadataFromNote(noteId, id).split("/")[2];
            String[] prevList = prevTitle.split("\\.");
            if(!title.split("\\.")[title.split("\\.").length - 1].equals(prevList[prevList.length - 1])) {
                title += "." + prevList[prevList.length - 1];
            }
            if (!server.getAllTitlesFromNote(noteId).contains(title)) {
                server.editFileTitle(title, noteId, id);
                refresh();
                main.showMessage("Title successfully changed." +
                        "\nFrom: " + prevTitle +
                        "\nTo: " + title, false);
                popupStage.close();
            }else {
                newTitle.clear();
                feedback.setText(noteEditorCtrl.getBundle().getString("titleExists"));
                feedback.setStyle("-fx-text-fill: red;");
            }
        }else{
            feedback.setText(noteEditorCtrl.getBundle().getString("titleEmpty"));
            feedback.setStyle("-fx-text-fill: red");
        }
    }

    /**
     * Decodes the file and writes it to a file, as path the filename
     * @param noteId the id of the selected note
     * @param id the id of the selected file
     */
    public void downloadFile(long noteId, long id){
        String metadata = server.getMetadataFromNote(noteId, id);
        byte[] file = server.getFileFromTitle(noteId, metadata.split("/")[2]);
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(noteEditorCtrl.getBundle().getString("selectFolder"));
            directoryChooser.setInitialDirectory(
                    new File(System.getProperty("user.home") + File.separator + "Downloads"));
            File selectedFile;

            try {
                selectedFile = directoryChooser.showDialog(null);
            } catch (IllegalArgumentException e) {
                directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                selectedFile = directoryChooser.showDialog(null);
            }
            if (selectedFile == null) {
                return;
            }

            String filePath = selectedFile.getAbsolutePath() + File.separator + metadata.split("/")[2];
            FileOutputStream output = new FileOutputStream(filePath);
            output.write(file);
            main.showMessage("File downloaded successfully to:\n" + filePath, false);
        }catch (IOException e) {
            main.showMessage("Error downloading file:\n" + metadata.split("/")[2] + "\n" + e.getMessage(), true);
            throw new RuntimeException(e);
        }
    }
}
