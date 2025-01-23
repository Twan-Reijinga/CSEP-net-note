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
