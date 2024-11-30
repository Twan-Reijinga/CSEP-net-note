package client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteTitle {

    private String title;
    private long id;

    public NoteTitle(String title, long id) {
        this.title = title;
        this.id = id;
    }

    public static List<NoteTitle> getDefaultNoteTitles() {
        // TODO: GET notes in collection
        List<NoteTitle> noteTitles = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            NoteTitle noteTitle = new NoteTitle("note title #" + i, i);
            noteTitles.add(noteTitle);
        }

        return noteTitles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NoteTitle noteTitle = (NoteTitle) o;
        return (id == noteTitle.id
                && Objects.equals(title, noteTitle.title));
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NoteTitle - " +
                "title: '" + title + '\'' +
                ", id:" + id;
    }

}
