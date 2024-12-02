package server.api;

import java.util.*;
import commons.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.CollectionRepository;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private CollectionRepository collectionRepository;

    @Autowired
    public CollectionController(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @GetMapping(path = {"", "/"})
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }
}
