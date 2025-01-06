package client.utils;

import commons.Note;

public class DeleteNoteAction extends NoteAction {
    private Note note;

    public DeleteNoteAction(Note note) {
        super(ActionType.DELETE);
        this.note = note;
    }

    public Note getNote() {
        return note;
    }
}
