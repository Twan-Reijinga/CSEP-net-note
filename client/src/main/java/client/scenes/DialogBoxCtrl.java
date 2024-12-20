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
package client.scenes;

import client.MyFXML;
import client.MyModule;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import static com.google.inject.Guice.createInjector;

/**
 * The DialogBoxCtrl class is responsible for creating and managing a dialog box in a JavaFX application.
 * It provides methods to customize the dialog's title, content, buttons, modality, and style.
 */
public class DialogBoxCtrl {
    private static final MyFXML FXML = new MyFXML(createInjector(new MyModule()));

    /**
     * Creates a new instance of DialogBoxCtrl and initializes the dialog box with the default template.
     * Use this instead of the default constructor.
     * @return a new instance of DialogBoxCtrl
     */
    public static DialogBoxCtrl createDialog() {
        var pair = FXML.load(DialogBoxCtrl.class, null, "client", "scenes", "DialogTemplate.fxml");
        var inst = pair.getKey();
        inst.mainStage.setScene(new Scene(pair.getValue()));
        inst.mainStage.sizeToScene();

        inst.withModality(Modality.WINDOW_MODAL)
                .withStyle(StageStyle.UTILITY)
                .allowResize(false);
        return inst;
    }


    @FXML
    public AnchorPane titleContainer;

    @FXML
    public Label title;

    @FXML
    public VBox content;

    @FXML
    public HBox footer;

    public final Stage mainStage;

    /**
     * Constructor used by tools
     * @see #createDialog() for actually creating a dialog box
     */
    public DialogBoxCtrl() {
        mainStage = new Stage();
    }

    /**
     * Displays the dialog box.
     */
    public void show() { mainStage.show();}

    /**
     * Displays the dialog box and waits for it to be closed before returning.
     */
    public void showAndWait() { mainStage.showAndWait();}

    /**
     * Sets the message of the dialog box, replacing any existing content.
     *
     * @param text the message to display
     * @return the current instance of DialogBoxCtrl for method chaining
     */
    public DialogBoxCtrl withMessage(String text) {
        var label = new Label(text);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        return withContent(label);
    }

    /**
     * Sets the content of the dialog box, replacing any existing content.
     *
     * @param content the new content to display
     * @return the current instance of DialogBoxCtrl for method chaining
     */
    public DialogBoxCtrl withContent(Node content) {
        this.content.getChildren().clear();
        return appendContent(content);
    }

    /**
     * Appends additional content to the dialog box.
     *
     * @param content the content to append
     * @return the current instance of DialogBoxCtrl for method chaining
     */
    public DialogBoxCtrl appendContent(Node content) {
        this.content.getChildren().add(content);
        return this;
    }

    /**
     * Sets the modality of the dialog box.
     *
     * @param modality the modality to set
     * @return the current instance of DialogBoxCtrl for method chaining
     */
    public DialogBoxCtrl withModality(Modality modality) {
        this.mainStage.initModality(modality);
        return this;
    }

    /**
     * Sets the title of the dialog box.
     *
     * @param title the title to set
     * @return the current instance of DialogBoxCtrl for method chaining
     */
    public DialogBoxCtrl withTitle(String title) {
        this.title.setText(title);
        return this;
    }

    /**
     * Sets the style of the dialog box.
     *
     * @param style the style to set
     * @return the current instance of DialogBoxCtrl for method chaining
     */
    public DialogBoxCtrl withStyle(StageStyle style) {
        this.mainStage.initStyle(style);
        return this;
    }

    /**
     * Enables or disables the resizing of the dialog box.
     *
     * @param enabled true to allow resizing, false to disallow
     * @return the current instance of DialogBoxCtrl for method chaining
     */
    public DialogBoxCtrl allowResize(boolean enabled) {
        this.mainStage.setResizable(enabled);
        return this;
    }

    /**
     * Appends a button to the dialog box footer with an associated action.
     *
     * @param label        the label of the button
     * @param action       the action to perform when the button is clicked
     */
    public DialogBoxCtrl appendButton(String label, EventHandler<ActionEvent> action, boolean closesDialog) {
        var button = new Button(label);
        if(closesDialog)
            button.setOnAction(e -> {
                action.handle(e);
                if(!e.isConsumed())
                    mainStage.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            });
        else
            button.setOnAction(action);
        footer.getChildren().add(button);
        return this;
    }
}

