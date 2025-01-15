package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.EmbeddedFile;
import commons.Note;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.io.*;
import java.util.Base64;
import java.util.List;

public class FilesCtrl {
    private final ServerUtils server;
    private final MainCtrl main;

    @FXML
    public HBox filesContainer;

    @FXML
    public TextField filePathContainer;

    @Inject
    public FilesCtrl(ServerUtils server, MainCtrl main) {
        this.server = server;
        this.main = main;
    }

    public void refresh() {
        filesContainer.getChildren().clear();
        Note note = server.getNoteById(main.getSelectedNoteId());
        List<EmbeddedFile> files = server.getAllFilesFromNote(note);
        for (EmbeddedFile currentFile : files) {
            Label label = new Label(currentFile.title);
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
     * Adds file to be stored in the database
     *  File is saved in base64, this can store all filetypes and large files (also works with JSON)
     */
    public void addFile() {
        System.out.println(filePathContainer.getText());
        if (main.getSelectedNoteId() == -1) {return;}

        Note note = server.getNoteById(main.getSelectedNoteId());
        File thisFile = new File(filePathContainer.getText());
        filePathContainer.clear();
        String fileString;
        try (FileInputStream input = new FileInputStream(thisFile)) {
            byte[] fileBytes = new byte[(int) thisFile.length()];
            input.read(fileBytes);
            fileString = Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        EmbeddedFile file = new EmbeddedFile("Title", note, fileString);
        server.addFileToNote(file);
        refresh();
    }

    /**
     * Deletes the selected file
     */
    public void deleteFile(long noteId, long id) {
            server.deleteFileToNote(noteId, id);
            refresh();
    }

    public void editTitle(long noteId, long id) {
        EmbeddedFile file = server.getFileFromNote(noteId, id);
        String MockTitle = "MockTitle1";
        file.title = MockTitle;
        server.editFileTitle(file);

        refresh();
    }

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

    public void checkEnter(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            addFile();
        }
    }
}
