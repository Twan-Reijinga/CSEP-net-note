package client.utils;

import commons.Note;
import commons.NoteTags;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagFilteringHandler {
    private List<NoteTags> loadedNoteTags;
    private ServerUtils serverUtils;
    private List<String> tagsSelected;

    public TagFilteringHandler(ServerUtils serverUtils) {
        loadedNoteTags = new ArrayList<>();
        this.serverUtils = serverUtils;
        tagsSelected = new ArrayList<>();
    }

    public void loadNewNoteTags(List<Long> noteIds){
        this.loadedNoteTags = this.serverUtils.getNoteTags(noteIds);
        clearTags();
    }

    public void onNoteDeleted(Long noteId){
        this.loadedNoteTags.removeIf(x -> x.getId().equals(noteId));
        displayNotes();
    }

    public void onNoteAdded(Note note){
        this.loadedNoteTags.add(extractNoteTags(note));
        // TODO what happens when the new note added, doesnt match the selected tags, should the tags be cleared?
        // clearTags();
    }

    public void onNoteUpdated(Note note){
        NoteTags currentTags = this.loadedNoteTags.stream()
                .filter(x -> x.getId().equals(note.id))
                .findFirst().get();
        currentTags.setTags(extractNoteTags(note).getTags());
    }

    public void clearTags(){
        this.tagsSelected.clear();
        displayNotes();
    }

    public void addTag(String tag){
        tagsSelected.add(tag);
        displayNotes();
    }

    public void removeTag(String tag){
        tagsSelected.remove(tag);
        displayNotes();
    }

    public void displayNotes(){
        List<Long> matching = new ArrayList<>();
        HashSet<String> availableTags = new HashSet<>();

        for(NoteTags noteTag: loadedNoteTags) {
            boolean hasAllTags = true;
            for(String tag: tagsSelected){
                if (!noteTag.getTags().contains(tag)) {
                    hasAllTags = false;
                    break;
                }
            }
            if(hasAllTags){
                matching.add(noteTag.getId());
                availableTags.addAll(noteTag.getTags());
            }
        }

        availableTags.removeAll(tagsSelected);

        //TODO tell the sidebar to load only the NoteTitles that match the tags
        // maybe set the others vboxpanels to visible false and disable them?
        // but then they might get selected after a deletion and the user wont see anything.
        // probably better to have it remember all NoteTitles in a list and only display certain parts of that list.
        // use observable list?
        // use availableTags to update the listBox options
    }

    public NoteTags extractNoteTags(Note note){
        HashSet<String> tags = new HashSet<>();
        Matcher matcher = Pattern.compile("#\\w+").matcher(note.content);
        while(matcher.find()){
            tags.add(matcher.group());
        }

        NoteTags noteTags = new NoteTags(note.id, tags);
        return noteTags;
    }
}
