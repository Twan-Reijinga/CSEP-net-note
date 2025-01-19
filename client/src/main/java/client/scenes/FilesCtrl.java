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
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Base64;
import java.util.List;

@SuppressWarnings("checkstyle:Indentation")
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

    public void selectFile() {
        if (main.getSelectedNoteId() == -1) {
            return;     //A note must be selected
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(null);
        addFile(file);
    }

    /**
     * Adds file to be stored in the database
     *  File is saved in base64, this can store all filetypes and large files (also works with JSON)
     * @param addedFile the selected file out of the
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
}
