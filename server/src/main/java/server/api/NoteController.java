package server.api;

import java.util.*;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
