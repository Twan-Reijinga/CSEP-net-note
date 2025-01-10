package client.utils;

import client.scenes.SidebarCtrl;
import commons.Note;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.Stack;

/**
 * ShortcutHandler is a utility class for managing keyboard shortcuts
 * across any JavaFX Scene. It is independent of specific scenes or controllers.
 */
public class ShortcutHandler {
    private final SidebarCtrl sidebarCtrl;
    private final Stack<NoteAction> actionStack = new Stack<>();
    private final HashMap<Long, Long> idMapping = new HashMap<>();

    private enum ChangeType {
        DOWN,
        UP
    }

    /**
     * Initialization for the ShortcutHandler.
     * @param sidebarCtrl The sidebar controller to change things in the sidebar.
     */
    public ShortcutHandler(SidebarCtrl sidebarCtrl) {
        this.sidebarCtrl = sidebarCtrl;
    }

    /**
     * attaches a scene to record key presses from to detect shortcuts.
     * @param scene The scene to record key presses from.
     */
    public void attach(Scene scene) {
        scene.setOnKeyPressed(this::handleKeyPresses);
    }

    /**
     * Handler for when a key is pressed to check if it needs to trigger a shortcut action.
     * @param event The key press that is detected.
     */
    private void handleKeyPresses(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.Z) {
            this.undo();
        } else if (event.isControlDown() && event.getCode() == KeyCode.R) {
            sidebarCtrl.refresh();
        } else if (event.isControlDown() && event.getCode() == KeyCode.DOWN) {
            this.noteSelectionChange(ChangeType.DOWN);
        } else if (event.isControlDown() && event.getCode() == KeyCode.UP) {
            this.noteSelectionChange(ChangeType.UP);
        }
    }

    /**
     * Method for undoing the last action of adding or deleting notes.
     * Adding the note back if the current action was deleting a note.
     * Deleting the note if the current action was creating a new note.
     * Tracks full history of all actions that happened since the beginning of opening the application using a stack.
     * Removes actions from history after it has undone the action.
     * References old id of deleted note to newly created note if applicable.
     */
    private void undo() {
        if (actionStack.isEmpty()) {
            return;
        }

        NoteAction action = actionStack.pop();
        NoteAction.ActionType actionType = action.getActionType();

        switch (actionType) {
            case ADD -> undoAdd(((AddNoteAction) action));
            case DELETE -> undoDelete((DeleteNoteAction) action);
        }
    }

    /**
     * Undo method for when the last action in the stack was adding a note.
     * From the given ADD action it tries to find the corresponding new Id in the mappings.
     * If not found it will use the current id as default
     * @param action The noteAction of type ADD with the corresponding noteId.
     */
    private void undoAdd(AddNoteAction action) {
        long id = idMapping.getOrDefault(action.getId(), action.getId());
        sidebarCtrl.deleteNoteById(id, false);
    }

    /**
     * Undo method for when the last action in the stack was deleting a note.
     * @param action The noteAction of type DELETE with the corresponding note with contents.
     */
    private void undoDelete(DeleteNoteAction action) {
        Note note = action.getNote();
        long newNoteId = sidebarCtrl.addNote(note);
        if (newNoteId != -1) {
            idMapping.put(action.getNote().id, newNoteId);
        }
    }

    private void noteSelectionChange(ChangeType changeType) {
        long currentNoteId = sidebarCtrl.getSelectedNoteId();

        switch (changeType) {
            case DOWN:
                long nextNoteId = sidebarCtrl.getNextNoteId(currentNoteId);
                if (nextNoteId != -1) {
                    sidebarCtrl.noteClick(nextNoteId);
                }
                break;
            case UP:
                long previousNoteId = sidebarCtrl.getPreviousNoteId(currentNoteId);
                if (previousNoteId != -1) {
                    sidebarCtrl.noteClick(previousNoteId);
                }
                break;
        }
    }

    /**
     * record action of adding a newly created note, to store in the history of the action stack.
     * @param noteId The id of the note that was created.
     */
    public void recordAdd(Long noteId) {
        actionStack.push(new AddNoteAction(noteId));
    }

    /**
     * record action of deleting a existing note, to store in the history of the action stack.
     * @param note The note with title and contents.
     */
    public void recordDelete(Note note) {
        actionStack.push(new DeleteNoteAction(note));
    }

}
