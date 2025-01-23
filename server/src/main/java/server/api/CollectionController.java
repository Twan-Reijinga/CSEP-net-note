package server.api;

import java.util.*;
import commons.Collection;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.database.NoteRepository;
import server.services.CollectionService;
import server.services.WebsocketService;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final NoteRepository noteRepository;
    private final CollectionService collectionService;
    private final WebsocketService websocketService;

    @Autowired
    public CollectionController(
            CollectionRepository collectionRepository,
            NoteRepository noteRepository,
            CollectionService collectionService,
            WebsocketService websocketService
    ) {
        this.collectionRepository = collectionRepository;
        this.noteRepository = noteRepository;
        this.collectionService = collectionService;
        this.websocketService = websocketService;
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
    public List<Note> getNotesInCollection(@PathVariable("id") UUID id ) {
        return collectionService.getNotesInCollection(id);
    }

    /**
     * Stores a new collection in the database, with the provided arguments.
     * Collection can not have empty values.
     * The method needs to be addressed through making a client.Post.
     * <p>
     * @param collection  the entity that needs to be stored in database
     * @return a response entity containing a collection if successfully created
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Collection> add(@RequestBody Collection collection) {
        if (collectionRepository.findById(collection.id).isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Collection added = collectionRepository.save(collection);
        websocketService.notifyCollectionSubscribers(websocketService.onCollectionCreated, added);
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
    public ResponseEntity<Void> remove(@PathVariable("id") UUID id) {
        try {
            // Delete notes inside the collection
            List<Note> notes = noteRepository.findByCollectionId(id);
            if (!notes.isEmpty()) {
                noteRepository.deleteAll(notes);
            }

            var temp = collectionRepository.findById(id).get();
            collectionRepository.deleteById(id);
            websocketService.notifyCollectionSubscribers(websocketService.onCollectionDeleted, temp);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path={"", "/"})
    public ResponseEntity<Collection> updateCollection(@RequestBody Collection collection) {
        try {
            var temp = collectionRepository.save(collection);
            websocketService.notifyCollectionSubscribers(websocketService.onCollectionCreated, temp);
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

    @GetMapping(path="/default")
    public ResponseEntity<Collection> getDefaultCollection() {
        try {
            Optional<Collection> collection = collectionRepository.getCollectionByName("default");
            if (collection.isPresent()) {
                return ResponseEntity.ok(collection.get());
            } else {
                Collection defaultCollection = new Collection("default", "Default Collection");
                defaultCollection = collectionRepository.save(defaultCollection);
                return ResponseEntity.ok(defaultCollection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path="/{collectionId}")
    public ResponseEntity<Collection> getCollectionById(@PathVariable UUID collectionId) {
        try {
            Optional<Collection> collection = collectionService.getCollectionById(collectionId);
            if (collection.isPresent()) {
                return ResponseEntity.ok(collection.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}
