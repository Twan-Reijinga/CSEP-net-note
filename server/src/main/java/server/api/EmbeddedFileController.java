package server.api;

import java.util.*;

import commons.EmbeddedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EmbeddedFileRepository;

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
        if (id < 0 || embeddedFileRepository.findById(id).isEmpty()
                || embeddedFileRepository.findById(id).get().note.id!= noteId) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(embeddedFileRepository.findById(id).get());
    }

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable("id") long id) {
        return embeddedFileRepository.existsById(id);
    }

    /**
     * Stores the file in the EmbeddedFileRep, stores the file as base64
     * @param id Unique file id (every file must be stored on note/embedded/{fileId}
     * @param file the file that needs to be saved
     * @return  HTTP response
     */
    @PostMapping("/{id}")
    public ResponseEntity<EmbeddedFile> addFile(@PathVariable("id") long id, @RequestBody EmbeddedFile file) {
        if (file.id < 0 || embeddedFileRepository.existsById(file.id)) {
            return ResponseEntity.badRequest().build();
        }

        EmbeddedFile saved = embeddedFileRepository.save(file);
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes the specified file
     * @param id The unique file id that needs to be removed
     * @return HTTP response based on valid file
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<EmbeddedFile> deleteFile(@PathVariable("id") long id) {
        if (id < 0 || !embeddedFileRepository.existsById(id)){
            return ResponseEntity.badRequest().build();
        }
        EmbeddedFile removed = embeddedFileRepository.findById(id).get();
        embeddedFileRepository.deleteById(id);
        return ResponseEntity.ok(removed);
    }

    /**
     * Deletes all the files in a note
     * @param id The unique id of a note
     * @return  - HTTP response
     */
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

    @PutMapping("{id}")
    public ResponseEntity<EmbeddedFile> updateFile(@RequestBody EmbeddedFile file) {
        try {
            EmbeddedFile saved = embeddedFileRepository.save(file);
            return ResponseEntity.ok(saved);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<byte[]> getFile(@PathVariable("title") String title) {
        try {
            List<EmbeddedFile> embeddedFiles = embeddedFileRepository.findAll();
            embeddedFiles.removeIf(CurrentFile -> !CurrentFile.title.equals(title));
            byte[] fileBytes = Base64.getDecoder().decode(embeddedFiles.getFirst().file);
            String type = "image/" + title.split("\\.")[title.split("\\.").length - 1];

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", type);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileBytes);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
