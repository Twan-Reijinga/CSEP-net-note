package client.handlers;

import static org.junit.jupiter.api.Assertions.*;

import client.config.Config;
import client.utils.ServerUtilsRepository;
import commons.Collection;
import commons.Note;
import commons.NoteTags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class TagFilteringHandlerTest {

    private ServerUtilsRepository serverUtils;
    private TagFilteringHandler tagFilteringHandler;
    private Collection collection;

    private long getServerNoteId(Note note) {
        List<Note> notes = serverUtils.getAllNotes();
        for (Note serverNotes : notes) {
            if (!serverNotes.title.equals(note.title)) continue;
            if (!serverNotes.content.equals(note.content)) continue;
            return serverNotes.id;
        }
        return -1;
    }

    @BeforeEach
    void setUp() {
        serverUtils = new ServerUtilsRepository(new Config());
        collection = new Collection("name", "title");
        tagFilteringHandler = new TagFilteringHandler(serverUtils);
    }

    @Test
    void testLoadNewNoteTags() {
        List<Long> noteIds = List.of(1L, 2L);
        serverUtils.clearAll();

        for (int i =0; i < 2; i++) {
            Note note = new Note("Test Note", "Some content", collection);

            serverUtils.addNote(note);
        }

        tagFilteringHandler.loadNewNoteTags(noteIds);
        List<Long> displayedNotes = tagFilteringHandler.getNotesToDisplay();

        assertEquals(2, displayedNotes.size());
        assertTrue(displayedNotes.containsAll(List.of(1L, 2L)));
    }

    @Test
    void testDeleteNoteTags() {
        List<Long> noteIds = List.of(1L, 2L);
        serverUtils.clearAll();

        for (int i =0; i < 2; i++) {
            Note note = new Note("Test Note", "Some content", collection);

            serverUtils.addNote(note);
        }

        tagFilteringHandler.loadNewNoteTags(List.of(1L, 2L));

        tagFilteringHandler.deleteNoteTags(1L);
        List<Long> displayedNotes = tagFilteringHandler.getNotesToDisplay();

        assertEquals(1, displayedNotes.size());
        assertTrue(displayedNotes.contains(2L));
    }

    @Test
    void testAddNoteTags() {
        Note note = new Note("test", "Test note with #tag and #test tags", collection);
        serverUtils.addNote(note);
        note.id = getServerNoteId(note);
        tagFilteringHandler.addNoteTags(note);

        List<Long> displayedNotes = tagFilteringHandler.getNotesToDisplay();
        assertEquals(1, displayedNotes.size());
        assertTrue(displayedNotes.contains(1L));
    }

    @Test
    void testUpdateNoteTags() {
        Note initialNote = new Note("test", "Initial #tag #test", collection);
        tagFilteringHandler.addNoteTags(initialNote);

        Note updatedNote = new Note("test", "Updated", collection);
        List<String> removedTags = tagFilteringHandler.updateNoteTags(updatedNote);

        assertTrue(removedTags.contains("#tag"));
        List<String> availableTags = tagFilteringHandler.getAvailableTags();
        assertFalse(availableTags.contains("#tag"));
    }

    @Test
    void testGetNotesToDisplay() {
        List<Long> noteIds = List.of(1L, 2L);
        serverUtils.clearAll();

        for (int i =0; i < 2; i++) {
            Note note = new Note("Test Note", "Some content", collection);

            serverUtils.addNote(note);
        }

        tagFilteringHandler.loadNewNoteTags(noteIds);

        tagFilteringHandler.addTag("tag1");

        List<String> expectedAvailableTags = List.of("tag2");

        tagFilteringHandler.getNotesToDisplay();
        List<String> actualAvailableTags = tagFilteringHandler.getAvailableTags();

        assertEquals(expectedAvailableTags, actualAvailableTags);
    }


    @Test
    void testClearTags() {
        List<Long> noteIds = List.of(1L, 2L);
        serverUtils.clearAll();

        for (int i =0; i < 2; i++) {
            Note note = new Note("Test Note", "Some content", collection);

            serverUtils.addNote(note);
        }

        tagFilteringHandler.loadNewNoteTags(List.of(1L));

        List<Long> displayedNotes = tagFilteringHandler.getNotesToDisplay();
        assertEquals(1, displayedNotes.size());
    }

    @Test
    void testRemoveTagsIfOrphaned() {
        Note note1 = new Note("title", "Test #tag #test", collection);
        Note note2 = new Note("title", "Test #csep #test", collection);

        serverUtils.addNote(note1);
        serverUtils.addNote(note2);
        note1.id = getServerNoteId(note1);
        note2.id = getServerNoteId(note2);

        tagFilteringHandler.addNoteTags(note1);
        tagFilteringHandler.addNoteTags(note2);

        HashSet<String> removedTags = new HashSet<>(List.of("#tag"));
        List<String> orphanedTags = tagFilteringHandler.removeTagsIfOrphaned(removedTags);

        assertTrue(orphanedTags.isEmpty());

        tagFilteringHandler.deleteNoteTags(1L);
        orphanedTags = tagFilteringHandler.removeTagsIfOrphaned(removedTags);

        assertEquals(1, orphanedTags.size());
        assertTrue(orphanedTags.contains("#tag"));
    }
}
