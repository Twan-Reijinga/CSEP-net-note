package client.scenes;

import client.LoaderFXML;
import client.utils.LanguageListCel;
import client.utils.LanguageOption;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import commons.Collection;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.*;


public class NoteEditorCtrl {
    @FXML
    private AnchorPane sidebarContainer;

    @FXML
    private AnchorPane markdownEditorContainer;

    @FXML
    private TextField searchBox;

    @FXML
    private Button searchButton;

    @FXML
    private CheckBox matchAllCheckBox;

    @FXML
    private ChoiceBox<String> searchInOptionsList;

    @FXML
    private ToggleButton advSearchButton;

    @FXML
    private HBox advSearchHBox;

    @FXML
    private ComboBox<Pair<UUID, String>> collectionDropdown;

    @FXML
    private ComboBox<LanguageOption> languageDropdown;

    // Injectable
    private final LoaderFXML FXML;
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    // Search debouncing parameters
    private Timer timeKeyPresses = new Timer();
    private int delayBetweenKeyPresses = 1000;

    private SidebarCtrl sidebarCtrl;
    private MarkdownEditorCtrl markdownEditorCtrl;
    private ResourceBundle bundle;

    private Pair<UUID, String> showAll;
    private Pair<UUID, String> editCollections;

    private Pair<UUID, String> chosenCollectionFilter = showAll;

    @Inject
    public NoteEditorCtrl(LoaderFXML FXML, ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.FXML = FXML;
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * JavaFX method that automatically runs when this controller is initialized.
     * @param sidebar root element of the Sidebar fxml
     * @param markdownEditor root element of the Markdown fxml
     * @param bundle resource bundle for current language
     */
    @FXML
    public void initialize(Pair<SidebarCtrl,
                           Parent> sidebar,
                           Pair<MarkdownEditorCtrl, Parent> markdownEditor,
                           ResourceBundle bundle) {
        sidebarCtrl = sidebar.getKey();
        Node sidebarNode = sidebar.getValue();

        markdownEditorCtrl = markdownEditor.getKey();
        Node markdownEditorNode = markdownEditor.getValue();

        appendSidebar(sidebarNode);
        appendMarkdownEditor(markdownEditorNode);

        collectionDropdown.setCellFactory(_ -> createCollectionDropdownOption());
        collectionDropdown.setButtonCell(createCollectionDropdownOption());

        advSearchHBox.setSpacing(10.0);

        this.searchInOptionsList.getItems().clear();
        this.searchInOptionsList.getItems().addAll("Title", "Content", "Both");
        this.matchAllCheckBox.setSelected(true);

        this.bundle = bundle;

        editCollections = new Pair<>(null, bundle.getString("editCollections"));
        showAll = new Pair<>(null, bundle.getString("showAll"));

        loadLanguageDropdown(bundle.getBaseBundleName());
        loadCollectionDropdown();
    }

    private void appendSidebar(Node sidebarNode) {
        sidebarContainer.getChildren().add(sidebarNode);
        AnchorPane.setTopAnchor(sidebarNode, 0.0);
        AnchorPane.setBottomAnchor(sidebarNode, 0.0);
    }

    private void appendMarkdownEditor(Node markdownEditorNode) {
        markdownEditorContainer.getChildren().add(markdownEditorNode);
        AnchorPane.setTopAnchor(markdownEditorNode, 0.0);
        AnchorPane.setBottomAnchor(markdownEditorNode, 0.0);
        AnchorPane.setLeftAnchor(markdownEditorNode, 0.0);
        AnchorPane.setRightAnchor(markdownEditorNode, 0.0);
    }

    private ListCell<Pair<UUID, String>> createCollectionDropdownOption() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Pair<UUID, String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setText(item.getValue());
                }
            }
        };
    }

    private void loadLanguageDropdown(String language) {
//        var languages = FXCollections.observableArrayList("English", "Dutch", "Spanish");
//        languageDropdown.setItems(languages);
        languageDropdown.setCellFactory(param -> new LanguageListCel());

        LanguageOption item1 = new LanguageOption("English", "/resources/flags/english.png");
        LanguageOption item2 = new LanguageOption("Dutch", "/resources/flags/dutch.png");

        languageDropdown.getItems().addAll(item1, item2);
    }

    private void loadCollectionDropdown() {
        List<Collection> collections = serverUtils.getAllCollections();
        List<Pair<UUID, String>> titles = collections.stream()
                .map(c -> new Pair<>(c.id, c.title)).toList();

        collectionDropdown.getItems().clear();

        collectionDropdown.getItems().add(showAll);
        collectionDropdown.getItems().addAll(titles);
        collectionDropdown.getItems().add(editCollections);

        // If chosen collection is removed, SHOW_ALL is shown
        if (collectionDropdown.getItems().contains(chosenCollectionFilter)) {
            collectionDropdown.setValue(chosenCollectionFilter);
        } else {
            collectionDropdown.setValue(showAll);
            chosenCollectionFilter = showAll;
        }
    }

    @FXML
    private void onLanguageDropdownAction() {
        String chosenLanguage = languageDropdown.getSelectionModel().getSelectedItem().getName();
        mainCtrl.switchLanguage(chosenLanguage);
    }

    @FXML
    private void onCollectionDropdownAction() {
        Pair<UUID, String> selectedItem = collectionDropdown.getSelectionModel().getSelectedItem();

        if (selectedItem == null) return;

        if (selectedItem.equals(editCollections)) {
            openCollectionSettings();
        } else {
            chosenCollectionFilter = selectedItem;
            sidebarCtrl.setSelectedCollectionId(selectedItem.getKey());
        }
    }

    private void openCollectionSettings() {
        var popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(bundle.getString("editCollections"));

        var popup = FXML.load(CollectionSettingsCtrl.class, bundle,"client", "scenes", "CollectionSettings.fxml");

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

        UUID collectionId = chosenCollectionFilter.getKey();
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
