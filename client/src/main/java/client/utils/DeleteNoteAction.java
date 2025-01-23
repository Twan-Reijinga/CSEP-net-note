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
package client.utils;

import commons.Note;

import java.util.Objects;

public class DeleteNoteAction extends NoteAction {
    private Note note;

    public DeleteNoteAction(Note note) {
        super(ActionType.DELETE);
        this.note = note;
    }

    public Note getNote() {
        return note;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeleteNoteAction that = (DeleteNoteAction) o;
        return Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), note);
    }

    @Override
    public String toString() {
        return "DeleteNoteAction{" + "\n" +
                "noteId=" + note.id + ",\n" +
                "noteTitle='" + note.title + "\',\n" +
                "noteContent='" + note.content + "\',\n" +
                '}';
    }
}
