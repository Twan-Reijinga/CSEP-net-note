package client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddNoteActionTest {

    private AddNoteAction action;

    @BeforeEach
    void setUp() {
        action = new AddNoteAction(1);
    }

    @Test
    void getId() {
        assertEquals(1, action.getId());
    }

    @Test
    void testEquals() {
        AddNoteAction action2 = new AddNoteAction(1);
        assertEquals(action, action2);
    }

    @Test
    void testHashCode() {
        AddNoteAction action2 = new AddNoteAction(1);
        assertEquals(action.hashCode(), action2.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("AddNoteAction{noteId=1}", action.toString());
    }
}