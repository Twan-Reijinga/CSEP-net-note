package server.api;

import java.util.*;
import commons.Collection;
import commons.Note;
import commons.NoteTitle;
import net.java.frej.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.database.NoteRepository;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final NoteRepository noteRepository;

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
    @GetMapping(path = "/search/{id}/{keywords}/{matchAll}/{searchIn}")
    public List<NoteTitle> searchNotesMatchAll(@PathVariable String id,
                                               @PathVariable String keywords,
                                               @PathVariable String matchAll,
                                               @PathVariable String searchIn) {
        List<NoteTitle> resultNotes = new ArrayList<>();
        String[] words = keywords.split(" ");
        Collection coll = (Collection)collectionRepository.findAll().toArray()[0];
        //List<Note> notesInCollection = collectionRepository.findById(Long.parseLong(id)).get().notes;
        List<Note> notesInCollection = coll.notes;


        for (Note note : notesInCollection) {
            if(noteContainsKeywords(note, words, Boolean.valueOf(matchAll), Integer.valueOf(searchIn))) {
                NoteTitle nt = new NoteTitle(note.title, note.id);
                resultNotes.add(nt);
            }
        }

        return resultNotes;
    }

    /** This note performs the search for keywords in
     *  the title and content of a specified note.
     *
     * @param note The note which we check for keywords
     * @param keywords The keywords we are searching for
     * @return true or false, depending on whether the
     *         note contains every keyword or not.
     */
    public boolean noteContainsKeywords(Note note, String[] keywords, boolean matchAll, int searchIn) {
        boolean isInTitle = false;
        boolean isInContent = false;

        switch (searchIn) {
            case 1:
                isInTitle = matchesKeywords(keywords, note.title, matchAll);
                break;
            case 2:
                isInContent = matchesKeywords(keywords, note.content, matchAll);
                break;
            default:
                isInTitle = matchesKeywords(keywords, note.title, matchAll);
                isInContent = matchesKeywords(keywords, note.content, matchAll);
                break;
        }

        return isInTitle || isInContent;
    }

    public boolean matchesKeywords(String[] keywords, String sequence, boolean all){
        String seqToLowerCase = sequence.toLowerCase();
        Regex pattern;

        for(String keyword : keywords){
            pattern = new Regex("(" + keyword.toLowerCase() + ")");

            if(all && pattern.presentInSequence(seqToLowerCase) == -1) return false;
            if(!all && pattern.presentInSequence(seqToLowerCase) != -1) return true;
        }

        return all;
    }
}
