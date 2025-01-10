package server.api;

import commons.NoteTitle;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/titles")
public class TitleController {

    private final NoteRepository noteRepository;
    private final CollectionRepository collectionRepository;

    /**
     * Constructor for the TitleController.
     * @param noteRepository Repository of all notes.
     * @param collectionRepository Repository of all collections.
     */
    @Autowired
    public TitleController(
            NoteRepository noteRepository,
            CollectionRepository collectionRepository) {
        this.noteRepository = noteRepository;
        this.collectionRepository = collectionRepository;
    }

    /**
     * This method handles GET requests to the "/api/titles" endpoint.
     * If no collection id is specified it will give all the title from the server.
     * If a collection id is specified, it will give only all the notes from
     * that collection.
     * Each NoteTitle has also its own id.
     *
     * @param collectionId Optional: id of the collection you want notes from
     *                     if not specified, all notes will be given.
     * @return A list with all the requested NoteTitle objects.
     */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<NoteTitle>> getAllTitles(@RequestParam(required = false) UUID collectionId) {
        List<Note> notes;
        if (collectionId == null) {
            notes = noteRepository.findAll();
        } else if (collectionRepository.existsById(collectionId)) {
            notes = noteRepository.findByCollectionId(collectionId);
        } else {
            return ResponseEntity.badRequest().build();
        }

        List<NoteTitle> titles = notes
                .stream()
                .map(note -> new NoteTitle(note.title, note.id))
                .toList();

        return ResponseEntity.ok(titles);
    }

    /**
     * this method handles GET requests from /api/titles/{id} to get a specific title
     * If no note with the specified ID exists,
     * the endpoint will return a 400 - "Bad request".
     *
     * @param id The identifier of the note you want to get the title of.
     * @return One NoteTitle object containing a title with the id.
     */
    @GetMapping(path="/{id}")
    public ResponseEntity<NoteTitle> getTitle(@PathVariable("id") Long id) {
        if (id < 0 || !noteRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Note> note = noteRepository.findById(id);
        if (note.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        NoteTitle nt = new NoteTitle(note.get().title, note.get().id);

        return ResponseEntity.ok(nt);
    }
}
