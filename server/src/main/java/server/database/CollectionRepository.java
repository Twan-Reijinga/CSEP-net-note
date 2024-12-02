package server.database;

import java.util.*;
import commons.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    // Find the first collection where isDefault is true
    Optional<Collection> findFirstByIsDefaultTrue();
}
