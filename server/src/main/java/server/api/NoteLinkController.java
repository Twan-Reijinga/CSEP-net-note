package server.api;

import commons.Note;
import commons.NoteTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.database.NoteRepository;
import server.services.NoteLinkService;
import server.services.TagService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/links")
public class NoteLinkController {
    private final NoteLinkService noteLinkService;

    @Autowired
    public NoteLinkController(NoteLinkService noteLinkService) {
        this.noteLinkService = noteLinkService;
    }

    @GetMapping(path = "")
    public ResponseEntity<Boolean> getAllTags(@RequestParam Long noteId,
                                              @RequestParam String newTitle,
                                              @RequestParam String oldTitle) {
        boolean result = this.noteLinkService.updateNoteLinks(noteId, newTitle, oldTitle);
        return ResponseEntity.ok(result);
    }

}
