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

import client.scenes.DialogBoxCtrl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.function.Consumer;

/**
 * The DialogBoxUtils class provides utility methods for creating various types of dialog boxes
 * using the DialogBoxCtrl class. It simplifies the process of creating dialogs with predefined
 * buttons and actions.
 */
public class DialogBoxUtils {

    /**
     * Creates a simple dialog with a title, content message, and a single button.
     *
     * @param title       the title of the dialog
     * @param content     the content message to display in the dialog
     * @param buttonLabel the label of the button
     * @param action      the action to perform when the button is clicked
     * @return a DialogBoxCtrl instance configured with the specified title, content, and button
     */
    public static DialogBoxCtrl createSimpleDialog(
            String title, String content,
            String buttonLabel, EventHandler<ActionEvent> action
    ) {
        var dialog = DialogBoxCtrl.createDialog().withTitle(title).withMessage(content);
        dialog.appendButton(buttonLabel, action, true);
        return dialog;
    }

    /**
     * Creates a simple dialog with a title, content message, and two buttons.
     *
     * @param title       the title of the dialog
     * @param content     the content message to display in the dialog
     * @param buttonLabel1 the label of the first button
     * @param action1     the action to perform when the first button is clicked
     * @param buttonLabel2 the label of the second button
     * @param action2     the action to perform when the second button is clicked
     * @return a DialogBoxCtrl instance configured with the specified title, content, and buttons
     */
    public static DialogBoxCtrl createSimpleDialog(
            String title, String content,
            String buttonLabel1, EventHandler<ActionEvent> action1,
            String buttonLabel2, EventHandler<ActionEvent> action2
    ) {
        var dialog = DialogBoxCtrl.createDialog().withTitle(title).withMessage(content);
        dialog.appendButton(buttonLabel1, action1, true);
        dialog.appendButton(buttonLabel2, action2, true);
        return dialog;
    }

    /**
     * Creates a simple dialog with a title, content message, and three buttons.
     *
     * @param title       the title of the dialog
     * @param content     the content message to display in the dialog
     * @param buttonLabel1 the label of the first button
     * @param action1     the action to perform when the first button is clicked
     * @param buttonLabel2 the label of the second button
     * @param action2     the action to perform when the second button is clicked
     * @param buttonLabel3 the label of the third button
     * @param action3     the action to perform when the third button is clicked
     * @return a DialogBoxCtrl instance configured with the specified title, content, and buttons
     */
    public static DialogBoxCtrl createSimpleDialog(
            String title, String content,
            String buttonLabel1, EventHandler<ActionEvent> action1,
            String buttonLabel2, EventHandler<ActionEvent> action2,
            String buttonLabel3, EventHandler<ActionEvent> action3
    ) {
        var dialog = DialogBoxCtrl.createDialog().withTitle(title).withMessage(content);
        dialog.appendButton(buttonLabel1, action1, true);
        dialog.appendButton(buttonLabel2, action2, true);
        dialog.appendButton(buttonLabel3, action3, true);
        return dialog;
    }

    /**
     * Creates a dialog with an "Ok" button and a specified title and message.
     *
     * @param title   the title of the dialog
     * @param message the message to display in the dialog
     * @return a DialogBoxCtrl instance configured with the specified title and message
     */
    public static DialogBoxCtrl createOkDialog(String title, String message) {
        return createSimpleDialog(title, message, "Ok", (_) -> {});
    }

    /**
     * Creates a Yes/No dialog with specified title and message, and a consumer to handle the user's choice.
     *
     * @param title   the title of the dialog
     * @param message the message to display in the dialog
     * @param action  a consumer that accepts a Boolean indicating the user's choice (true for Yes, false for No)
     * @return a DialogBoxCtrl instance configured with the specified title and message
     */
    public static DialogBoxCtrl createYesNoDialog(String title, String message, Consumer<Boolean> action) {
        return createSimpleDialog(title, message,
                "Yes", (_) -> { action.accept(true); },
                "No", (_) -> { action.accept(false);}
        );
    }

    /**
     * Creates a dialog with "Ok" and "Cancel" buttons, along with a specified title and message.
     *
     * @param title   the title of the dialog
     * @param message the message to display in the dialog
     * @param action  a consumer that accepts a Boolean indicating the user's choice (true for Ok, false for Cancel)
     * @return a DialogBoxCtrl instance configured with the specified title and message
     */
    public static DialogBoxCtrl createOkCancelDialog(String title, String message, Consumer<Boolean> action) {
        return createSimpleDialog(title, message,
                "Ok", (_) -> { action.accept(true); },
                "Cancel", (_) -> { action.accept(false);}
        );
    }
}
