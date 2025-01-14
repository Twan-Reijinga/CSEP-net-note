package server.services;

import commons.Collection;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.CollectionRepository;
import server.database.NoteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CollectionService {
    private RandomService randomService;
    private CollectionRepository collectionRepository;
    private NoteRepository noteRepository;

    @Autowired
    public CollectionService(RandomService randomService,
                             CollectionRepository collectionRepository,
                             NoteRepository noteRepository) {
        this.randomService = randomService;
        this.collectionRepository = collectionRepository;
        this.noteRepository = noteRepository;
    }

    public String getUniqueCollectionName() {
        while (true) {
            String random = "collection-" + randomService.getRandomString(6);
            if (!collectionRepository.existsByName(random)) {
                return random;
            }
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

}
