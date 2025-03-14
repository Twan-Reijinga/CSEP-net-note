package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.NoteLinkService;

@RestController
@RequestMapping("/api/links")
public class NoteLinkController {
    private final NoteLinkService noteLinkService;

    @Autowired
    public NoteLinkController(NoteLinkService noteLinkService) {
        this.noteLinkService = noteLinkService;
    }

    @GetMapping(path = "")
    public ResponseEntity<Boolean> renameNoteLinks(@RequestParam Long noteId,
                                                   @RequestParam String newTitle,
                                                   @RequestParam String oldTitle) {
        boolean result = this.noteLinkService.updateNoteLinks(noteId, newTitle, oldTitle);
        return ResponseEntity.ok(result);
    }

}
