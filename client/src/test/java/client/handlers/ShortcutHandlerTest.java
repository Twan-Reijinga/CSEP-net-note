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

import client.Main;
import client.config.Config;
import client.scenes.MainCtrl;
import client.scenes.NoteEditorCtrl;
import client.scenes.SidebarCtrl;
import client.utils.AddNoteAction;
import client.utils.NoteLinkHandler;
import client.utils.ServerUtils;
import client.utils.ServerUtilsRepository;
import commons.Collection;
import commons.Note;
import javafx.geometry.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortcutHandlerTest {

    private ShortcutHandler shortcutHandler;

    @BeforeEach
    void setUp() {
        Config config = new Config();
        MainCtrl mainCtrl = new MainCtrl(config, new ServerUtilsRepository(config));
        SidebarCtrl sidebarCtrl = new SidebarCtrl(new ServerUtilsRepository(config), config);
        this.shortcutHandler = new ShortcutHandler(mainCtrl, sidebarCtrl);
    }

    @Test
    void recordAdd() {
        shortcutHandler.recordAdd(1L);
    }

    @Test
    void recordDelete() {
        Collection collection = new Collection();
        Note note = new Note("test", "test", collection);
        shortcutHandler.recordDelete(note);
    }
}