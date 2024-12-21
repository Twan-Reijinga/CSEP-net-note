package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class NoteEditorCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane sidebarContainer;

    @FXML
    private AnchorPane markdownEditorContainer;

    @FXML
    private TextField searchBox;

    @FXML
    private Button searchButton;

    @FXML
    private ToggleButton advancedSearchButton;

    @FXML
    private ComboBox<String> collectionsDropdown;

    @FXML
    private ComboBox<String> languageDropdown;

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
        sidebarContainer.getChildren().add(sideBarParent);
        AnchorPane.setTopAnchor(sideBarParent, 0.0);
        AnchorPane.setBottomAnchor(sideBarParent, 0.0);
        markdownEditorContainer.getChildren().add(markdownParent);
        AnchorPane.setTopAnchor(markdownParent, 0.0);
        AnchorPane.setBottomAnchor(markdownParent, 0.0);
        AnchorPane.setLeftAnchor(markdownParent, 0.0);
        AnchorPane.setRightAnchor(markdownParent, 0.0);
        String[] availableLanguages = new String[] {"English", "Dutch", "Spanish"};
        languageDropdown.getItems().addAll(availableLanguages);
        languageDropdown.setOnAction(actionEvent -> {
            String chosenLanguage = languageDropdown.getSelectionModel().getSelectedItem();
            mainCtrl.changeUILanguage(chosenLanguage);
        });
    }
}
