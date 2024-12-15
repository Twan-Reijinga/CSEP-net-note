package server.api;

import commons.Collection;
import commons.Note;
import commons.NoteTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.SearchService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchControllerTest {

    private Collection collection1;
    private Collection collection2;

    private Note note1;
    private Note note2;
    private Note note3;

    private CollectionController controller;
    private SearchController searchController;

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

        controller = new CollectionController(repo, noteRepo);
        searchController = new SearchController(new SearchService(repo));
    }

    @Test
    public void successfulSearchCollectionTestMatchAll(){
        NoteTitle nt1 = new NoteTitle(note1.title, note1.id);
        List<NoteTitle> expected = new ArrayList<NoteTitle>();
        expected.add(nt1);

        List<NoteTitle> result = searchController.searchNotes(String.valueOf(collection1.id),
                                                    "Notettl", "true", "0").getBody();
        assertEquals(expected, result);
    }

    @Test
    public void successfulSearchCollectionTestMatchAny(){
        NoteTitle nt1 = new NoteTitle(note1.title, note1.id);
        List<NoteTitle> expected = new ArrayList<NoteTitle>();
        expected.add(nt1);

        List<NoteTitle> result = searchController.searchNotes(String.valueOf(collection1.id),
                                                    "Notetitle and random", "false", "0").getBody();
        assertEquals(expected, result);
    }
}
