package server;

import java.util.*;
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

            // FIXME: potential over-complication (right now). We are using memdb so why bother checking
            //  if default collection exists, furthermore, if we were to use filedb data initializer would
            //  create to many same titles, collections, etc. Maybe data initializer should have different
            //  modes: one for dev one for prod

            Optional<Collection> collectionResult = collectionRepository.findFirstByIsDefaultTrue();

            if (collectionResult.isPresent()) return;

            // Create the default collection if it doesn't exist
            Collection defaultCollection = new commons.Collection("default", "Default Collection");
            defaultCollection.isDefault = true;

            // Add another collection to show off multi-collection support
            Collection arbitraryCollection = new commons.Collection("arbitrary", "Arbitrary Collection");

            collectionRepository.save(defaultCollection);
            collectionRepository.save(arbitraryCollection);

            System.out.println("Boilerplate collections created.");

            // Create some boilerplate notes in the default collection
            createBoilerplateNotes(defaultCollection, 3);
            createBoilerplateNotes(arbitraryCollection, 1);
        };
    }

    private void createBoilerplateNotes(Collection collection, int count) {
        for (int i = 1; i < count + 1; i++) {
            Note note = new Note(
                    "Sample Note " + i,
                    "This is the content of the note #" + i,
                    collection);
            noteRepository.save(note);
        }

        System.out.println("Boilerplate notes created for: " + collection.name);
    }
}
