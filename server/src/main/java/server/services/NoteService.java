package server.services;

import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final CollectionService collectionService;

    @Autowired
    public NoteService(NoteRepository noteRepository, CollectionService collectionService) {
        this.noteRepository = noteRepository;
        this.collectionService = collectionService;
    }

    public boolean isLastNoteInCollection(long id) {
        Note note = noteRepository.findById(id).orElseThrow();
        int numberOfNotes = collectionService.getNotesInCollection(note.collection.id).size();
        return numberOfNotes == 1;
    }
}
