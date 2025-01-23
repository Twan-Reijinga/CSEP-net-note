package server.services;

import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    /**
     * This method updates all links to a specific note when it's title is changed.
     * This is done by taking the contents of all notes in the same collection and replacing
     * the text of all links inside with the new title.
     * @param noteId The id of the note which had it's title changed
     * @param newTitle The new title for the note
     * @param oldTitle The title used previously to reference that note
     * @return true if successful, otherwise false
     */
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
