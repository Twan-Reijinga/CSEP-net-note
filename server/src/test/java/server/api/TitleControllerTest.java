package server.api;

import commons.Collection;
import commons.Note;
import commons.NoteTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TitleControllerTest {

    private TestNoteRepository noteRepo;
    private TestCollectionRepository collectionRepo;
    private Note note1;
    private Note note2;
    private Note note3;
    private Collection collection;
    private Collection collection2;

    private TitleController controller;


    @BeforeEach
    public void setUp() {
        noteRepo = new TestNoteRepository();
        collectionRepo = new TestCollectionRepository();

        collection = new Collection("first collection", "title");
        collectionRepo.save(collection);
        collection2 = new Collection("second collection", "title");
        collectionRepo.save(collection);

        note1 = new Note("Title 1", "Content 1", collection);
        noteRepo.save(note1);
        note2 = new Note("Title 2", "Content 2", collection2);
        noteRepo.save(note2);
        note3 = new Note("Title 3", "Content 3", collection2);
        noteRepo.save(note3);



        controller = new TitleController(noteRepo, collectionRepo);
    }

    @Test
    void getAllTitles() {
        List<NoteTitle> titles = controller.getAllTitles(null).getBody();
        List<NoteTitle> expected = List.of(
                NoteTitle.fromNote(note1),
                NoteTitle.fromNote(note2),
                NoteTitle.fromNote(note3));
        assertEquals(titles, expected);
    }

    @Test
    void getAllTitlesForCollection() {
        List<NoteTitle> titles = controller.getAllTitles(1L).getBody();
        List<NoteTitle> expected = List.of(
                NoteTitle.fromNote(note1));
        assert titles != null;
        List<String> titleStrings =
                titles.stream().map(NoteTitle::getTitle).toList();
        List<String> expectedStrings =
                expected.stream().map(NoteTitle::getTitle).toList();
        assertEquals(titleStrings, expectedStrings);
    }

    @Test
    void getTitle() {
        NoteTitle title = controller.getTitle(0L).getBody();
        assertEquals(NoteTitle.fromNote(note1), title);
    }
}