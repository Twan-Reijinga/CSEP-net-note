package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.database.CollectionRepository;
import server.database.NoteRepository;
import commons.Collection;
import commons.Note;

@Configuration
public class DataInitializer {

    private NoteRepository noteRepository;

    private CollectionRepository collectionRepository;

    @Autowired
    public DataInitializer(NoteRepository noteRepository, CollectionRepository collectionRepository) {
        this.noteRepository = noteRepository;
        this.collectionRepository = collectionRepository;
    }

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            if (collectionRepository.existsById("default")) return;

            // Create the default collection if it doesn't exist
            Collection defaultCollection = new Collection("default", "Default Collection");
            collectionRepository.save(defaultCollection);

            // Create some boilerplate notes in the default collection
            createBoilerplateNotes(defaultCollection);

            System.out.println("Default collection created.");
        };
    }

    private void createBoilerplateNotes(Collection defaultCollection) {
        // List of boilerplate notes to add
        Note note1 = new Note("Sample Note 1", "This is the content of the first sample note.", defaultCollection);
        Note note2 = new Note("Sample Note 2", "This is the content of the second sample note.", defaultCollection);
        Note note3 = new Note("Sample Note 3", "This is the content of the third sample note.", defaultCollection);

        // Save the notes to the repository (i.e., store them in the database)
        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);

        System.out.println("Boilerplate notes created.");
    }
}
