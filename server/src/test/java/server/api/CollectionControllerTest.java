package server.api;

import commons.Collection;
import commons.Note;
import commons.NoteTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

        note1 = new Note("NoteTitle 1", "Content 1", collection1);
        noteRepo.save(note1);

        collection1 = new Collection("Name 1", "Title 1");
        collection1.notes.add(note1);
        repo.save(collection1);

        note2 = new Note("NoteTitle 2", "Content 2", collection2);
        noteRepo.save(note2);
        note3 = new Note("NoteTitle 3", "Content 3", collection2);
        noteRepo.save(note3);

        collection2 = new Collection("Name 2", "Title 2");
        collection2.notes.add(note2);
        collection2.notes.add(note3);
        repo.save(collection2);

        collection3 = new Collection("Name 3", "Title 3");

        controller = new CollectionController(repo, noteRepo);
    }


    @Test
    public void getAllCollectionsTest() {
        List<Collection> allCollections = controller.getAllCollections();
        List<Collection> expected = List.of(collection1, collection2);

        assertEquals(expected, allCollections);
    }

//    @Test
//    public void getNotesFromCollectionTest() {
//        List<Note> noteCol1 = controller.getNotesInCollection(collection1.id);
//        List<Note> expected = List.of(note1);
//
//        assertEquals(expected, noteCol1);
//
//        List<Note> noteCol2 = controller.getNotesInCollection(collection2.id);
//        List<Note> expected2 = List.of(note2, note3);
//
//        assertEquals(expected2, noteCol2);
//    }
//
//    @Test
//    public void addCollectionTest() {
//        collection3.id = 2;
//        controller.add(collection3);
//        List<Collection> collections = controller.getAllCollections();
//        List<Collection> expected = List.of(collection1, collection2, collection3);
//
//        assertEquals(expected, collections);
//    }
//
//    @Test
//    public void removeCollectionTest() {
//        Long tempId = collection1.id;
//        controller.remove(collection1.id);
//
//        List<Note> noteCol1 = controller.getNotesInCollection(tempId);
//
//        assertEquals(null, noteCol1);
//    }
//
//    @Test
//    public void removeCollectionTest2() {
//        controller.remove(collection2.id);
//
//        List<Collection> collections = controller.getAllCollections();
//        List<Collection> expected = List.of(collection1);
//        assertEquals(expected, collections);
//    }

    @Test
    public void successfulSearchCollectionTestMatchAll(){
        NoteTitle nt1 = new NoteTitle(note1.title, note1.id);
        List<NoteTitle> expected = new ArrayList<NoteTitle>();
        expected.add(nt1);

        List<NoteTitle> result = controller.searchNotes(String.valueOf(collection1.id), "NoteTitle", "true", "0");
        assertEquals(expected, result);
    }

    @Test
    public void successfulSearchCollectionTestMatchAny(){
        NoteTitle nt1 = new NoteTitle(note1.title, note1.id);
        List<NoteTitle> expected = new ArrayList<NoteTitle>();
        expected.add(nt1);

        List<NoteTitle> result = controller.searchNotes(String.valueOf(collection1.id), "Notetitle and random", "false", "0");
        assertEquals(expected, result);
    }
}

