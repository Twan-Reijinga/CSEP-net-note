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
package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteTitle {

    private long id;
    private String title;

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
    public NoteTitle(String title, long id) {
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
     * Converter function to convert a Note into a NoteTitle object.
     * @param note Original Note.
     * @return NoteTitle with the same title and id as original Note.
     */
    public static NoteTitle fromNote(Note note) {
        return new NoteTitle(note.title, note.id);
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
     * generated hash base on the title and id of a specified NoteTitle object.
     * @return hash for title and id of object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, id);
    }

    /**
     * Getter for the title.
     * @return The title as a string.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title.
     * @param title New title to assign to NoteTitle object.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the id
     * @return id as a long type
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for the id
     * @param id New id to assign to NoteTitle object as a long.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * To string method for the NoteTitle using a multi line style.
     * @return The object as a human readably string.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(
                this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

}
