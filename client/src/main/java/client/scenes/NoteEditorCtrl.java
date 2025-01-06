package client.scenes;

import client.LoaderFXML;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import commons.Collection;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

import java.util.Timer;
import java.util.TimerTask;


public class NoteEditorCtrl {
    private Timer timeKeyPresses = new Timer();
    private int delayBetweenKeyPresses = 1000;

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
    private ComboBox<String> collectionDropdown;

    @FXML
    private ComboBox<String> languageDropdown;

    // Injectable
    private final LoaderFXML FXML;
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @FXML
    private CheckBox matchAllCheckBox;

    @FXML
    private ChoiceBox<String> searchInOptionsList;

    @FXML
    private ToggleButton advSearchButton;

    @FXML
    private HBox advSearchHBox;

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
        sidebarContainer.getChildren().add(sideBarParent);
        AnchorPane.setTopAnchor(sideBarParent, 0.0);
        AnchorPane.setBottomAnchor(sideBarParent, 0.0);

        markdownEditorContainer.getChildren().add(markdownParent);
        AnchorPane.setTopAnchor(markdownParent, 0.0);
        AnchorPane.setBottomAnchor(markdownParent, 0.0);
        AnchorPane.setLeftAnchor(markdownParent, 0.0);
        AnchorPane.setRightAnchor(markdownParent, 0.0);

        advSearchHBox.setSpacing(10.0);

        this.searchInOptionsList.getItems().clear();
        this.searchInOptionsList.getItems().addAll("Title", "Content", "Both");
        this.matchAllCheckBox.setSelected(true);

        loadLanguageDropdown();
        loadCollectionDropdown();
    }

    private void loadLanguageDropdown() {
        String[] availableLanguages = new String[] {"English", "Dutch", "Spanish"};
        languageDropdown.getItems().addAll(availableLanguages);
    }

    private void loadCollectionDropdown() {
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

    @FXML
    private void onLanguageDropdownAction() {
        String chosenLanguage = languageDropdown.getSelectionModel().getSelectedItem();
        mainCtrl.changeUILanguage(chosenLanguage);
    }


    private void openCollectionSettings() {
        var popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with the main window
        popupStage.setTitle("Edit collections...");

        var popup = FXML.load(CollectionSettingsCtrl.class, null,"client", "scenes", "CollectionSettings.fxml");

        var popupNode = popup.getValue();
        var popupScene = new Scene(popupNode);

        popupStage.setOnCloseRequest(_ -> loadCollectionDropdown());

        popupStage.setScene(popupScene);
        popupStage.show();
    }


    /** Called upon clicking the search button
     *  Calls the sendSearchRequest method from the mainCtrl with the text from the searchBox.
     *  Currently, nothing happens if no text is present in the search box.
     */
    public void onSearchButtonPressed(){
        String searchText = searchBox.getText();
        boolean matchAll = this.matchAllCheckBox.isSelected();
        String whereToSearch = this.searchInOptionsList.getSelectionModel().getSelectedItem();
        long collectionId = 0;
        if(!searchText.isEmpty()){
            mainCtrl.sendSearchRequest(searchText, collectionId, matchAll, whereToSearch);
        }
        else{
            mainCtrl.refreshSideBar();
        }
    }

    /** Called everytime a key event is detected in the searchBar, schedules a timer
     *  for delayBetweenKeyPresses milliseconds after which the onSearchButtonPressed()
     *  method is called to perform a search. Consecutive calls cancel previous timers
     *  and schedule new ones, therefore the callback is called only delayBetweenKeyPresses
     *  after the last keypress.
     */
    public void onSearchBarInput() {
        timeKeyPresses.cancel();
        timeKeyPresses = new Timer();

        timeKeyPresses.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> onSearchButtonPressed());
            }
        }, delayBetweenKeyPresses);

    }

    /** This method expands the topMostAnchor Pane and reveals a checkbox and a choice box whose
     * values are used in the search to make it broader or more specific depending on the choice.
     */
    public void onAdvSearchButtonPressed(){
        boolean selected = advSearchButton.isSelected();
        if(selected){
            advSearchHBox.setPrefHeight(advSearchHBox.getPrefHeight() + 30);
        }
        else{
            advSearchHBox.setPrefHeight(advSearchHBox.getPrefHeight() - 30);
        }
        matchAllCheckBox.setDisable(!selected);
        searchInOptionsList.setDisable(!selected);
        matchAllCheckBox.setVisible(selected);
        searchInOptionsList.setVisible(selected);
    }
}
