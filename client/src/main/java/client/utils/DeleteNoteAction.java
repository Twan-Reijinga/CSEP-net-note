package client.utils;

import commons.Note;

import java.util.Objects;

public class DeleteNoteAction extends NoteAction {
    private Note note;

    public DeleteNoteAction(Note note) {
        super(ActionType.DELETE);
        this.note = note;
    }

    public Note getNote() {
        return note;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeleteNoteAction that = (DeleteNoteAction) o;
        return Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), note);
    }

    @Override
    public String toString() {
        return "DeleteNoteAction{" + "\n" +
                "noteId=" + note.id + ",\n" +
                "noteTitle='" + note.title + "\',\n" +
                "noteContent='" + note.content + "\',\n" +
                '}';
    }
}
