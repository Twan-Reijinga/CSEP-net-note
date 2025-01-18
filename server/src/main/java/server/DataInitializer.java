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
            // TODO: consider creating dev/prod versions of this data-initializer

            // Create the default collection if it doesn't exist
            Collection defaultCollection = new Collection("default", "Default Collection");

            // Add another collection to show off multi-collection support
            Collection arbitraryCollection = new Collection("arbitrary", "Arbitrary Collection");

            // TODO: *IMPORTANT*: note to the future: app will (most likely) crash if
            //  there is NO collection named *"default"* with AT LEAST *one note* inside

            defaultCollection = collectionRepository.save(defaultCollection);
            arbitraryCollection = collectionRepository.save(arbitraryCollection);

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
