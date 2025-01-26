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

import client.LoaderFXML;
import client.utils.LanguageListCell;
import client.utils.LanguageOption;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import commons.Collection;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    private Label searchInLabel;

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

    @FXML
    private MenuButton tagOptionsButton;

    @FXML
    private HBox tagContainerHBox;

    @FXML
    private AnchorPane filesContainer;

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
     * @param files root element of the files fxml
     * @param bundle resource bundle for current language
     */
    @FXML
    public void initialize(Pair<SidebarCtrl, Parent> sidebar,
                           Pair<MarkdownEditorCtrl,
                            Parent> markdownEditor,
                           Pair<FilesCtrl, Parent> files,
                           ResourceBundle bundle) {
        sidebarCtrl = sidebar.getKey();
        Node sidebarNode = sidebar.getValue();

        markdownEditorCtrl = markdownEditor.getKey();
        Node markdownEditorNode = markdownEditor.getValue();

        appendSidebar(sidebarNode);
        appendFiles(files.getValue());
        appendMarkdownEditor(markdownEditorNode);

        collectionDropdown.setCellFactory(_ -> createCollectionDropdownOption());
        collectionDropdown.setButtonCell(createCollectionDropdownOption());

        setupSearchElements(bundle);
        this.bundle = bundle;

        editCollections = new Pair<>(null, bundle.getString("editCollections"));
        showAll = new Pair<>(null, bundle.getString("showAll"));

        Locale locale = bundle.getLocale();
        String fullLanguageName =
                convertLocaleToLanguageString(locale);
        loadLanguageDropdown(fullLanguageName);
        loadCollectionDropdown(); loadTagOptions();
        serverUtils.connection.subscribe(websocketUpdate -> {
            if(websocketUpdate.collection != null || websocketUpdate.defaultId != null)
                Platform.runLater(this::loadCollectionDropdown);
        });
    }

    private String convertLocaleToLanguageString(Locale locale) {
        String language = locale.getLanguage();
        String fullLanguageName;

        switch (language) {
            case "en":
                fullLanguageName = "English";
                break;
            case "nl":
                fullLanguageName = "Dutch";
                break;
            case "es":
                fullLanguageName = "Spanish";
                break;
            default:
                fullLanguageName = "English";
                break;
        }
        return fullLanguageName;
    }

    private void appendSidebar(Node sidebarNode) {
        sidebarContainer.getChildren().add(sidebarNode);
        AnchorPane.setTopAnchor(sidebarNode, 0.0);
        AnchorPane.setBottomAnchor(sidebarNode, 0.0);
    }

    private void appendFiles(Node filesNode) {
        filesContainer.getChildren().add(filesNode);
        AnchorPane.setTopAnchor(filesNode, 0.0);
        AnchorPane.setBottomAnchor(filesNode, 0.0);
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
                    UUID collectionId = item.getKey();
                    // Note: collectionId can be null if, for example, "Show all" is to be updated
                    boolean isDefault = collectionId != null && collectionId.equals(mainCtrl.getDefaultCollectionId());
                    setText(item.getValue() + (isDefault ? " (Default)" : ""));
                }
            }
        };
    }

    private void loadLanguageDropdown(String language) {
        List<LanguageOption> languageOptions = List.of(
            new LanguageOption("English", "/images/english.png"),
            new LanguageOption("Dutch", "/images/dutch.png"),
            new LanguageOption("Spanish", "/images/spanish.png")
        );

        languageDropdown.setCellFactory(new LanguageListCell());
        languageDropdown.setButtonCell(new LanguageListCell().call(null));

        languageDropdown.getItems().addAll(
                languageOptions
        );

        for (LanguageOption option : languageDropdown.getItems()) {
            if (language.equalsIgnoreCase(option.getName())) {
                languageDropdown.setValue(option);
                return;
            }
        }
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
        String chosenLanguage = languageDropdown
                .getSelectionModel()
                .getSelectedItem()
                .getName();
        Locale language;

        switch (chosenLanguage) {
            case "English":
                language = Locale.of("EN", "us");
                break;
            case "Dutch":
                language = Locale.of("NL", "nl");
                break;
            case "Spanish":
                language = Locale.of("ES", "es");
                break;
            default:
                language = Locale.of("EN", "en");
                break;
        }
        mainCtrl.switchLanguage(language);
        return;
    }

    @FXML
    private void onCollectionDropdownAction() {
        Pair<UUID, String> selectedItem = collectionDropdown
                .getSelectionModel()
                .getSelectedItem();

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

        var popupCtrl = popup.getKey();
        var popupNode = popup.getValue();
        var popupScene = new Scene(popupNode);

        popupStage.setResizable(false);
        popupStage.setOnCloseRequest(event -> {
            if (popupCtrl.hasUnsavedChanges()) {
                popupCtrl.handleUnsavedChanges();
            }

            loadCollectionDropdown();
        });

        popupStage.setScene(popupScene);
        popupStage.show();
    }

    /**
     * Moves to the next item in the dropdown, skipping the last option ("Edit Collections").
     * If the second-to-last item is selected, it wraps around to the first item.
     *
     * The method calculates the next index. If it reaches the last option, it wraps to the
     * first item. Otherwise, it selects the next item. This ensures smooth navigation while
     * avoiding the special last option.
     *
     * The dropdown must be properly set up with items before calling this method.
     * If the dropdown is empty, nothing happens.
     */
    public void selectNextCollection() {
        int selected = collectionDropdown
                .getSelectionModel()
                .getSelectedIndex();
        int next = selected + 1;
        int size = collectionDropdown
                .getItems()
                .size();
        boolean isLastOption = size - 1 == next;

        if (isLastOption) {
            collectionDropdown
                    .getSelectionModel()
                    .selectFirst();
            return;
        }
        collectionDropdown
                .getSelectionModel()
                .selectNext();
    }

    /**
     * Moves to the previous item in the dropdown, skipping the last option ("Edit Collections").
     * If the first item is selected, it wraps around to the second-to-last item.
     *
     * The method checks if the current selection is the first item. If so, it selects the
     * second-to-last item. Otherwise, it moves to the previous item. This ensures smooth
     * backward navigation while avoiding the special last option.
     *
     * The dropdown must be properly set up with items before calling this method.
     * If the dropdown is empty, nothing happens.
     */
    public void selectPreviousCollection() {
        int selected = collectionDropdown
                .getSelectionModel()
                .getSelectedIndex();
        boolean isFirstOption = selected == 0;

        if (isFirstOption) {
            int size = collectionDropdown
                    .getItems()
                    .size();
            int secondToLast = size - 2;
            collectionDropdown
                    .getSelectionModel()
                    .select(secondToLast);
            return;
        }
        collectionDropdown
                .getSelectionModel()
                .selectPrevious();
        return;
    }

    /** Called upon clicking the search button
     *  Calls the sendSearchRequest method from the mainCtrl with the text from the searchBox.
     *  Currently, nothing happens if no text is present in the search box.
     */
    public void onSearchButtonPressed(){
        String searchText = searchBox.getText();
        boolean matchAll = this.matchAllCheckBox.isSelected();
        int whereToSearch = this.searchInOptionsList.getSelectionModel().getSelectedIndex();

        UUID collectionId = chosenCollectionFilter.getKey();
        if(!searchText.isEmpty()){
            mainCtrl.sendSearchRequest(searchText, collectionId, matchAll, whereToSearch);
        } else{
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

    /**
     * This method expands an HBox and reveals a checkbox and a choice box whose
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
        searchInLabel.setVisible(selected);
        matchAllCheckBox.setVisible(selected);
        searchInOptionsList.setVisible(selected);
    }

    /**
     * This method is called when the "clear tags" hyperlink is pressed.
     * Calls the mainCtrl to handle the necessary logic for clearing all tags.
     */
    @FXML
    public void onClearAllTagsPressed(){
        mainCtrl.clearTagFilters();
    }

    /**
     * This method is used to load the items for the MenuButton that displays
     * the possible tags to add as filters.
     */
    public void loadTagOptions(){
        this.tagOptionsButton.getItems().clear();
        List<MenuItem> items = new ArrayList<>();
        List<String> availableTagOptions = mainCtrl.listAvailableTags();
        if(availableTagOptions.isEmpty()){
            items.add(new MenuItem("No tags."));
        }
        else{
            for(String tag: availableTagOptions){
                MenuItem newItem = new MenuItem(tag);

                newItem.setOnAction(e -> {
                    this.onTagOptionClicked(newItem.getText());
                });
                items.add(newItem);
            }
        }
        this.tagOptionsButton.getItems().addAll(items);
    }

    /**
     * Called when an item representing a tag from the MenuButton is clicked.
     * Calls the mainCtrl to handle adding the new tag that was selected as a filter.
     * @param tag the tag selected from the MenuButton
     */
    public void onTagOptionClicked(String tag){
        mainCtrl.addTagFilter(tag);
    }

    /**
     * This method is used to add a label to the HBox for each new tag selected as a filter.
     * @param tag the tag that will be used as a text for the new label added to the HBox.
     */
    public void addSelectedTagToHBox(String tag){
        if(!isTagAlreadyInHBox(tag)){
            Label selectedTagLabel = new Label(tag);
            selectedTagLabel.setStyle(getSelectedTagStyle());
            selectedTagLabel.setMinWidth(25L);

            int index = this.tagContainerHBox.getChildren().size() - 3;
            this.tagContainerHBox.getChildren().add(index, selectedTagLabel);
        }
    }

    /**
     * This method is used to remove certain tags from the HBox, which happens
     * when a note is updated and one of its tags gets deleted.
     * @param tags a list of tags, each corresponding to the text in the
     *             labels that will be removed from the HBox.
     */
    public void removeTagsFromHBox(List<String> tags){
        List<Node> toRemove = new ArrayList<>();
        for(Node child: this.tagContainerHBox.getChildren()){
            if(child.getClass() == Label.class){
                if(tags.contains(((Label)child).getText())){
                    toRemove.add(child);
                }
            }
        }
        this.tagContainerHBox.getChildren().removeAll(toRemove);
    }

    /**
     * This method removes all Labels for tags that were added to the HBox upon selection.
     */
    public void clearSelectedTagsFromHBox(){
        while(this.tagContainerHBox.getChildren().size() > 4){
            this.tagContainerHBox.getChildren().remove(1);
        }
    }

    /**
     * This method is used to check whether a given tag is already added as an item to the HBox.
     * It's used to prevent the addition of a tag multiple times, when it's clicked on many times in the WebView.
     * @param tag the tag to check for.
     * @return true if the tag is already displayed, false otherwise.
     */
    private boolean isTagAlreadyInHBox(String tag){
        boolean alreadyIn = false;
        for(Node node: this.tagContainerHBox.getChildren()){
            if(node.getClass() == Label.class){
                if((((Label) node).getText().equals(tag))){
                    alreadyIn = true;
                }
            }
        }
        return alreadyIn;
    }

    /**
     * focus on the text field of the searchbar you can immediately search when starting to type
     */
    public void focusOnSearch() {
        searchBox.requestFocus();
    }

    private String getSelectedTagStyle(){
        return "-fx-border-color: black;"
                + "-fx-border-width: 1px;"
                + "-fx-border-radius: 10px;"
                + "-fx-padding: 1px 5px 1px 5px;";
    }

    /**
     * This method is called on initialization of NoteEditorCtrl. It sets the elements and initial
     * values for the nodes related to the advanced searching and also creates a tooltip for
     * the advanced search button.
     * @param bundle used to load the text for the nodes in the correct language.
     */
    private void setupSearchElements(ResourceBundle bundle){
        Tooltip advSearchButtonTooltip = new Tooltip(bundle.getString("advSearchButtonTooltip"));
        advSearchButtonTooltip.setShowDelay(Duration.seconds(0.1));
        this.advSearchButton.setTooltip(advSearchButtonTooltip);
        advSearchHBox.setSpacing(10.0);
        this.searchInOptionsList.getItems().clear();
        String bothOption = bundle.getString("inBoth");
        String titleOption = bundle.getString("inTitle");
        String contentOption = bundle.getString("inContent");
        this.searchInOptionsList.getItems().addAll(bothOption, titleOption, contentOption);
        this.searchInOptionsList.getSelectionModel().selectFirst();
        this.matchAllCheckBox.setSelected(true);
    }

    public ResourceBundle getBundle(){
        return bundle;
    }
}
