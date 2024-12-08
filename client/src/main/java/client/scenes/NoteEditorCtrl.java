package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
import commons.NoteTitle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
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
     *  Calls the searchNotesInCollection method that performs a GET request
     *  to the server and searches by the text given in the searchBox.
     *  Results are displayed in a list.
     *
     */
    public void onSearchButtonPressed(){
        String searchText = searchBox.getText();
        long collectionId = 0;
        if(!searchText.isEmpty()){
            mainCtrl.sendSearchRequest(searchText, collectionId, true, 0);
        }
        else{
            // Do we return all notes or display an alert?
        }
    }

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
}
