package server.api;

import java.util.*;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.NoteRepository;
import server.services.NoteService;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteRepository noteRepository;
    private final NoteService noteService;

    @Autowired
    public NoteController(NoteRepository noteRepository, NoteService noteService) {
        this.noteRepository = noteRepository;
        this.noteService = noteService;
    }

    @GetMapping(path = {"", "/"})
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
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
        if (id < 0 || !noteRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(noteRepository.findById(id).get());
    }

    /**
     * Returns a boolean for the existence of a note
     * @param id    unique identifier of a note
     * @return      boolean whether the note is found in the database
     */
    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable("id") long id) {
        return noteRepository.existsById(id);
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
        if (note.id < 0 || noteRepository.existsById(note.id)) {
            return ResponseEntity.badRequest().build();
        }

        Note saved = noteRepository.save(note);
        return ResponseEntity.ok(saved);
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
        if (id < 0 || !noteRepository.existsById(id)){
            return ResponseEntity.badRequest().build();
        }
        Note removed = noteRepository.findById(id).get();
        noteRepository.deleteById(id);
        return ResponseEntity.ok(removed);
    }

    @PutMapping(path={"", "/"})
    public ResponseEntity<Note> updateNote(@RequestBody Note note) {
        try {
            noteRepository.save(note);
            return ResponseEntity.ok(note);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO: do something with this mock function which is still being used for some reason
    @GetMapping(path="/mock")
    public ResponseEntity<Note> mockGetDefaultNote() {
        List<Note> notes = noteRepository.findAll();
        if (notes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(notes.get(0));
    }

    @GetMapping(path="/last")
    public ResponseEntity<Boolean> isLastNoteInCollection(@RequestParam long noteId) {
        boolean isLastNote = noteService.isLastNoteInCollection(noteId);
        return ResponseEntity.ok(isLastNote);
    }
}
