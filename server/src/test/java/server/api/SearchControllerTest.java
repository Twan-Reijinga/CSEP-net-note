package server.api;

import commons.Collection;
import commons.Note;
import commons.NoteTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.SearchService;
import server.services.CollectionService;
import server.services.RandomService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchControllerTest {

    private Collection collection1;
    private Collection collection2;

    private Note note1;
    private Note note2;
    private Note note3;

    private SearchController searchController;

    @BeforeEach
    public void setUp() {
        TestCollectionRepository repo = new TestCollectionRepository();
        TestNoteRepository noteRepo = new TestNoteRepository();

        collection1 = new Collection("Name 1", "Title 1");
        repo.save(collection1);
        collection2 = new Collection("Name 2", "Title 2");
        repo.save(collection2);

        note1 = new Note("NoteTitle 1", "Content 1", collection1);
        noteRepo.save(note1);

        note2 = new Note("NoteTitle 2", "Content 2", collection2);
        noteRepo.save(note2);

        note3 = new Note("NoteTitle 3", "Content 3", collection2);
        noteRepo.save(note3);

        CollectionService service = new CollectionService(new RandomService(), repo, noteRepo);
        searchController = new SearchController(new SearchService(service));
    }

    @Test
    public void successfulSearchCollectionTestMatchAll(){
        NoteTitle nt1 = new NoteTitle(note1.title, note1.id);
        List<NoteTitle> expected = new ArrayList<NoteTitle>();
        expected.add(nt1);

        List<NoteTitle> result = searchController.searchNotes("Notettl", "true", "Both", collection1.id).getBody();
        assertEquals(expected, result);
    }

    @Test
    public void successfulSearchCollectionTestMatchAny(){
        NoteTitle nt1 = new NoteTitle(note1.title, note1.id);
        List<NoteTitle> expected = new ArrayList<NoteTitle>();
        expected.add(nt1);

        List<NoteTitle> result = searchController.searchNotes("Notetitle and random", "false", "Both", collection1.id).getBody();
        assertEquals(expected, result);
    }
}
