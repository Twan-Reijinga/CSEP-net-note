package server.services;

import commons.Collection;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.CollectionRepository;
import server.database.NoteRepository;

import java.util.*;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final NoteRepository noteRepository;
    private final WebsocketService websocketService;

    @Autowired
    public CollectionService(CollectionRepository collectionRepository,
                             NoteRepository noteRepository,
                             WebsocketService websocketService) {
        this.collectionRepository = collectionRepository;
        this.noteRepository = noteRepository;
        this.websocketService = websocketService;
    }

    public Optional<Collection> addCollection(Collection collection) {
        if (collectionRepository.findById(collection.id).isPresent())
            return Optional.empty();

        try {
            collection = collectionRepository.save(collection);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
        websocketService.notifyCollectionSubscribers(websocketService.onCollectionCreated, collection);
        return Optional.of(collection);
    }

    public Optional<Collection> updateCollection(Collection collection) {
        if (!collectionRepository.existsById(collection.id))
            return Optional.empty();

        try {
            collection = collectionRepository.save(collection);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
        websocketService.notifyCollectionSubscribers(websocketService.onCollectionUpdated, collection);
        return Optional.of(collection);
    }

    public Optional<Object> deleteCollection(UUID id) {
        if (!collectionRepository.existsById(id))
            return Optional.empty();

        var collection = collectionRepository.findById(id).get();
        try {
            // Also delete all notes from collection
            List<Note> notes = noteRepository.findByCollectionId(id);
            if (!notes.isEmpty()) noteRepository.deleteAll(notes);

            collectionRepository.deleteById(id);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
        websocketService.notifyCollectionSubscribers(websocketService.onCollectionDeleted, collection);
        return Optional.of(new Object());
    }

    public Optional<Collection> getCollectionByName(String name) {
        return collectionRepository.getCollectionByName(name);
    }

    public String getUniqueCollectionName() {
        final Random random = new Random();
        final StringBuilder randomString = new StringBuilder();
        final String template = "collection-";
        final String alphabet = "abcdefghijklmnopqrstuvwxyz";
        randomString.append(template);

        while (true) {
            randomString.setLength(template.length());

            int length = 6;
            while(length-- > 0)
                randomString.append(alphabet.charAt(random.nextInt(alphabet.length())));

            var newName = randomString.toString();
            if(!collectionRepository.existsByName(newName))
                return newName;
        }
    }

    public List<Note> getNotesInCollection(UUID id) {
        if (collectionRepository.findById(id).isEmpty()) {
            return null;
        }
        List<Note> tempNoteList = new ArrayList<>(List.copyOf(noteRepository.findAll()));
        tempNoteList.removeIf(currentNote -> !(currentNote.collection.id.equals(id)));
        return tempNoteList;
    }

    public Optional<Collection> getCollectionById(UUID collectionId) {
        Optional <Collection> collection = collectionRepository.findById(collectionId);
        return collection;
    }

    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }

    public void setDefaultCollectionId(UUID id) {
        websocketService.notifyDefaultIdSubscribers(websocketService.onDefaultCollectionIdChanged, id);
        System.out.println("Changed default id to " + id);
    }
}
