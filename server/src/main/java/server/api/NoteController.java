package server.api;

import java.util.*;
import commons.Note;
import commons.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.NoteRepository;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteRepository noteRepository;

    @Autowired
    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping(path = {"", "/"})
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    /** This method handles Get requests to the url "/api/notes/{id}"
     *  when a request is made the database is queried for a note with the id specified.
     *  If no such note exists the response is 400 - "Bad request".
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

    /** This method handles get requests to the "/api/notes/sidebar" url
     *  when a request is made it gets all notes in the database, then
     *  each note is mapped to a NoteMapper object that contains only an id
     *  and a title.
     *
     * @return a list of NoteMapper objects
     */
    @GetMapping(path = "/sidebar")
    public List<NoteMapper> getMappedNotes(){
        List<Note> notes = noteRepository.findAll();
        List<NoteMapper> mapped = notes.stream().map(x -> new NoteMapper(x.id, x.title)).toList();

        return mapped;
    }

    /**
     * Stores a new note in the database, with the provided arguments.
     * Note can not have empty values, and has to have a collection.
     * The method needs to be addressed through making a client.Post.
     * <p>
     * {@code @Param} note  the entity that needs to be stored in database
     * {@code @Return} a http Response, either bad-build or good
     */
    @PostMapping("")
    public ResponseEntity<Note> add(@RequestBody Note note) {
        if (note.collection == null || note.id <= 0 || note.title == null || noteRepository.existsById(note.id)) {
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
}
