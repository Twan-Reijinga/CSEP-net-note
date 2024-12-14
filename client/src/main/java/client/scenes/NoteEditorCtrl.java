package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class NoteEditorCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    private ComboBox<String> languageDropDown;

    @FXML
    private AnchorPane sideBarContainer;

    @FXML
    private AnchorPane markdownPaneContainer;

    @FXML
    private Label appTitle;

    @FXML
    private AnchorPane topMostAnchor;

    @Inject
    public NoteEditorCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * JavaFX's method that automatically runs when this controller is initialized.
     * @param sideBarParent root element of the Sidebar fxml
     * @param markdownParent root element of the Markdown fxml
     */
    @FXML
    public void initialize(Parent sideBarParent, Parent markdownParent) {
        centerTextField();
        topMostAnchor.widthProperty().addListener((observable, oldValue, newValue) -> {
            centerTextField();
        });
        sideBarContainer.getChildren().add(sideBarParent);
        AnchorPane.setTopAnchor(sideBarParent, 0.0);
        AnchorPane.setBottomAnchor(sideBarParent, 0.0);
        markdownPaneContainer.getChildren().add(markdownParent);
        AnchorPane.setTopAnchor(markdownParent, 0.0);
        AnchorPane.setBottomAnchor(markdownParent, 0.0);
        AnchorPane.setLeftAnchor(markdownParent, 0.0);
        AnchorPane.setRightAnchor(markdownParent, 0.0);
        String[] availableLanguages = new String[] {"English", "Dutch", "Spanish"};
        languageDropDown.getItems().addAll(availableLanguages);
        languageDropDown.setOnAction(actionEvent -> {
            String chosenLanguage = languageDropDown.getSelectionModel().getSelectedItem();
            mainCtrl.changeUILanguage(chosenLanguage);
        });
    }

    /**
     * translates the NetNote title to always be center aligned to the anchor pane it is in
     */
    public void centerTextField() {
        double anchorWidth = topMostAnchor.getWidth();
        double textFieldWidth = appTitle.getWidth();
        appTitle.setLayoutX((anchorWidth - textFieldWidth) / 2);
        appTitle.relocate(appTitle.getLayoutX(), appTitle.getLayoutY());
    }
}
