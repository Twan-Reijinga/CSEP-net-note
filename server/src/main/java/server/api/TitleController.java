package server.api;

import client.utils.NoteTitle;
import commons.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.database.NoteRepository;

import java.util.List;

@RestController
@RequestMapping("/api/titles")
public class TitleController {

    private final NoteRepository noteRepository;

    public TitleController(NoteRepository repo, NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<NoteTitle>> getTitle(@RequestParam(required = false) Long collectionId) {
        List<Note> notes = noteRepository.findAll();
        // TODO: filter on collectionID, could only be done after MR !8 from Oleh is approved //

        List<NoteTitle> titles = notes
                .stream()
//                .filter(note -> note.collectionId = collectionId) // ^ see TODO ^ //
                .map(note -> new NoteTitle(note.title, note.id))
                .toList();

        return ResponseEntity.ok(titles);
    }
}
