package server.api;

import commons.Note;
import commons.NoteTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.database.NoteRepository;
import server.services.TagService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final NoteRepository noteRepository;
    private final CollectionRepository collectionRepository;
    private final TagService tagService;

    @Autowired
    public TagController(NoteRepository noteRepository,
                         CollectionRepository collectionRepository,
                         TagService tagService) {
        this.noteRepository = noteRepository;
        this.collectionRepository = collectionRepository;
        this.tagService = tagService;
    }

    @GetMapping(path = {"", "/{collectionId}"})
    public ResponseEntity<List<NoteTags>> getAllTags(@PathVariable Long collectionId) {
        if (!collectionRepository.existsById(collectionId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Note> notes = noteRepository.findByCollectionId(collectionId);

        List<NoteTags> noteTags = new ArrayList<>();
        for (Note note : notes) {
            noteTags.add(tagService.getTags(note));
        }

        return ResponseEntity.ok(noteTags);
    }

    @PostMapping(path = {"", "/list"})
    public ResponseEntity<List<NoteTags>> getSpecifiedNoteTags(@RequestBody List<Long> requested) {
        List<NoteTags> response = new ArrayList<>();

        for (Long noteId : requested) {
            Note note = noteRepository.findById(noteId).orElse(null);
            if (note == null) {
                return ResponseEntity.badRequest().build();
            }
            response.add(tagService.getTags(note));
        }

        return ResponseEntity.ok(response);
    }
}
