package client.handlers;

import client.Main;
import client.config.Config;
import client.scenes.MainCtrl;
import client.scenes.NoteEditorCtrl;
import client.scenes.SidebarCtrl;
import client.utils.AddNoteAction;
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
        MainCtrl mainCtrl = new MainCtrl(config, new ServerUtilsRepository());
        SidebarCtrl sidebarCtrl = new SidebarCtrl(new ServerUtilsRepository(), config);
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