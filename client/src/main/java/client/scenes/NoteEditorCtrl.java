package client.scenes;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.Timer;
import java.util.TimerTask;


public class NoteEditorCtrl {
    private final MainCtrl mainCtrl;
    private Timer timeKeyPresses = new Timer();
    private int delayBetweenKeyPresses = 1000;

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
    private AnchorPane topMostAnchor;

    @FXML
    private CheckBox matchAllCheckBox;

    @FXML
    private ChoiceBox<String> searchInOptionsList;

    @FXML
    private ToggleButton advSearchButton;

    @Inject
    public NoteEditorCtrl(MainCtrl mainCtrl) {
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

        this.searchInOptionsList.getItems().addAll("Title", "Content", "Both");
        this.matchAllCheckBox.setSelected(true);
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
            topMostAnchor.setPrefHeight(topMostAnchor.getPrefHeight() + 30);
        }
        else{
            topMostAnchor.setPrefHeight(topMostAnchor.getPrefHeight() - 30);
        }
        matchAllCheckBox.setDisable(!selected);
        searchInOptionsList.setDisable(!selected);
        matchAllCheckBox.setVisible(selected);
        searchInOptionsList.setVisible(selected);
    }
}
