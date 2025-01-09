package server.services;

import commons.Note;
import commons.NoteTags;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TagService {
    public NoteTags getTags(Note note){
        HashSet<String> tags = new HashSet<>();
        Matcher matcher = Pattern.compile("#\\w+").matcher(note.content);
        while(matcher.find()){
            tags.add(matcher.group());
        }

        NoteTags noteTags = new NoteTags(note.id, tags);
        return noteTags;
    }
}