package client.utils;

import commons.Note;
import commons.NoteTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TagFilteringHandler {
    private List<NoteTags> loadedNoteTags;
    private ServerUtils serverUtils;
    private List<String> tagsSelected;
    private HashSet<String> availableTags;

    public TagFilteringHandler(ServerUtils serverUtils) {
        loadedNoteTags = new ArrayList<>();
        this.serverUtils = serverUtils;
        tagsSelected = new ArrayList<>();
        availableTags = new HashSet<>();
    }

    public void loadNewNoteTags(List<Long> noteIds){
        this.loadedNoteTags = this.serverUtils.getNoteTags(noteIds);
    }

    public void deleteNoteTags(Long noteId){
        this.loadedNoteTags.removeIf(x -> x.getId().equals(noteId));
    }

    public void addNoteTags(Note note){
        this.loadedNoteTags.add(convertToNoteTags(note));
    }

    public List<String> updateNoteTags(Note note){
        NoteTags currentTags = this.loadedNoteTags.stream()
                .filter(x -> x.getId().equals(note.id))
                .findFirst().get();
        HashSet<String> newTags = convertToNoteTags(note).getTags();

        HashSet<String> removedTags = new HashSet<>();
        for(String tag: currentTags.getTags()){
            if(!newTags.contains(tag)){
                removedTags.add(tag);
            }
        }
        currentTags.setTags(newTags);
        return this.checkIfTagsOrphaned(removedTags);
    }

    public void clearTags(){
        this.tagsSelected.clear();
    }

    public void addTag(String tag){
        tagsSelected.add(tag);
    }

    public void removeTag(String tag){
        tagsSelected.remove(tag);
    }

    public List<String> getAvailableTags() {
        List<String> availableTagsList = new ArrayList<>(availableTags.stream().toList());
        Collections.sort(availableTagsList);
        return availableTagsList;
    }

    public List<Long> getNotesToDisplay(){
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
        this.availableTags = availableTags;
        return matching;
    }

    public NoteTags convertToNoteTags(Note note){
        HashSet<String> tags = new HashSet<>();
        Matcher matcher = Pattern.compile("#\\w+").matcher(note.content);
        while(matcher.find()){
            tags.add(matcher.group());
        }

        NoteTags noteTags = new NoteTags(note.id, tags);
        return noteTags;
    }

    public List<String> checkIfTagsOrphaned(HashSet<String> removedTags) {
        List<String> allTags = this.loadedNoteTags.stream().map(x -> x.getTags())
                .flatMap(hashSet -> hashSet.stream())
                .collect(Collectors.toList());
        HashSet<String> tags = new HashSet<>(allTags);

        List<String> removed = new ArrayList<>();
        for (String tag : removedTags) {
            if (!tags.contains(tag)) {
                this.removeTag(tag);
                removed.add(tag);
            }
        }

        return removed;
    }
}
