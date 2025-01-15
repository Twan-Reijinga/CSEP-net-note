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

    private List<String> findNoteLinks(String content){
        Pattern pattern = Pattern.compile("\\[\\[((?:(?!]]).)*)]]");
        Matcher matcher = pattern.matcher(content);

        List<String> noteLinks = new ArrayList<>();
        while (matcher.find()) {
            noteLinks.add(matcher.group(1));
        }
        return noteLinks;
    }

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
