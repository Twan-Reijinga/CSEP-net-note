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

import commons.Collection;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeleteNoteActionTest {

    private DeleteNoteAction action;
    private Note note;
    private Collection collection;

    @BeforeEach
    void setUp() {
        collection = new Collection("name", "title");
        note = new Note("title", "content", collection);
        action = new DeleteNoteAction(note);
    }

    @Test
    void getNote() {
        assertEquals(note, action.getNote());
    }

    @Test
    void testEquals() {
        DeleteNoteAction action2 = new DeleteNoteAction(note);
        assertEquals(action, action2);
    }

    @Test
    void testHashCode() {
        DeleteNoteAction action2 = new DeleteNoteAction(note);
        assertEquals(action.hashCode(), action2.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("DeleteNoteAction{\n" +
                "noteId=0,\n" +
                "noteTitle='title',\n" +
                "noteContent='content',\n" +
                "}", action.toString());
    }
}