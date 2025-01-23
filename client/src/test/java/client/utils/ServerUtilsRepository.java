package client.utils;

import client.config.Config;
import commons.*;
import commons.Collection;
import jakarta.ws.rs.ProcessingException;

import java.net.ConnectException;
import java.util.*;
import java.util.stream.Collectors;

public class ServerUtilsRepository extends ServerUtils {
    private final Map<UUID, Collection> collections;
    private final Map<Long, Note> notes;
    private boolean serverAvailable;
    private long lastNoteId;

    public ServerUtilsRepository(Config config) {
        super(config);
        this.collections = new HashMap<>();
        this.notes = new HashMap<>();
        this.serverAvailable = true;
        initializeDefaultCollection();
        lastNoteId = 1;
    }

    private void initializeDefaultCollection() {
        Collection defaultCollection = new Collection("name", "title");
        defaultCollection.id = UUID.randomUUID();
        defaultCollection.name = "Default";
        collections.put(defaultCollection.id, defaultCollection);
    }

    @Override
    public List<Collection> getAllCollections() {
        return new ArrayList<>(collections.values());
    }

    @Override
    public Collection getDefaultCollection() {
        return collections.values().stream()
                .filter(c -> "Default".equals(c.name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection addCollection(Collection collection) {
        if (collection.id == null) {
            collection.id = UUID.randomUUID();
        }
        collections.put(collection.id, collection);
        return collection;
    }

    @Override
    public Collection updateCollection(Collection collection) {
        if (collections.containsKey(collection.id)) {
            collections.put(collection.id, collection);
            return collection;
        }
        return null;
    }

    @Override
    public String getUniqueCollectionName() {
        int counter = 1;
        String baseName = "New Collection";
        String name = baseName;
        String finalName = name;
        while (collections.values().stream().anyMatch(c -> c.name.equals(finalName))) {
            name = baseName + " " + counter++;
        }
        return name;
    }

    @Override
    public void deleteCollection(Collection collection) {
        collections.remove(collection.id);
    }

    @Override
    public List<NoteTitle> getNoteTitles() {
        return notes.values().stream()
                .map(note -> new NoteTitle("title", 1L))
                .collect(Collectors.toList());
    }

    @Override
    public List<NoteTitle> getNoteTitlesInCollection(UUID collectionId) {
        return notes.values().stream()
                .filter(note -> note.collection.id.equals(collectionId))
                .map(note -> new NoteTitle("title", 1L))
                .collect(Collectors.toList());
    }

    @Override
    public Note updateNote(Note note) {
        if (notes.containsKey(note.id)) {
            notes.put(note.id, note);
            return note;
        }
        return null;
    }

    @Override
    public boolean isServerAvailable() {
        if (!serverAvailable) {
            throw new ProcessingException(new ConnectException());
        }
        return true;
    }

    @Override
    public List<Note> getAllNotes() {
        return new ArrayList<>(notes.values());
    }

    @Override
    public Note getNoteById(Long id) {
        return notes.get(id);
    }

    @Override
    public boolean existsNoteById(long id) {
        return notes.containsKey(id);
    }

    @Override
    public void addNote(Note note) {
        note.id = generateNoteId();
        notes.put(note.id, note);
    }

    @Override
    public void deleteNote(Note note) {
        notes.remove(note.id);
    }

    @Override
    public Collection getCollectionById(UUID collectionId) {
        return collections.get(collectionId);
    }

    @Override
    public List<NoteTitle> searchNotesInCollection(UUID collectionId, String text, boolean matchAll, int whereToSearch) {
        return notes.values().stream()
                .filter(note -> collectionId == null || note.collection.id.equals(collectionId))
                .filter(note -> matchesSearchCriteria(note, text, matchAll, whereToSearch))
                .map(note -> new NoteTitle("title", 1L))
                .collect(Collectors.toList());
    }

    @Override
    public List<NoteTags> getAllNoteTags(UUID collectionId) {
        HashSet<String> tags = new HashSet<>(List.of("tag1", "tag2"));
        return notes.values().stream()
                .filter(note -> note.collection.id.equals(collectionId))
                .map(note -> new NoteTags(1L, tags))
                .collect(Collectors.toList());
    }

    @Override
    public List<NoteTags> getNoteTags(List<Long> noteIds) {
        HashSet<String> tags = new HashSet<>(List.of("tag1", "tag2"));
        return noteIds.stream()
                .map(notes::get)
                .filter(Objects::nonNull)
                .map(note -> new NoteTags(note.id, tags))
                .collect(Collectors.toList());
    }

    private long generateNoteId() {
        return this.lastNoteId++;
    }

    private boolean matchesSearchCriteria(Note note, String text, boolean matchAll, int whereToSearch) {
        if (text == null || text.isEmpty()) {
            return true;
        }

        String[] searchTerms = text.toLowerCase().split("\\s+");
        String noteTitle = note.title.toLowerCase();
        String noteContent = note.content.toLowerCase();

        if (1 == whereToSearch) {
            return matchTerms(searchTerms, noteTitle, matchAll);
        } else if (2 == whereToSearch) {
            return matchTerms(searchTerms, noteContent, matchAll);
        } else {
            return matchAll ?
                    matchTerms(searchTerms, noteTitle + " " + noteContent, matchAll) :
                    matchTerms(searchTerms, noteTitle, false) || matchTerms(searchTerms, noteContent, false);
        }
    }

    private boolean matchTerms(String[] terms, String text, boolean matchAll) {
        if (matchAll) {
            return Arrays.stream(terms).allMatch(text::contains);
        } else {
            return Arrays.stream(terms).anyMatch(text::contains);
        }
    }

    public void clearAll() {
        collections.clear();
        notes.clear();
        initializeDefaultCollection();
    }
}