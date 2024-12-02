package server.api;

import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NoteControllerTest {
    private TestNoteRepository repo;
    private Note note1;
    private Note note2;
    private Note note3;

    private NoteController controller;

    @BeforeEach
    public void setUp(){
        repo = new TestNoteRepository();

        note1 = new Note("Title 1", "Content 1", null);
        repo.save(note1);
        note2 = new Note("Title 2", "Content 2", null);
        repo.save(note2);
        note3 = new Note("Title 3", "Content 3", null);

        controller = new NoteController(repo);
    }

    @Test
    public void getCorrectNoteByIdTest(){
        Note note = controller.getById(0).getBody();
        assertEquals(note, note1);
    }

    @Test
    public void getAllNotesTest(){
        List<Note> mapped = controller.getAllNotes();
        List<Note> expected = List.of(note1, note2);

        assertEquals(expected, mapped);
    }

}
