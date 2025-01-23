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
    private UUID currentCollectionId;
    private List<NoteTitle> noteTitlesInCollection;

    @Inject
    public NoteLinkHandler(ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
        setNoteTitlesForCollection(null);
    }

    /**
     * Updates the local list which tracks note-links in the current collection.
     * Changes the title for the specified note if it's in the same collection as the one currently active.
     * @param title the new title it has.
     * @param id the id of the note.
     * @param collectionId the collection of the note
     */
    public void updateLink(String title, Long id, UUID collectionId) {
        if(this.currentCollectionId.equals(collectionId)) {
            this.noteTitlesInCollection.stream().filter(noteTitle -> noteTitle.getId() == id)
                                                .findFirst()
                                                .ifPresent(noteTitle -> {noteTitle.setTitle(title);});
        }
    }

    /**
     * Called when a new note is added. Adds the title and id to the local list used to check note-links only if
     * the newly created note is in the same colleciton as the one currently active.
     * @param collectionId the collection of the currently open note in the markdownEditorCtrl.
     */
    public void addLink(UUID collectionId) {
        if(this.currentCollectionId.equals(collectionId)) {
            setNoteTitlesForCollection(collectionId);
        }
    }

    /**
     * Called when a new note is deleted. Removes it's title and id from the local list
     * only if it is in the same colleciton as the note currently active.
     * @param title The title of the note that was removed.
     * @param collectionId the collection of the currently open note in the markdownEditorCtrl.
     */
    public void deleteLink(String title, UUID collectionId){
        if(this.currentCollectionId.equals(collectionId)){
            this.noteTitlesInCollection.removeIf(noteTitle -> noteTitle.getTitle().equals(title));
        }
    }

    /**
     * Reads all note titles in the collection of the active note.
     * Loads the noteTitles to a list that is later used to keep track of valid and invalid links.
     * @param collectionId the collection of the active note.
     */
    public void setNoteTitlesForCollection(UUID collectionId) {
        currentCollectionId = collectionId;
        noteTitlesInCollection = this.serverUtils.getNoteTitlesInCollection(currentCollectionId);
    }

    /**
     * Updates the noteTitles in the collection by reading them again from the server.
     */
    public void updateNoteTitlesInCollection() {
        setNoteTitlesForCollection(currentCollectionId);
    }

    /**
     * This method processes the input string and finds all links inside
     * using a specific regex pattern.
     * @param content the string to process
     * @return the list of all links inside the string.
     */
    private List<String> findNoteLinks(String content){
        Pattern pattern = Pattern.compile("\\[\\[((?:(?!]]).)*)]]");
        Matcher matcher = pattern.matcher(content);

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

        if(!collectionId.equals(currentCollectionId)){
            setNoteTitlesForCollection(collectionId);
        }

        for(String noteLink : noteLinks){
            Long idOfLinked = null;
            NoteTitle linkedNote = noteTitlesInCollection.stream().filter(x -> x.getTitle().equals(noteLink))
                                                        .findFirst().orElse(null);
            if(linkedNote != null){
                idOfLinked = linkedNote.getId();
            }
            noteLinkMap.put(noteLink, idOfLinked);
        }
        return noteLinkMap;
    }
}
