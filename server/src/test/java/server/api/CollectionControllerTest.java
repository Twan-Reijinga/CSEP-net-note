package server.api;

import commons.Collection;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.CollectionService;
import server.services.RandomService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CollectionControllerTest {

    private Collection collection1;
    private Collection collection2;
    private Collection collection3;

    private Note note1;
    private Note note2;
    private Note note3;

    private CollectionController controller;

    @BeforeEach
    public void setUp() {
        TestCollectionRepository repo = new TestCollectionRepository();
        TestNoteRepository noteRepo = new TestNoteRepository();
        CollectionService s = new CollectionService(new RandomService(), repo);

        collection1 = new Collection("Name 1", "Title 1");
        note1 = new Note("NoteTitle 1", "Content 1", collection1);
        noteRepo.save(note1);

        repo.save(collection1);


        collection2 = new Collection("Name 2", "Title 2");
        collection3 = new Collection("Name 3", "Title 3");
        note2 = new Note("NoteTitle 2", "Content 2", collection2);
        noteRepo.save(note2);
        note3 = new Note("NoteTitle 3", "Content 3", collection2);
        noteRepo.save(note3);

        repo.save(collection2);


        controller = new CollectionController(repo, noteRepo, s);
    }


    @Test
    public void getAllCollectionsTest() {
        List<Collection> allCollections = controller.getAllCollections();
        List<Collection> expected = List.of(collection1, collection2);

        assertEquals(expected, allCollections);
    }

    @Test
    public void getNotesFromCollectionTest() {
        List<Note> noteCol1 = controller.getNotesInCollection(collection1.id);
        List<Note> expected = List.of(note1);

        assertEquals(expected, noteCol1);

        List<Note> noteCol2 = controller.getNotesInCollection(collection2.id);
        List<Note> expected2 = List.of(note2, note3);

        assertEquals(expected2, noteCol2);
    }

    @Test
    public void addCollectionTest() {
        collection3.id = UUID.randomUUID();
        controller.add(collection3);
        List<Collection> collections = controller.getAllCollections();
        List<Collection> expected = List.of(collection1, collection2, collection3);

        assertEquals(expected, collections);
    }

    @Test
    public void removeCollectionTest() {
        UUID tempId = collection1.id;
        controller.remove(collection1.id);

        List<Note> noteCol1 = controller.getNotesInCollection(tempId);

        assertNull(noteCol1);
    }

    @Test
    public void removeCollectionTest2() {
        controller.remove(collection2.id);

        List<Collection> collections = controller.getAllCollections();
        List<Collection> expected = List.of(collection1);
        assertEquals(expected, collections);
    }

}

