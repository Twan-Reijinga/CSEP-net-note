package server.api;

import java.util.*;
import commons.Collection;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CollectionService;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionService collectionService;

    @Autowired
    public CollectionController(
            CollectionService collectionService
    ) {
        this.collectionService = collectionService;
    }

    @GetMapping(path = {"", "/"})
    public List<Collection> getAllCollections() {
        return collectionService.getAllCollections();
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
        Optional<Collection> addedCollection = collectionService.addCollection(collection);
        return addedCollection.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
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
        Optional<Object> result = collectionService.deleteCollection(id);
        if(result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(path={"", "/"})
    public ResponseEntity<Collection> updateCollection(@RequestBody Collection collection) {
        Optional<Collection> updatedCollection = collectionService.updateCollection(collection);
        return updatedCollection.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
        var collection = collectionService.getCollectionByName("default");
        if (collection.isPresent())
            return ResponseEntity.ok(collection.get());

        var newDefault = collectionService.addCollection(new Collection("default", "Default Collection"));
        return newDefault.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping(path="/{collectionId}")
    public ResponseEntity<Collection> getCollectionById(@PathVariable UUID collectionId) {
        Optional<Collection> collection = collectionService.getCollectionById(collectionId);
        return collection.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
