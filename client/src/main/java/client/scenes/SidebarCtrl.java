package client.scenes;

import commons.Collection;
import commons.Note;
import commons.NoteTitle;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Date;
import java.util.List;


public class SidebarCtrl {

    private final ServerUtils server;
    private long selectedNoteId;
    private Collection defaultCollection;

    @FXML
    public VBox noteContainer;

    /**
     * Sidebar control constructor for functionality behind the sidebar UI element.
     * @param server Server utilities for requests and functionality dependent on the server.
     */
    @Inject
    public SidebarCtrl(ServerUtils server, Collection defaultCollection) {
        this.server = server;
        this.defaultCollection = defaultCollection;
        selectedNoteId = -1;

        this.defaultCollection = new Collection("default", "Default Collection");
        this.defaultCollection.id = 1;
        this.defaultCollection.isDefault = true;
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

    /**
     * Creates a new title that is unique to the other title in the format "New note: #"
     * 
     * @param input an integer that indicates what the first default title should be (usually 1)
     * @return an integer which is one above the current highest "New note: #", so that every note is unique in title.
     */
    private int createDefaultTitle(int input) {
        List<NoteTitle> notes = server.getNoteTitles();
        if (!notes.isEmpty()) {
        	boolean correctTitle = false;
        	for (NoteTitle currentNote : notes) {
        		correctTitle = true;
        		char[] chars = currentNote.getTitle().substring(10).toCharArray();
        		if (chars.length == 0) {
        			correctTitle = false;
        		}
        		for (char currentChar : chars) {
        			if (!Character.isDigit(currentChar))	
        				correctTitle = false;
        		}
        		if (correctTitle == true && currentNote.getTitle().startsWith("New note: ")) {
        			int tempInt = Integer.parseInt(currentNote.getTitle().split(" ")[2]);
        			if (tempInt >= input) {
        				input = tempInt + 1;
        			}
                }
            }
        }
        return input;
    }

    /**
     * Adds a default note to the database with a unique title, unique id, content and a default collection, 
     * or the collection of the selected note. Afterwards selects the newly created note.
     */
    public void addNote() {
        Collection collection = defaultCollection;
        if (getSelectedNoteId() > 0) {
            collection = server.getNoteById(getSelectedNoteId()).collection;;
        }
        int input = createDefaultTitle(1);
        Note newNote = new Note("New note: " + input, "Edit content here.", collection);
        newNote.createdAt = new Date();
        server.addNote(newNote);
        refresh();
        selectedNoteId = Integer.parseInt(noteContainer.getChildren().getLast().getId());
        noteClick(selectedNoteId);
    }
    
    /**
     * Deletes the selected note, and if there is no note -> creates a new default one.
     * Afterwards selects the first note.
     */
    public void deleteNote() {
        if (getSelectedNoteId() > 0) {
            Note note1 = server.getNoteById(getSelectedNoteId());
            server.deleteNote(note1);
            selectedNoteId = -1;
            if (server.getAllNotes().isEmpty()){
                addNote();
            }
            refresh();
            selectedNoteId = Integer.parseInt(noteContainer.getChildren().getFirst().getId());
            noteClick(selectedNoteId);
        }
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
