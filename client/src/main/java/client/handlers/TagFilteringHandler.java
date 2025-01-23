/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.handlers;

import client.utils.ServerUtils;
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

    /**
     * Used to load all NoteTags objects corresponding to the NoteTitles loaded into the sidebar.
     * @param noteIds the ids of the notes that will be used to create the NoteTags
     */
    public void loadNewNoteTags(List<Long> noteIds){
        this.loadedNoteTags = this.serverUtils.getNoteTags(noteIds);
    }

    /**
     * Removes a NoteTags object from the list of all objects.
     * Used when a note is deleted through the sidebar.
     * @param noteId the id of the NoteTags object to remove
     */
    public void deleteNoteTags(Long noteId){
        this.loadedNoteTags.removeIf(x -> x.getId().equals(noteId));
    }

    /**
     * Adds a new NoteTags object to the list.
     * Used when a note is created through the sideBar.
     * @param note the note that will be used for creating a new NoteTags object
     */
    public void addNoteTags(Note note){
        this.loadedNoteTags.add(convertToNoteTags(note));
    }

    /**
     * This method is called when a note is updated to update it's corresponding NoteTags object.
     * Added tags are recorder in the NoteTags object.
     * Removed tags are passed to the removeTagsIfOrphaned() method.
     * @param note The note which had it's content updated.
     * @return a list with all tags that were removed from the removeTagsIfOrphaned() method.
     */
    public List<String> updateNoteTags(Note note){
        NoteTags currentTags = this.loadedNoteTags.stream()
                .filter(x -> x.getId().equals(note.id))
                .findFirst().orElse(null);
        HashSet<String> removedTags = new HashSet<>();
        HashSet<String> updatedTags = convertToNoteTags(note).getTags();

        if(currentTags != null){
            for(String tag: currentTags.getTags()){
                if(!updatedTags.contains(tag)){
                    removedTags.add(tag);
                }
            }
            currentTags.setTags(updatedTags);
        }
        else{
            removedTags.addAll(updatedTags);
        }
        return this.removeTagsIfOrphaned(removedTags);
    }

    /**
     * Clears all selected tag filters.
     */
    public void clearTags(){
        this.tagsSelected.clear();
    }

    /**
     * Adds a tag used for filtering.
     * @param tag the tag to add
     */
    public void addTag(String tag){
        tagsSelected.add(tag);
    }

    /**
     * Removes a tag from the selected for filtering.
     * @param tag the tag to remove
     */
    public void removeTag(String tag){
        tagsSelected.remove(tag);
    }

    /**
     * Used to get the available for further filtering tags.
     * @return a sorted list of the tags available
     */
    public List<String> getAvailableTags() {
        List<String> availableTagsList = new ArrayList<>(availableTags.stream().toList());
        Collections.sort(availableTagsList);
        return availableTagsList;
    }

    /**
     * Checks which notes match the selected tag filters and from them finds the tags
     * left as available options for further filtering.
     * @return the id's of the notes that match the filters selected.
     */
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

    /**
     * Used to extract the tags from a Note and create a NoteTags object.
     * @param note the Note used to create the NoteTags object.
     * @return an object containing the id of the Note it represents and
     * a HashSet containing all tags in said Note.
     */
    public NoteTags convertToNoteTags(Note note){
        HashSet<String> tags = new HashSet<>();
        Matcher matcher = Pattern.compile("#\\w+").matcher(note.content);
        while(matcher.find()){
            tags.add(matcher.group());
        }

        NoteTags noteTags = new NoteTags(note.id, tags);
        return noteTags;
    }

    /**
     * Used when a note is updated and some of its tags were removed.
     * Checks whether any other note contains those tags, if not, removes them and
     * later returns the removed.
     * @param removedTags The tags that were removed from a single note
     * @return the tags that were removed.
     */
    public List<String> removeTagsIfOrphaned(HashSet<String> removedTags) {
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
