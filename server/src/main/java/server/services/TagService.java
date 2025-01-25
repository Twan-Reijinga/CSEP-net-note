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

        Pattern bracketPattern = Pattern.compile("\\[\\[.*?]]");
        Matcher bracketMatcher = bracketPattern.matcher(note.content);
        boolean[] ignore = new boolean[note.content.length()];

        while (bracketMatcher.find()) {
            for (int j = bracketMatcher.start(); j < bracketMatcher.end(); j++) {
                ignore[j] = true;
            }
        }

        Matcher matcher = Pattern.compile("#\\w+").matcher(note.content);
        while(matcher.find()){
            boolean isIgnored = this.ignoreText(matcher.start(), matcher.end(), ignore);
            if (!isIgnored) {
                tags.add(matcher.group());
            }
        }

        NoteTags noteTags = new NoteTags(note.id, tags);
        return noteTags;
    }

    private boolean ignoreText(int start, int end, boolean[] ignore){
        for (int j = start; j < end; j++) {
            if (ignore[j]) {
                return true;
            }
        }
        return false;
    }
}
