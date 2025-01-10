package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.CollectionRepository;

@Service
public class CollectionService {
    private RandomService randomService;
    private CollectionRepository collectionRepository;

    @Autowired
    public CollectionService(RandomService randomService, CollectionRepository collectionRepository) {
        this.randomService = randomService;
        this.collectionRepository = collectionRepository;
    }

    public String getUniqueCollectionName() {
        while (true) {
            String random = "collection-" + randomService.getRandomString(6);
            if (!collectionRepository.existsByName(random)) {
                return random;
            }
        }
    }
}
