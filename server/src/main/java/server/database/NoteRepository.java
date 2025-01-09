package server.database;

import commons.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Find all notes that belong to a specific collection
     * @param collectionId The ID that corresponds with the collection.
     * @return A list of all notes in the specified collection.
     */
    @Query("SELECT n FROM Note n WHERE n.collection.id = :collectionId")
    List<Note> findByCollectionId(@Param("collectionId") UUID collectionId);
}
