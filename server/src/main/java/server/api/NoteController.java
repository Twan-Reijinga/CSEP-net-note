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
    private NoteRepository noteRepository;

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

    @GetMapping(path="/mock")
    public ResponseEntity<Note> MOCK_getDefaultNote() {
        List<Note> notes = noteRepository.findAll();
        if (notes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(notes.get(0));
    }
}
