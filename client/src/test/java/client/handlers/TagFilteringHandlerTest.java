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
        List<NoteTags> mockNoteTags = List.of(
                new NoteTags(1L, new HashSet<>(List.of("#java", "#test"))),
                new NoteTags(2L, new HashSet<>(List.of("#python", "#test")))
        );

        serverUtils.clearAll();

        for (NoteTags noteTag : mockNoteTags) {
            Note note = new Note("Test Note", "Some content", collection);
            note.id = noteTag.getId();
            serverUtils.addNote(note);
        }

        tagFilteringHandler.loadNewNoteTags(noteIds);
        List<Long> displayedNotes = tagFilteringHandler.getNotesToDisplay();

        assertEquals(2, displayedNotes.size());
        assertTrue(displayedNotes.containsAll(List.of(1L, 2L)));
    }

    @Test
    void testDeleteNoteTags() {
        List<NoteTags> mockNoteTags = List.of(
                new NoteTags(1L, new HashSet<>(List.of("#java", "#test"))),
                new NoteTags(2L, new HashSet<>(List.of("#python", "#test")))
        );

        serverUtils.clearAll();
        for (NoteTags noteTag : mockNoteTags) {
            Note note = new Note("Test Note", "Some content", collection);
            note.id = noteTag.getId();
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
        Note note = new Note("test", "Test note with #java and #test tags", collection);
        serverUtils.addNote(note);
        note.id = getServerNoteId(note);
        tagFilteringHandler.addNoteTags(note);

        List<Long> displayedNotes = tagFilteringHandler.getNotesToDisplay();
        assertEquals(1, displayedNotes.size());
        assertTrue(displayedNotes.contains(1L));
    }

    @Test
    void testUpdateNoteTags() {
        Note initialNote = new Note("test", "Initial #java #test", collection);
        tagFilteringHandler.addNoteTags(initialNote);

        Note updatedNote = new Note("test", "Updated", collection);
        List<String> removedTags = tagFilteringHandler.updateNoteTags(updatedNote);

        assertTrue(removedTags.contains("#java"));
        List<String> availableTags = tagFilteringHandler.getAvailableTags();
        assertFalse(availableTags.contains("#java"));
    }

    @Test
    void testGetNotesToDisplay() {
        serverUtils.clearAll();

        List<Long> noteIds = List.of(1L, 2L, 3L);
        List<NoteTags> loadedNoteTags = List.of(
                new NoteTags(1L, new HashSet<>(List.of("tag1", "tag2"))),
                new NoteTags(2L, new HashSet<>(List.of("tag2", "tag3")))
        );

        for (NoteTags noteTags : loadedNoteTags) {
            Note note = new Note("Note", "content", collection);
            note.id = noteTags.getId();
            serverUtils.addNote(note);
            note.id = getServerNoteId(note);
        }

        tagFilteringHandler.loadNewNoteTags(noteIds);

        tagFilteringHandler.addTag("tag1");

        List<String> expectedAvailableTags = List.of("tag2");

        List<Long> actualMatchingIds = tagFilteringHandler.getNotesToDisplay();
        List<String> actualAvailableTags = tagFilteringHandler.getAvailableTags();

        assertEquals(expectedAvailableTags, actualAvailableTags);
    }


    @Test
    void testClearTags() {
        tagFilteringHandler.addTag("#test");
        tagFilteringHandler.addTag("#java");
        tagFilteringHandler.clearTags();

        serverUtils.clearAll();
        List<NoteTags> mockNoteTags = List.of(
                new NoteTags(1L, new HashSet<>(List.of("#java", "#test")))
        );

        for (NoteTags noteTag : mockNoteTags) {
            Note note = new Note("Test Note", "Some content", collection);
            note.id = noteTag.getId();
            serverUtils.addNote(note);
        }

        tagFilteringHandler.loadNewNoteTags(List.of(1L));

        List<Long> displayedNotes = tagFilteringHandler.getNotesToDisplay();
        assertEquals(1, displayedNotes.size());
    }

    @Test
    void testRemoveTagsIfOrphaned() {
        Note note1 = new Note("title", "Test #java #test", collection);
        Note note2 = new Note("title", "Test #python #test", collection);

        serverUtils.addNote(note1);
        serverUtils.addNote(note2);
        note1.id = getServerNoteId(note1);
        note2.id = getServerNoteId(note2);

        tagFilteringHandler.addNoteTags(note1);
        tagFilteringHandler.addNoteTags(note2);

        HashSet<String> removedTags = new HashSet<>(List.of("#java"));
        List<String> orphanedTags = tagFilteringHandler.removeTagsIfOrphaned(removedTags);

        assertTrue(orphanedTags.isEmpty());

        tagFilteringHandler.deleteNoteTags(1L);
        orphanedTags = tagFilteringHandler.removeTagsIfOrphaned(removedTags);

        assertEquals(1, orphanedTags.size());
        assertTrue(orphanedTags.contains("#java"));
    }
}
