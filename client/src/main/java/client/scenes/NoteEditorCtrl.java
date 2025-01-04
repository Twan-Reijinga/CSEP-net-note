package client.scenes;

import client.LoaderFXML;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import commons.Collection;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class NoteEditorCtrl {
    @FXML
    private ComboBox<String> languageDropDown;

    @FXML
    private AnchorPane sideBarContainer;

    @FXML
    private AnchorPane markdownPaneContainer;

    @FXML
    private Label appTitle;

    @FXML
    private TextField searchBox;

    @FXML
    private Button searchButton;

    @FXML
    private ComboBox<String> collectionDropdown;

    @FXML
    private AnchorPane topMostAnchor;

    // Injectable
    private final LoaderFXML FXML;
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @Inject
    public NoteEditorCtrl(LoaderFXML FXML, ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.FXML = FXML;
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * JavaFX method that automatically runs when this controller is initialized.
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

        loadCollectionsDropdown();
    }

    private void loadCollectionsDropdown() {
        List<Collection> collections = serverUtils.getAllCollections();
        List<String> titles = collections.stream().map(c -> c.title).toList();

        collectionDropdown.getItems().clear();

        collectionDropdown.getItems().add("Show all");
        collectionDropdown.getItems().addAll(titles);
        collectionDropdown.getItems().add("Edit collections...");
    }

    @FXML
    private void onCollectionDropdownAction() {
        String selectedItem = collectionDropdown.getSelectionModel().getSelectedItem();

        if (selectedItem != null && selectedItem.equals("Edit collections...")) {
            openCollectionSettings();
        }
    }

    private void openCollectionSettings() {
        var popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with the main window
        popupStage.setTitle("Edit collections...");

        var popup = FXML.load(CollectionSettingsCtrl.class, null,"client", "scenes", "CollectionSettings.fxml");

        var popupNode = popup.getValue();
        var popupScene = new Scene(popupNode);

        popupStage.setOnCloseRequest(_ -> loadCollectionsDropdown());

        popupStage.setScene(popupScene);
        popupStage.show();
    }

    /**
     * translates the NetNote title to always be center aligned to the anchor pane it is in
     */
    private void centerTextField() {
        double anchorWidth = topMostAnchor.getWidth();
        double textFieldWidth = appTitle.getWidth();
        appTitle.setLayoutX((anchorWidth - textFieldWidth) / 2);
        appTitle.relocate(appTitle.getLayoutX(), appTitle.getLayoutY());
    }
}
