package server.api;

import commons.NoteTitle;
import commons.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/titles")
public class TitleController {

    private final NoteRepository noteRepository;
    private final CollectionRepository collectionRepository;

    public TitleController(
            NoteRepository noteRepository,
            CollectionRepository collectionRepository) {
        this.noteRepository = noteRepository;
        this.collectionRepository = collectionRepository;
    }

    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<NoteTitle>> getAllTitles(@RequestParam(required = false) Long collectionId) {
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
