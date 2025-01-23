package client.utils;

import java.util.Objects;

public class AddNoteAction extends NoteAction{
    private final long noteId;

    public AddNoteAction(long noteId) {
        super(ActionType.ADD);
        this.noteId = noteId;
    }

    public long getId() {
        return noteId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AddNoteAction that = (AddNoteAction) o;
        return noteId == that.noteId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), noteId);
    }

    @Override
    public String toString() {
        return "AddNoteAction{" +
                "noteId=" + noteId +
                '}';
    }
}
