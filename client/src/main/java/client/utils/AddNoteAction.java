package client.utils;

public class AddNoteAction extends NoteAction{
    private final long noteId;

    public AddNoteAction(long noteId) {
        super(ActionType.ADD);
        this.noteId = noteId;
    }

    public long getId() {
        return noteId;
    }
}
