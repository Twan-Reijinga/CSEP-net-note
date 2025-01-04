package client.utils;

public class NoteAction {

    public enum ActionType {
        ADD,
        DELETE;
    }

    private final ActionType actionType;

    public NoteAction(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
