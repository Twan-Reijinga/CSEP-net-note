package server.services;

import commons.Collection;
import commons.Note;
import commons.NoteTitle;
import net.java.frej.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.api.CollectionController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SearchService {
    private final CollectionController collectionController;

    @Autowired
    public SearchService(CollectionController collectionController) {
        this.collectionController = collectionController;
    }

    /**
     * This method loads the necessary data for the search,
     * then it processes the given parameters, and after that it calls
     * the method noteContainsKeywords() for each loaded.
     *
     * @param id the ID of the collection to search within.
     * @param keywords the keywords to search for.
     * @param matchAll if true, all keywords must match; otherwise, any keyword can match.
     * @param searchIn specifies where to search (e.g., title, content).
     * @return a list of Notes that match the search query.
     */
    public List<NoteTitle> getSearchResults(UUID id, String keywords, boolean matchAll, String searchIn) {
        Collection coll = collectionController.getAllCollections().get(0);
        //List<Note> notesInCollection = collectionRepository.findById(id).get().notes;
        List<Note> notesInCollection = collectionController.getNotesInCollection(coll.id);
        List<NoteTitle> resultNotes = new ArrayList<>();

        int searchInValue = getSearchInValue(searchIn);

        String validated = validateUserInput(keywords);
        String[] words = new String[]{};
        boolean useRegularSearch = false;
        if (validated.isBlank()) {
            useRegularSearch = true;
            words = keywords.split("\\s+");
        }
        else words = validated.split("\\s+");


        // TODO: IMPLEMENT PROPER FILTERING BY CHOSEN COLLECTION

        for (Note note : notesInCollection) {
            if(noteContainsKeywords(note, words, matchAll, searchInValue, useRegularSearch)) {
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
    public boolean noteContainsKeywords(Note note, String[] keywords, boolean matchAll, int searchIn, boolean useRegularSearch) {
        boolean isInTitle = false;
        boolean isInContent = false;

        switch (searchIn) {
            case 1:
                if(useRegularSearch) isInTitle = matchesKeywordsRegular(keywords, note.title, matchAll);
                else isInTitle = matchesKeywordsFuzzy(keywords, note.title, matchAll);
                break;
            case 2:
                if(useRegularSearch) isInContent = matchesKeywordsRegular(keywords, note.content, matchAll);
                else isInContent = matchesKeywordsFuzzy(keywords, note.content, matchAll);
                break;
            default:
                if(useRegularSearch) {
                    isInTitle = matchesKeywordsRegular(keywords, note.title, matchAll);
                    isInContent = matchesKeywordsRegular(keywords, note.content, matchAll);
                }
                else{
                    isInTitle = matchesKeywordsFuzzy(keywords, note.title, matchAll);
                    isInContent = matchesKeywordsFuzzy(keywords, note.content, matchAll);
                }
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
    public boolean matchesKeywordsFuzzy(String[] keywords, String sequence, boolean all){
        String seqToLowerCase = sequence.toLowerCase();
        Regex pattern;
        double threshold = 0.4;

        if(all){
            for(String keyword : keywords){
                pattern = new Regex("(" + keyword.toLowerCase() + ")");
                pattern.setThreshold(threshold);
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
            pattern.setThreshold(threshold);
            if(pattern.presentInSequence(seqToLowerCase) != -1) return true;
        }

        return all;
    }

    /** This method takes the array of keywords given and performs a normal search
     * on a String sequence for every keyword.
     *
     * @param keywords the words that the sequence is searched for
     * @param sequence the string sequence on which the search is performed
     * @param all a boolean param to determine whether the sequence should match all keywords or not
     * @return true or false, whether the sequence contains the keywords or not
     */
    public boolean matchesKeywordsRegular(String[] keywords, String sequence, boolean all){
        String seqToLowerCase = sequence.toLowerCase();

        if(all){
            for(String keyword : keywords){
                if(!sequence.contains(keyword)) return false;
            }
        }
        else{
            for(String keyword : keywords){
                if(sequence.contains(keyword)) return true;
            }
        }
        return all;
    }

    /**
     * This method is used to get the numerical value for the searchIn variable.
     * @param searchIn the string value read from the http request
     * @return the numerical value corresponding to the string given
     */
    private int getSearchInValue(String searchIn){
        return switch (searchIn) {
            case "Title" -> 1;
            case "Content" -> 2;
            default -> 0;
        };
    }

    /**
     * This method is used to validate the input the user has given for the search request.
     * It removes all special characters that may cause issues for the fuzzy search library.
     * @param input The user's input
     * @return a new String where all characters that were not word characters,
     * whitespaces or "_" have been removed.
     */
    private String validateUserInput(String input){
        String specialChars = "[^\\w\\s]";
        String validated = input.replaceAll(specialChars, "");
        return validated;
    }
}
