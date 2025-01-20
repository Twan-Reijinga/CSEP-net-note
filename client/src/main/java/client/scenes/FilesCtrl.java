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
    public HBox filesContainer;

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
            label.setMaxWidth(100);
            label.setStyle("-fx-text-fill: blue");
            label.setOnMouseClicked(event -> downloadFile(currentFile.note.id, currentFile.id));
            Button remove = new Button("⮾");
            remove.setOnAction(event -> deleteFile(currentFile.note.id, currentFile.id));
            Button edit = new Button("&");
            edit.setOnAction(event -> editTitle(currentFile.note.id, currentFile.id));
            HBox wrapper = new HBox(label, edit, remove);
            wrapper.setPadding(new Insets(10, 5, 10, 5));
            wrapper.setOnMouseClicked(event -> {
            });
            filesContainer.getChildren().add(wrapper);
        }
    }

    /**
     * Opens a file explorer to select a file from you computer
     */
    public void selectFile() {
        if (main.getSelectedNoteId() == -1) {
            return;     //A note must be selected
        }
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
            File file = fileChooser.showOpenDialog(null);
            addFile(file);
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
                return;
            }
        }

        String fileString;
        try (FileInputStream input = new FileInputStream(addedFile)) {
            byte[] fileBytes = new byte[(int) addedFile.length()];
            input.read(fileBytes);
            fileString = Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        EmbeddedFile file = new EmbeddedFile(addedFile.getName(), note, fileString);
        server.addFileToNote(file);
        refresh();
    }

    /**
     * Deletes the selected file
     * @param noteId the id of the selected note
     * @param id the unique id that defines the file
     */
    public void deleteFile(long noteId, long id) {
        server.deleteFileToNote(noteId, id);
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
        newTitle.setOnKeyPressed(event -> {feedback.setText("");});
        Label oldTitle = new Label("Previous title: " + server.getFileFromNote(noteId, id).title);
        oldTitle.setMaxWidth(250);
        Button button = new Button("Change Title");

        button.setOnAction(event -> {buttonChangeTitle(noteId, id, popupStage, newTitle, feedback);});
        VBox layout = new VBox(10, newTitle, oldTitle, feedback, button);
        Scene popupScene = new Scene(layout, 300, 150);
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
    private void buttonChangeTitle(long noteId, long id, Stage popupStage, TextField newTitle, Label feedback) {
        String title = newTitle.getText();
        if (!title.isEmpty()) {
            String[] prevTitle = server.getFileFromNote(noteId, id).title.split("\\.");
            if(!title.split("\\.")[title.split("\\.").length - 1].equals(prevTitle[prevTitle.length - 1])) {
                title += "." + prevTitle[prevTitle.length - 1];
            }
            if (!server.getAllTitlesFromNote(noteId).contains(title)) {
                changeTitle(title, noteId, id);
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
            FileOutputStream output = new FileOutputStream(file.title);
            output.write(thisFile);
            System.out.println("File downloaded");
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
