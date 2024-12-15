package server.service;

import commons.Collection;
import commons.Note;
import commons.NoteTitle;
import net.java.frej.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.CollectionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private final CollectionRepository collectionRepository;

    @Autowired
    public SearchService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public List<NoteTitle> getSearchResults(long id, String keywords, boolean matchAll, int searchIn){
        String[] words = keywords.split(" ");
        Collection coll = (Collection)collectionRepository.findAll().toArray()[0];
        //List<Note> notesInCollection = collectionRepository.findById(id).get().notes;
        List<Note> notesInCollection = coll.notes;

        List<NoteTitle> resultNotes = new ArrayList<>();

        for (Note note : notesInCollection) {
            if(noteContainsKeywords(note, words, matchAll, searchIn)) {
                NoteTitle nt = new NoteTitle(note.title, note.id);
                resultNotes.add(nt);
            }
        }

        return resultNotes;
    }

    /** This method searches a note for the keywords specified using the additional options
     *  to determine which fields of the note are searched.
     *
     * @param note The note which we check for keywords
     * @param keywords The keywords we are searching for
     * @param matchAll boolean indicator whether a note should contain all keywords or not
     * @param searchIn int number used to set different options for searching different parts of a note
     * @return true or false, depending on whether the note mathces the search or not.
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

    /** This method takes the array of keywords given and performs a fuzzy search
     * on a String sequence for every keyword. A keyword is considered present in the sequence
     * if the Demerau-Levenstein distance between it and a part of the sequence over the length of the keyword
     * is no more than 0.34. Meaning that at least 2/3 of the keyword should be spelled correctly.
     *
     * @param keywords the words that the sequence is searched for
     * @param sequence the string sequence on which the search is performed
     * @param all a boolean param to determine whether the sequence should match all keywords or not
     * @return true or false, whether the sequence passes the search or not
     */
    public boolean matchesKeywords(String[] keywords, String sequence, boolean all){
        String seqToLowerCase = sequence.toLowerCase();
        Regex pattern;

        if(all){
            for(String keyword : keywords){
                pattern = new Regex("(" + keyword.toLowerCase() + ")");
                if(pattern.presentInSequence(seqToLowerCase) == -1) return false;
            }
        }
        else{
            String anyPattern = "{^";
            for(int i = 0; i < keywords.length; i++){
                anyPattern += keywords[i].toLowerCase();

                if(i != keywords.length - 1) anyPattern += ",";
            }
            anyPattern += "}";

            pattern = new Regex(anyPattern);
            if(pattern.presentInSequence(seqToLowerCase) != -1) return true;
        }

        return all;
    }
}
