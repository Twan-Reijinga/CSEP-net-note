package client.utils;

import commons.NoteTitle;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteLinkHandler {
    private ServerUtils serverUtils;

    @Inject
    public NoteLinkHandler(ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
    }

    /**
     * This method processes the input string and finds all links inside
     * using a specific regex pattern.
     * @param content the string to process
     * @return the list of all links inside the string.
     */
    private List<String> findNoteLinks(String content){
        String toMatch = content + " ";
        Pattern pattern = Pattern.compile("\\[\\[((?:(?!]]).)*)]]");
        Matcher matcher = pattern.matcher(toMatch);

        List<String> noteLinks = new ArrayList<>();
        while (matcher.find()) {
            noteLinks.add(matcher.group(1));
        }
        return noteLinks;
    }

    /**
     * This method processes the contents of a note. First, it finds the links inside by making a call
     * to findNoteLinks(). Then, it checks with the server which of those links are valid and
     * which are invalid, by comparing them to the list of all titles in the note's collection.
     * @param content the content of the note
     * @param collectionId the collection in which the note is stored
     * @return a hashmap, where the keys are the links and
     * the value is the id of the note they reference or null if they are invalid
     */
    public HashMap<String, Long> getLinks(String content, UUID collectionId){
        List<String> noteLinks = findNoteLinks(content);
        HashMap<String, Long> noteLinkMap = new HashMap<>();

        List<NoteTitle> noteTitles = this.serverUtils.getNoteTitlesInCollection(collectionId);

        for(String noteLink : noteLinks){
            Long idOfLinked = null;
            NoteTitle linkedNote = noteTitles.stream().filter(x -> x.getTitle().equals(noteLink))
                                                        .findFirst().orElse(null);
            if(linkedNote != null){
                idOfLinked = linkedNote.getId();
            }
            noteLinkMap.put(noteLink, idOfLinked);
        }
        return noteLinkMap;
    }
}
