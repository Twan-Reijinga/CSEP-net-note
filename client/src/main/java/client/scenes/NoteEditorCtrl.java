package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


public class NoteEditorCtrl {
    private final MainCtrl mainCtrl;

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
    @FXML
    public void initialize() {
        centerTextField();
        topMostAnchor.widthProperty().addListener((observable, oldValue, newValue) -> {
            centerTextField();
        });
    }

    /**
     * translates the NetNote title to always be center aligned to the anchor pane it is in,
     */
    public void centerTextField() {
        double anchorWidth = topMostAnchor.getWidth();
        double textFieldWidth = appTitle.getWidth();
        appTitle.setLayoutX((anchorWidth - textFieldWidth) / 2);
        appTitle.relocate(appTitle.getLayoutX(), appTitle.getLayoutY());
    }
}
