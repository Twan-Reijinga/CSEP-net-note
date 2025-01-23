package client.scenes;

import client.config.Config;
import commons.Collection;
import commons.Note;
import commons.NoteTitle;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.*;


public class SidebarCtrl {
    private long selectedNoteId;
    private List<NoteTitle> noteTitles = new ArrayList<>();
    private UUID selectedCollectionId;

    @FXML
    public VBox noteContainer;

    @FXML
    private Pane messageContainer;

    @FXML
    private Label messageTextLabel;

    private MainCtrl mainCtrl;
    private FilesCtrl filesCtrl;

    // Injectable
    private final ServerUtils server;
    private final Config config;

    private Timer messageTimer;
    private TimerTask messageClearTask;


    /**
     * Sidebar control constructor for functionality behind the sidebar UI element.
     * @param server Server utilities for requests and functionality dependent on the server.
     * @param config a client config
     */
    @Inject
    public SidebarCtrl(ServerUtils server, Config config) {
        this.server = server;
        this.config = config;
        selectedNoteId = -1;

        messageTimer = new Timer();
    }

    /**
     * initializer for the SidebarCtrl object.
     * @param mainCtrl The mainCtrl to execute actions outside the sidebar.
     * @param filesCtrl The fileCtrl to execute actions outside the sidebar.
     */
    public void initialize(MainCtrl mainCtrl, FilesCtrl filesCtrl) {
        this.mainCtrl = mainCtrl;
        this.filesCtrl = filesCtrl;

        // Hide and remove message container from layout
        messageContainer.setVisible(false);
        messageContainer.setManaged(false);


        refresh();
        selectFirstNote();

        ServerUtils.connection.subscribe(update -> {
            if(update.note == null) return;
            Platform.runLater(this::refresh);
        });
    }

    public void showMessage(String message, boolean isError) {
        if (messageClearTask != null) {
            messageClearTask.cancel();
        }

        if (isError) messageContainer.setStyle("-fx-background-color: #FFA07A;");
        else messageContainer.setStyle("-fx-background-color: #90EE90;");

        messageTextLabel.setText(message);

        messageContainer.setVisible(true);
        messageContainer.setManaged(true);

        messageClearTask = new TimerTask() {
            public void run() {
                messageContainer.setVisible(false);
                messageContainer.setManaged(false);
                messageClearTask = null;
            }
        };

        messageTimer.schedule(messageClearTask, 3000);
    }

    /**
     * Refresh function to activate a GET request to the server to receive all note titles.
     * Functionality will be used when pressed on the refresh button in the GUI.
     */
    public void refresh() {
        List<NoteTitle> titles;
        if (selectedCollectionId == null) {
            titles = server.getNoteTitles();
        } else {
            titles = server.getNoteTitlesInCollection(selectedCollectionId);
        }

        loadNoteTitles(titles);
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
        mainCtrl.applyFiltersToSideBar();
    }

    /**
     * This method is used to display the items in the sidebar.
     * @param ids the ids of the noteTitles which should be displayed.
     */
    public void displayNoteTitles(List<Long> ids){
        noteContainer.getChildren().clear();
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
            if(title.getId() == selectedNoteId){
                wrapper.setStyle("-fx-background-color: #98c1d9");
            }
            noteContainer.getChildren().add(wrapper);
        }
    }

    public void setSelectedCollectionId(UUID collectionId) {
        selectedCollectionId = collectionId;
        refresh();
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
     * Creates a new title that is unique to the other title in the format "New note #"
     * @param collectionId an id of collection where the note will be created
     * @return a title with incremented number in "New note #", so that every note is unique in title.
     */
    private String createDefaultTitle(UUID collectionId) {
        List<NoteTitle> notes = server.getNoteTitlesInCollection(collectionId);

        int maxNoteNumber = notes.stream()
                .filter(nt -> nt.getTitle().startsWith("New note #"))
                .mapToInt(nt -> {
                    try {
                        return Integer.parseInt(nt.getTitle().replace("New note #", ""));
                    } catch (Exception e) {
                        return -1;
                    }
                })
                .max()
                .orElse(0);

        return "New note #" + (maxNoteNumber + 1);
    }

    @FXML
    public void onCreateNote() {
        // If no collection is selected, create notes in default one
        UUID destinationCollectionId = selectedCollectionId == null ?
                config.getDefaultCollectionId() : selectedCollectionId;

        createNote(destinationCollectionId);
    }

    /**
     * Adds a default note to the database with a unique title, unique id, content and a default collection,
     * or the collection of the selected note.
     * Afterward selects the newly created note (last note).
     * @param collectionID a collection id where a note will be created
     */
    public void createNote(UUID collectionID) {
        Collection collection = server.getCollectionById(collectionID);

        String title = createDefaultTitle(collectionID);
        Note newNote = new Note(title, "Edit content here.", collection);

        addNote(newNote);
        mainCtrl.recordAdd(selectedNoteId);

        showMessage("Note successfully created!", false);
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
        mainCtrl.addNewTags(note);
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
        boolean isDeleted = deleteNoteById(selectedNoteId, true);
        if (isDeleted) showMessage("Note successfully deleted!", false);
        else showMessage("Deletion cancelled.", true);
    }

    /**
     * Deletes the note with specified id, and with options to reverse so it can be stored in the undo history.
     * A normal delete needs to be reversible with the undo command.
     * But if it deletes a note as part of an undo action it does not need to record it again.
     * @param id The ID of the note that needs to be deleted.
     * @param isReversible The option to record the action so it can be reverse with an undo action.
     * @return returns if a note was deleted
     */
    public boolean deleteNoteById(long id, boolean isReversible) {
        if (id <= 0 || !server.existsNoteById(id)) {
            return false; // note already didn't exist anymore //
        }

        Note note = server.getNoteById(id);

        if (!mainCtrl.userConfirmDeletion(note.title)) {
            return false;
        }


        boolean isLastNote = server.isLastNoteInCollection(id);
        if (isLastNote) {
            createNote(note.collection.id);
        }

        server.deleteAllFilesToNote(note);
        server.deleteNote(note);
        mainCtrl.deleteTags(note.id);
        if (isReversible) {
            mainCtrl.recordDelete(note);
        }
        refresh();
        selectedNoteId = Integer.parseInt(noteContainer.getChildren().getFirst().getId());
        noteClick(selectedNoteId);

        return true;
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
        filesCtrl.refresh();
    }

    private void selectFirstNote() {
        if (noteContainer.getChildren().isEmpty()) return;

        long id = Long.parseLong(noteContainer.getChildren().getFirst().getId());
        noteClick(id);
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
