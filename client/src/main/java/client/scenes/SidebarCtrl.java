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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SidebarCtrl {

    private final ServerUtils server;
    private long selectedNoteId;
    private Collection defaultCollection;
    private List<NoteTitle> noteTitles = new ArrayList<>();

    @FXML
    public VBox noteContainer;
    private MainCtrl mainCtrl;

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
     * initializer for the SidebarCtrl object.
     * @param mainCtrl The mainCtrl to execute actions outside the sidebar.
     */
    public void initialize(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Refresh function to activate a GET request to the server to receive all note titles.
     * Functionality will be used when pressed on the refresh button in the GUI.
     */
    public void refresh() {
        noteContainer.getChildren().clear();
        loadNoteTitles(server.getNoteTitles());
    }

    /**
     * This method updates the noteTitles field and calls the loadNewNoteTags method from mainCtrl.
     * Then it calls the applyFilters method from main, which displays the NoteTitles of
     * the Notes that have the selected tags.
     * @param titles The new list of titles that is stored (before filtering)
     */
    public void loadNoteTitles(List<NoteTitle> titles){
        noteTitles = titles;
        mainCtrl.loadNewNoteTags(titles.stream().map(x -> x.getId()).toList());
        mainCtrl.applyFilters();
    }

    /**
     * This method is used to display the items in the sidebar.
     * @param ids the ids of the noteTitles which should be displayed.
     */
    public void displayNoteTitles(List<Long> ids){
        List<NoteTitle> toDisplay = noteTitles.stream().filter(x -> ids.contains(x.getId())).toList();

        for (NoteTitle title : toDisplay) {
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
     * change title on the fly, from the note editor, so you don't have to refresh every time.
     * @param id The id of the note to change the title of.
     * @param newTitle The new title for this note.
     */
    public void updateTitle(long id, String newTitle) {
        for (var titleBoxes : noteContainer.getChildren()) {
            if (titleBoxes.getId().equals(id + "")) {
                assert titleBoxes instanceof VBox;
                VBox titleVBox = (VBox) titleBoxes;
                Label text = (Label) titleVBox.getChildren().getFirst();
                text.setText(newTitle);
            }
        }
    }

    /**
     * Creates a new title that is unique to the other title in the format "New note: #"
     * 
     * @param input an integer that indicates what the first default title should be (usually 1)
     * 				every other title will have a higher number.
     * @return an integer which increments the current highest "New note: #", so that every note is unique in title.
     */
    private int createDefaultTitle(int input) {
        List<NoteTitle> notes = server.getNoteTitles();
        if (!notes.isEmpty()) {
        	boolean correctTitle = true;
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
     * or the collection of the selected note. 
     * Afterward selects the newly created note (last note).
     */
    public void createNote() {
        Collection collection = defaultCollection;
        if (getSelectedNoteId() > 0) {
            collection = server.getNoteById(getSelectedNoteId()).collection;
        }

        int input = createDefaultTitle(1);
        Note newNote = new Note("New note: " + input, "Edit content here.", collection);

        addNote(newNote);
        mainCtrl.recordAdd(selectedNoteId);
    }

    /**
     * Adds a specific note to the database.
     * The ID will not remain the same!
     * @param note The specific note object to add.
     * @return The new ID of the note OR -1 if the note could not be added.
     */
    public long addNote(Note note) {
        if (server.existsNoteById(note.id)) {
            return -1; // note already exists
        }

        note.createdAt = new Date();
        server.addNote(note);
        mainCtrl.addTags(note);
        refresh();
        selectedNoteId = Integer.parseInt(noteContainer.getChildren().getLast().getId());
        noteClick(selectedNoteId);
        return selectedNoteId;
    }
    
    /**
     * Deletes the selected note, and if there is no note -> 
     * creates a new default note.signaturized
     * Afterward selects the first note.
     */
    public void deleteSelectedNote() {
        deleteNoteById(selectedNoteId, true);
    }

    /**
     * Deletes the note with specified id, and with options to reverse so it can be stored in the undo history.
     * A normal delete needs to be reversible with the undo command.
     * But if it deletes a note as part of an undo action it does not need to record it again.
     * @param id The ID of the note that needs to be deleted.
     * @param isReversible The option to record the action so it can be reverse with an undo action.
     */
    public void deleteNoteById(long id, boolean isReversible) {
        if (!server.existsNoteById(id)) {
            return; // note didn't exist anymore //
        }

        if (id <= 0) {
            return;
        }

        if (server.getAllNotes().size() < 2){
            createNote();
        }

        Note note = server.getNoteById(id);

        server.deleteNote(note);
        mainCtrl.deleteTags(note.id);
        if (isReversible) {
            mainCtrl.recordDelete(note);
        }


        refresh();
        selectedNoteId = Integer.parseInt(noteContainer.getChildren().getFirst().getId());
        noteClick(selectedNoteId);
    }

    /**
     * Note click function for action when a specific note in the sidebar is clicked
     * intended behaviour is that the note contents opens.
     * @param id identifier that is linked to a specific note that corresponds to the servers note ID.
     */
    public void noteClick(long id) {
        for (var titleBoxes : noteContainer.getChildren()) {
            if (titleBoxes.getId().equals(id + "")) {
                titleBoxes.setStyle("-fx-background-color: #98c1d9");
            } else {
                titleBoxes.setStyle("-fx-background-color: transparent");
            }
        }
        selectedNoteId = id;
        mainCtrl.updateNote(id);
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
        return selectedNoteId;
    }

    public long getNextNoteId(long id) {
        boolean isNext = false;
        for (var titleBoxes : noteContainer.getChildren()) {
            if (isNext) {
                return Long.parseLong(titleBoxes.getId());
            }
            if (titleBoxes.getId().equals(id + "")) {
                isNext = true;
            }
        }
        return -1;
    }

    public long getPreviousNoteId(long id) {
        long previousNoteId = -1;
        for (var titleBoxes : noteContainer.getChildren()) {
            if (titleBoxes.getId().equals(id + "")) {
                return previousNoteId;
            }
            previousNoteId = Long.parseLong(titleBoxes.getId());
        }
        // never reached
        return previousNoteId;
    }

}
