package server.api;

import commons.NoteTitle;
import commons.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/titles")
public class TitleController {

    private final NoteRepository noteRepository;

    public TitleController(NoteRepository repo, NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<NoteTitle>> getAllTitles(@RequestParam(required = false) Long collectionId) {
        List<Note> notes = noteRepository.findAll();

        List<NoteTitle> titles = notes
                .stream()
                .map(note -> new NoteTitle(note.title, note.id))
                .toList();

        return ResponseEntity.ok(titles);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<NoteTitle> getTitle(@PathVariable("id") Long collectionId) {
        Optional<Note> note = noteRepository.findById(collectionId);
        if (note.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        NoteTitle nt = new NoteTitle(note.get().title, note.get().id);

        return ResponseEntity.ok(nt);
    }
}
