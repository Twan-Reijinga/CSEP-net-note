package server.database;

import commons.Collection;
import commons.EmbeddedFile;
import commons.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmbeddedFileRepository extends JpaRepository<EmbeddedFile, Long> {

    /**
     * Find all embedded files that belong to a specific note
     * @param noteId The ID that corresponds with the note.
     * @return A list of all files in the specified note.
     */
    @Query("SELECT e FROM EmbeddedFile e WHERE e.note.id = :noteId")
    List<EmbeddedFile> findEmbeddedFilesByNoteId(@Param("noteId") long noteId);
}
