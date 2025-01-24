package server.api;

import java.util.*;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.NoteService;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping(path = {"", "/"})
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }

    /**
     * This method handles GET requests to the url "/api/notes/{id}"
     * when a request is made the database is queried for a note with the id specified.
     * If no such note exists the response is 400 - "Bad request".
     *
     * @param id    unique identifier of every note
     * @return      the note with that id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(@PathVariable("id") long id) {
        Optional<Note> note = noteService.getNote(id);
        return note.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Returns a boolean for the existence of a note
     * @param id    unique identifier of a note
     * @return      boolean whether the note is found in the database
     */
    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable("id") long id) {
        Optional<Note> note = noteService.getNote(id);
        return note.isPresent();
    }

    /**
     * Stores a new note in the database, with the provided arguments.
     * Note can not have empty values, and has to have a collection.
     * The method needs to be addressed through making a client.Post.
     * <p>
     * @param note  the entity that needs to be stored in database
     * @return a http Response, either bad-build or good
     */
    @PostMapping("")
    public ResponseEntity<Note> add(@RequestBody Note note) {
        Optional<Note> addedNote = noteService.addNote(note);
        return addedNote.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Removes the note of the provided ID from database
     * id has to match with a note in the database.
     * <p>
     * @param id  the identifier of a note
     * @return a http Response, either a bad build or a valid one
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Note> delete(@PathVariable("id") long id) {
        Optional<Object> result = noteService.deleteNote(id);
        if(result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(path={"", "/"})
    public ResponseEntity<Note> updateNote(@RequestBody Note note) {
        Optional<Note> updatedNote = noteService.updateNote(note);
        return updatedNote.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping(path="/last")
    public ResponseEntity<Boolean> isLastNoteInCollection(@RequestParam long noteId) {
        boolean isLastNote = noteService.isLastNoteInCollection(noteId);
        return ResponseEntity.ok(isLastNote);
    }
}
