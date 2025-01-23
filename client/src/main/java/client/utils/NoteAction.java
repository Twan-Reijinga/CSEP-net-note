package client.utils;

import java.util.Objects;

public abstract class NoteAction {

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NoteAction that = (NoteAction) o;
        return actionType == that.actionType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(actionType);
    }

    @Override
    public String toString() {
        return "NoteAction{" +
                "actionType=" + actionType +
                '}';
    }
}
