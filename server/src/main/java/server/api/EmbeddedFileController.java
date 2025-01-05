package server.api;

import java.util.*;

import commons.EmbeddedFile;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EmbeddedFileRepository;
import server.database.NoteRepository;

@RestController
@RequestMapping("/api/notes/{noteId}/embedded")
public class EmbeddedFileController {
    private final EmbeddedFileRepository embeddedFileRepository;

    @Autowired
    public EmbeddedFileController(EmbeddedFileRepository embeddedFileRepository) {
        this.embeddedFileRepository = embeddedFileRepository;
    }

    @GetMapping(path = {"", "/"})
    public List<EmbeddedFile> getAllFiles(@PathVariable("noteId") long id) {
        List<EmbeddedFile> emFiles = embeddedFileRepository.findAll();
        emFiles.removeIf(CurrentFile -> CurrentFile.note.id!=id);
        return emFiles;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmbeddedFile> getById(@PathVariable("noteId") long noteId, @PathVariable("id") long id) {
        if (id < 0 || embeddedFileRepository.findById(id).isEmpty() || embeddedFileRepository.findById(id).get().note.id!= noteId) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(embeddedFileRepository.findById(id).get());
    }

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable("id") long id) {
        return embeddedFileRepository.existsById(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<EmbeddedFile> addFile(@PathVariable("id") long id, @RequestBody EmbeddedFile file) {
        if (file.id < 0 || embeddedFileRepository.existsById(file.id)) {
            return ResponseEntity.badRequest().build();
        }

        EmbeddedFile saved = embeddedFileRepository.save(file);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmbeddedFile> deleteFile(@PathVariable("id") long id) {
        if (id < 0 || !embeddedFileRepository.existsById(id)){
            return ResponseEntity.badRequest().build();
        }
        EmbeddedFile removed = embeddedFileRepository.findById(id).get();
        embeddedFileRepository.deleteById(id);
        return ResponseEntity.ok(removed);
    }

    @DeleteMapping("")
    public ResponseEntity<List<EmbeddedFile>> deleteAllFiles(@PathVariable("noteId") long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        List<EmbeddedFile> removed = embeddedFileRepository.findAll();
        removed.removeIf(CurrentFile -> CurrentFile.note.id!=id);
        embeddedFileRepository.deleteAll(removed);
        return ResponseEntity.ok(removed);
    }
}
