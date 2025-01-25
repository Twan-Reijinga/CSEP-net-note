package commons;

import java.util.UUID;

public class WebsocketUpdate {
    public enum Opcode {
        NOTE_CREATE,
        NOTE_UPDATE,
        NOTE_DELETE,

        COLLECTION_CREATE,
        COLLECTION_UPDATE,
        COLLECTION_DELETE,

        DEFAULT_COLLECTION_CHANGED
    }

    public Opcode opcode;
    public Collection collection;
    public Note note;
    public UUID defaultId;

    public WebsocketUpdate() {}
    public WebsocketUpdate(Opcode opcode, Note note, Collection collection, UUID defaultId) {
        this.opcode = opcode;
        this.note = note;
        this.collection = collection;
        this.defaultId = defaultId;
    }
}
