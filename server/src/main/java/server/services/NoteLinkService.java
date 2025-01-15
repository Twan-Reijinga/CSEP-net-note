package server.services;

import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.CollectionRepository;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NoteLinkService {
    private final NoteRepository noteRepository;

    @Autowired
    public NoteLinkService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public boolean updateNoteLinks(Long noteId, String newTitle, String oldTitle){
        Optional<Note> updatedNote = noteRepository.findById(noteId);
        if(!updatedNote.isPresent()){
            return false;
        }
        Note note = updatedNote.get();

        List<Note> notesInCollection = noteRepository.findByCollectionId(note.collection.id);

        for(Note item: notesInCollection){
            item.content = item.content.replace("[[" + oldTitle + "]]", "[[" + newTitle + "]]");
        }

        noteRepository.saveAllAndFlush(notesInCollection);
        return true;
    }
}
