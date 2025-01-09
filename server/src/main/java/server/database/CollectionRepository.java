package server.database;

import java.util.*;
import commons.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, UUID> {
    Boolean existsByName(String name);

    Optional<Collection> getCollectionByName(String name);
}
