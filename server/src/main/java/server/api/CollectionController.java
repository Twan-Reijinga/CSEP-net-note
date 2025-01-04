package server.api;

import java.util.*;
import commons.Collection;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.database.NoteRepository;
import server.services.CollectionService;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final NoteRepository noteRepository;
    private final CollectionService collectionService;

    @Autowired
    public CollectionController(
            CollectionRepository collectionRepository,
            NoteRepository noteRepository,
            CollectionService collectionService
    ) {
        this.collectionRepository = collectionRepository;
        this.noteRepository = noteRepository;
        this.collectionService = collectionService;
    }

    @GetMapping(path = {"", "/"})
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }


    /**
     * Returns a list of all notes that are in the collection
     * Works with removing the collection
     * <p>
     * @param id the identifier of the collection
     * @return List of Notes, the notes that correspond to the current selected collection
     */
    @GetMapping("/getNotes/{id}")
    public List<Note> getNotesInCollection(@PathVariable("id") Long id ) {
        if (id < 0 || collectionRepository.findById(id).isEmpty()) {
            return null;
        }
        List<Note> tempNoteList = new ArrayList<>(List.copyOf(noteRepository.findAll()));
        tempNoteList.removeIf(currentNote -> !(currentNote.collection.id == id));
        return tempNoteList;
    }

    /**
     * Stores a new collection in the database, with the provided arguments.
     * Collection can not have empty values.
     * The method needs to be addressed through making a client.Post.
     * <p>
     * {@code @Param} collection  the entity that needs to be stored in database
     * {@code @Return} a http Response, either bad-build or good
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Collection> add(@RequestBody Collection collection) {
        if (collection == null || collection.name == null || collection.title == null || collectionRepository.findById(collection.id).isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Collection added = collectionRepository.save(collection);
        return ResponseEntity.ok(added);
    }

    /**
     * Removes the collection of the provided ID from database
     * Name has to match with a collection in the database.
     * <p>
     * @param id  the identifier of the collection
     * @return a http Response, either a bad build or a valid one
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> remove(@PathVariable("id") Long id) {
        try {
            // TODO: also I need to start using services for this...

            // FIXME: once Petar's (search_bar) MR will be merge, it will include
            //  one-to-many decorator in collection with orphan removal constraint
            //  this will allow for cleaner approach

            // Delete notes inside the collection
            List<Note> notes = noteRepository.findByCollectionId(id);
            if (!notes.isEmpty()) {
                noteRepository.deleteAll(notes);
            }

            collectionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path={"", "/"})
    public ResponseEntity<Collection> updateCollection(@RequestBody Collection collection) {
        try {
            collectionRepository.save(collection);
            return ResponseEntity.ok(collection);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path="/unique-name")
    public ResponseEntity<String> getUniqueCollectionName() {
        try {
            String uniqueName = collectionService.getUniqueCollectionName();
            return ResponseEntity.ok(uniqueName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
