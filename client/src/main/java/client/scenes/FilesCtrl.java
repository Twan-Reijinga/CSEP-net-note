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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Base64;
import java.util.List;

public class FilesCtrl {
    private final ServerUtils server;
    private final MainCtrl main;

    @FXML
    public VBox filesContainer;

    @Inject
    public FilesCtrl(ServerUtils server, MainCtrl main) {
        this.server = server;
        this.main = main;
    }

    /**
     * Resets the HBox with all added files
     */
    public void refresh() {
        filesContainer.getChildren().clear();
        Note note = server.getNoteById(main.getSelectedNoteId());
        List<EmbeddedFile> files = server.getAllFilesFromNote(note);
        for (EmbeddedFile currentFile : files) {
            Label label = new Label(currentFile.title);
            label.setCursor(Cursor.HAND);
            label.setStyle("-fx-text-fill: blue");
            label.setOnMouseClicked(event -> downloadFile(currentFile.note.id, currentFile.id));
            Button remove = new Button("Remove");
            remove.setOnAction(event -> deleteFile(currentFile.note.id, currentFile.id));
            Button edit = new Button("Edit");
            edit.setOnAction(event -> editTitle(currentFile.note.id, currentFile.id));
            HBox buttons = new HBox(10,edit, remove);
            VBox wrapper = new VBox(label, buttons);
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
            //A note must be selected
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
        for (EmbeddedFile file : server.getAllFilesFromNote(note)) {
            if (file.title.equals(addedFile.getName())) {
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
        popupStage.setTitle("Edit Title");
        TextField newTitle = new TextField();
        newTitle.setPromptText("Enter new title");
        newTitle.setOnKeyPressed(event -> {
            if ((event.getCode() == KeyCode.ENTER)) {
                onChangeTitle(noteId, id, popupStage, newTitle, feedback);
            } else {feedback.setText("");}});

        Label oldTitle = new Label("Previous title: " + server.getFileFromNote(noteId, id).title);
        oldTitle.setMaxWidth(290);
        Button button = new Button("Change Title");

        button.setOnAction(event -> {onChangeTitle(noteId, id, popupStage, newTitle, feedback);});
        HBox titleFeedback = new HBox(10,button, feedback);
        VBox layout = new VBox (5, newTitle, oldTitle, titleFeedback);
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
        if (!title.isEmpty()) {
            String prevTitle = server.getFileFromNote(noteId, id).title;
            String[] prevList = prevTitle.split("\\.");
            if(!title.split("\\.")[title.split("\\.").length - 1].equals(prevList[prevList.length - 1])) {
                title += "." + prevList[prevList.length - 1];
            }
            if (!server.getAllTitlesFromNote(noteId).contains(title)) {
                changeTitle(title, noteId, id);
                main.showMessage("Title successfully changed." +
                        "\nFrom: " + prevTitle +
                        "\nTo: " + title, false);
                popupStage.close();
            }else {
                newTitle.clear();
                feedback.setText("Title already exists");
                feedback.setStyle("-fx-text-fill: red;");
            }
        }else{
            feedback.setText("Please provide a title");
            feedback.setStyle("-fx-text-fill: red");
        }
    }

    /**
     * Changes the title to the provided title passes along to server
     * @param title the new title that the user made
     * @param noteId the id of the selected note
     * @param id the id of the selected file
     */
    public void changeTitle(String title, long noteId, long id) {
        EmbeddedFile file = server.getFileFromNote(noteId, id);
        file.title = title;
        server.editFileTitle(file);

        refresh();
    }

    /**
     * Decodes the file and writes it to a file, as path the filename
     * @param noteId the id of the selected note
     * @param id the id of the selected file
     */
    public void downloadFile(long noteId, long id){
        EmbeddedFile file = server.getFileFromNote(noteId, id);
        byte[] thisFile = Base64.getDecoder().decode(file.file);
        try {
            String filePath = System.getProperty("user.home") + "/Downloads/" + file.title;
            FileOutputStream output = new FileOutputStream(filePath);
            output.write(thisFile);
            main.showMessage("File downloaded successfully to:\n" + filePath, false);
        }catch (IOException e) {
            main.showMessage("Error downloading file:\n" + file.title, true);
            throw new RuntimeException(e);
        }
    }
}
