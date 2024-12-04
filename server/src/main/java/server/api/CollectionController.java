package server.api;

import java.util.*;
import commons.Collection;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.CollectionRepository;
import server.database.NoteRepository;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private CollectionRepository collectionRepository;
    private NoteRepository noteRepository;

    @Autowired
    public CollectionController(CollectionRepository collectionRepository, NoteRepository noteRepository) {
        this.collectionRepository = collectionRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping(path = {"", "/"})
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }



    /** This method handles the GET request to the address bellow
     *  and returns all notes containing all keywords given.
     *
     * @param keywords the words by which the search is performed
     * @return list of all notes containing every keyword
     */
    @GetMapping(path = "/search/{name}/{keywords}")
    public List<Note> getSearchedNotes(@PathVariable String name, @PathVariable String keywords) {
        List<Note> resultNotes = new ArrayList<>();
        String[] words = keywords.split(" ");
        // List<Note> notesInCollection = collectionRepository.findById(name).Notes;

//        for (Note note : notesInCollection) {
//            if(scanSingleNote(note, words)){
//                resultNotes.add(note);
//            }
//        }

        return resultNotes;
    }

    /** This note performs the search for keywords in
     *  the title and content of a specified note.
     *
     * @param note The note which we check for keywords
     * @param words The keywords we are searching for
     * @return true or false, depending on whether the
     *         note contains every keyword or not.
     */
    public boolean scanSingleNote(Note note, String[] words){
        for(String word : words){
            boolean isInTitle = note.title.toLowerCase().contains(word.toLowerCase());
            boolean isInContent = note.content.toLowerCase().contains(word.toLowerCase());
            if(!(isInTitle || isInContent)){
                return false;
            }
        }
        return true;
    }
}
