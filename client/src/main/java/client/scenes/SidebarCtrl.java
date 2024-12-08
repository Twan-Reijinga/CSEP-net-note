package client.scenes;

import commons.Note;
import commons.NoteTitle;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;


public class SidebarCtrl {

    private final ServerUtils server;
    private long selectedNoteId;

    @FXML
    public VBox noteContainer;

    /**
     * Sidebar control constructor for functionality behind the sidebar UI element.
     * @param server Server utilities for requests and functionality dependent on the server.
     */
    @Inject
    public SidebarCtrl(ServerUtils server) {
        this.server = server;
        selectedNoteId = -1;
    }

    /**
     * Refresh function to activate a GET request to the server to receive all note titles.
     * Functionality will be used when pressed on the refresh button in the GUI.
     */
    public void refresh() {
        noteContainer.getChildren().clear();

        List<NoteTitle> titles = server.getNoteTitles();
        for (NoteTitle title : titles) {
            Label label = new Label(title.getTitle());
            VBox wrapper = new VBox(label);
            wrapper.setId(title.getId() + "");
            wrapper.setPadding(new Insets(5, 10, 5, 10));
            wrapper.setOnMouseClicked(event -> {
                long id = Long.parseLong(wrapper.getId()); // Get the VBox's ID
                noteClick(id);             // Call a function, passing the ID
            });
            noteContainer.getChildren().add(wrapper);
        }
    }

    public void addNote() {
        Note note1 = server.getAllNotes().getFirst();
        note1.id = 0; // Update the ID, the database will make one automatically
        server.addNote(note1);
    }

    public void deleteNote() {
        Note note1 = server.getAllNotes().getFirst();
        System.out.println(server.getAllNotes());
        server.deleteNote(note1);
    }

    /**
     * Note click function for action when a specific note in the sidebar is clicked
     * intended behaviour is that the note contents opens.
     * @param id identifier that is linked to a specific note that corresponds to the servers note ID.
     */
    private void noteClick(Long id) {
        for (var titleBoxes : noteContainer.getChildren()) {
            if (titleBoxes.getId().equals(id + "")) {
                titleBoxes.setStyle("-fx-background-color: #98c1d9");
            } else {
                titleBoxes.setStyle("-fx-background-color: transparent");
            }
        }
        selectedNoteId = id;
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
    public Long getSelectedNoteId() {
        return selectedNoteId;
    }
}
