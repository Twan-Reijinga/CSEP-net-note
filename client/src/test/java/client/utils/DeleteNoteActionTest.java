package client.utils;

import commons.Collection;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeleteNoteActionTest {

    private DeleteNoteAction action;
    private Note note;
    private Collection collection;

    @BeforeEach
    void setUp() {
        collection = new Collection("name", "title");
        note = new Note("title", "content", collection);
        action = new DeleteNoteAction(note);
    }

    @Test
    void getNote() {
        assertEquals(note, action.getNote());
    }

    @Test
    void testEquals() {
        DeleteNoteAction action2 = new DeleteNoteAction(note);
        assertEquals(action, action2);
    }

    @Test
    void testHashCode() {
        DeleteNoteAction action2 = new DeleteNoteAction(note);
        assertEquals(action.hashCode(), action2.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("DeleteNoteAction{\n" +
                "noteId=0,\n" +
                "noteTitle='title',\n" +
                "noteContent='content',\n" +
                "}", action.toString());
    }
}