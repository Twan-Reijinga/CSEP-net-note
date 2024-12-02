package client.utils;

import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteTitle {

    private String title;

    @Id
    private int id;

    /**
     * Default constructor used to create a NoteTitle object from a JSON file.
     * When used, id and title still need to be specified later.
     */
    public NoteTitle() {}

    /**
     * Constructor for the NoteTitle that initializes all the private fields of the object.
     * @param title The title of the note stored in the database, could be edited later.
     * @param id Identifier that is linked to the note in the database to the specific note
     *           the id is unique for every note.
     */
    public NoteTitle(String title, int id) {
        this.title = title;
        this.id = id;
    }

    /**
     * A function that generated a list of some NoteTitle objects that can be used to
     * test without needing to depend on server functionality.
     * @return list of default NodeTitle object with title and id.
     */
    public static List<NoteTitle> getDefaultNoteTitles() {
        // for testing //
        List<NoteTitle> noteTitles = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            NoteTitle noteTitle = new NoteTitle("note title #" + i, i);
            noteTitles.add(noteTitle);
        }

        return noteTitles;
    }

    /**
     * Equals method for the NoteTitle.
     * Title and id need to be the same to be equal.
     * @param o Other object to compare to.
     * @return Boolean that indicates if Object o is equal to this NoteTitle object.
     */
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

    /**
     * generated hash base on the title and id of a specified NoteTitle object
     * @return hash for title and id of object
     */
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
