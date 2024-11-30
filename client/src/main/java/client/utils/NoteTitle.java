package client.utils;

import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteTitle {

    private String title;

    @Id
    private int id;

    public NoteTitle() {}

    public NoteTitle(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public static List<NoteTitle> getDefaultNoteTitles() {
        // for testing //
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NoteTitle - " +
                "title: '" + title + '\'' +
                ", id:" + id;
    }

}
