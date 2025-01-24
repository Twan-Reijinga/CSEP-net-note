package server.services;

import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final CollectionService collectionService;
    private final WebsocketService websocketService;

    @Autowired
    public NoteService(NoteRepository noteRepository,
                       CollectionService collectionService,
                       WebsocketService websocketService) {
        this.noteRepository = noteRepository;
        this.collectionService = collectionService;
        this.websocketService = websocketService;
    }

    public boolean isLastNoteInCollection(long id) {
        Note note = noteRepository.findById(id).orElseThrow();
        int numberOfNotes = collectionService.getNotesInCollection(note.collection.id).size();
        return numberOfNotes == 1;
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public Optional<Note> addNote(Note note) {
        if (noteRepository.existsById(note.id))
            return Optional.empty();

        try {
            note = noteRepository.save(note);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
        websocketService.notifyNoteSubscribers(websocketService.onNoteCreated, note);
        return Optional.of(note);
    }

    public Optional<Note> getNote(long id) {
        return noteRepository.findById(id);
    }

    public Optional<Note> updateNote(Note note) {
        if (!noteRepository.existsById(note.id))
            return Optional.empty();

        try {
            note = noteRepository.save(note);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
        websocketService.notifyNoteSubscribers(websocketService.onNoteUpdated, note);
        return Optional.of(note);
    }

    public Optional<Object> deleteNote(long id) {
        if (!noteRepository.existsById(id))
            return Optional.empty();

        var note = noteRepository.findById(id).get();
        try {
            noteRepository.deleteById(id);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
        websocketService.notifyNoteSubscribers(websocketService.onNoteDeleted, note);
        return Optional.of(new Object());
    }
}
