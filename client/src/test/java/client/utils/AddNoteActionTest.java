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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddNoteActionTest {

    private AddNoteAction action;

    @BeforeEach
    void setUp() {
        action = new AddNoteAction(1);
    }

    @Test
    void getId() {
        assertEquals(1, action.getId());
    }

    @Test
    void testEquals() {
        AddNoteAction action2 = new AddNoteAction(1);
        assertEquals(action, action2);
    }

    @Test
    void testHashCode() {
        AddNoteAction action2 = new AddNoteAction(1);
        assertEquals(action.hashCode(), action2.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("AddNoteAction{noteId=1}", action.toString());
    }
}