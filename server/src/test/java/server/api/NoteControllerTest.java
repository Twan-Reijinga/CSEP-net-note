package server.api;

import commons.Collection;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.CollectionService;
import server.services.NoteService;

import java.util.ArrayList;
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

        // FIXME: invalid service, collection service cannot be null
        //  it requires fixing the tests (because even here, all notes have collection null which is not possible)
        NoteService service = new NoteService(repo, null);

        controller = new NoteController(repo, service);
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

    @Test
    public void addNoteTest() {
        note3.id = 2;
        note3.collection = new Collection();
        controller.add(note3);
        List<Note> added = controller.getAllNotes();
        List<Note> expected = List.of(note1, note2, note3);

        assertEquals(expected, added);
    }

    @Test
    public void addWrongIdTest() {
        note3.id = 0;
        controller.add(note3);
        List<Note> notAdded = controller.getAllNotes();
        List<Note> expected = List.of(note1, note2);

        assertEquals(expected, notAdded);
    }

    @Test
    public void deleteNoteTest() {
        controller.delete(note2.id);
        List<Note> removed = controller.getAllNotes();
        List<Note> expected = List.of(note1);

        assertEquals(expected, removed);
    }

    @Test
    public void deleteEmptyRepTest() {
        controller.delete(note1.id);
        controller.delete(note2.id);
        controller.delete(note3.id);

        List<Note> removed = controller.getAllNotes();
        List<Note> expected = new ArrayList<>();
        assertEquals(expected, removed);
    }


}
