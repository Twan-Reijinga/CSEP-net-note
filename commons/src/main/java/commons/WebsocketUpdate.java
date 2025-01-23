package commons;

public class WebsocketUpdate {
    public enum Opcode {
        NOTE_CREATE,
        NOTE_UPDATE,
        NOTE_DELETE,

        COLLECTION_CREATE,
        COLLECTION_UPDATE,
        COLLECTION_DELETE,
    }

    public Opcode opcode;
    public Collection collection;
    public Note note;

    public WebsocketUpdate() {}
    public WebsocketUpdate(Opcode opcode, Note note, Collection collection) {
        this.opcode = opcode;
        this.note = note;
        this.collection = collection;
    }
}
