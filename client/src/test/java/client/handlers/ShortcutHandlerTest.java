package client.handlers;

import client.config.Config;
import client.scenes.SidebarCtrl;
import client.utils.AddNoteAction;
import client.utils.ServerUtils;
import commons.Collection;
import commons.Note;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortcutHandlerTest {

    @Test
    void recordAdd() {
        ShortcutHandler s = new ShortcutHandler(new SidebarCtrl(new ServerUtils(new Config()), new Config()));
        s.recordAdd(1L);
    }

    @Test
    void recordDelete() {
        ShortcutHandler s = new ShortcutHandler(new SidebarCtrl(new ServerUtils(new Config()), new Config()));
        Collection collection = new Collection();
        Note note = new Note("test", "test", collection);
        s.recordDelete(note);
    }
}