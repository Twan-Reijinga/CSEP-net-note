package client.scenes;

import commons.NoteTitle;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;


public class SidebarCtrl {

    private final ServerUtils server;

    @FXML
    public VBox noteContainer;

    /**
     * Sidebar control constructor for functionality behind the sidebar UI element.
     * @param server Server utilities for requests and functionality dependent on the server.
     */
    @Inject
    public SidebarCtrl(ServerUtils server) {
        this.server = server;
    }

    /**
     * Refresh function to activate a GET request to the server to receive all note titles.
     * Functionality will be used when pressed on the refresh button in the GUI.
     */
    public void refresh() {
        List<NoteTitle> titles = server.getNoteTitles();
        loadSideBar(titles);
    }

    /**
     * Note click function for action when a specific note in the sidebar is clicked
     * intended behaviour is that the note contents opens.
     * @param id identifier that is linked to a specific note that corresponds to the servers note ID.
     */
    private void noteClick(Long id) {
        System.out.println("Clicked on note " + id);
    }


    /**
     * Load function to load all desired objects in the sidebar.
     * The function will be called everytime the sidebar is refreshed and also
     * on every search performed in order to load the search results.
     * @param titles the notes that are supposed to be loaded into the sidebar.
     */
    public void loadSideBar(List<NoteTitle> titles) {
        noteContainer.getChildren().clear();

        for (NoteTitle title : titles) {
            Label label = new Label(title.getTitle());
            VBox wrapper = new VBox(label);
            wrapper.setId(title.getId() + "");
            wrapper.setPadding(new Insets(5, 10, 5, 10));
            wrapper.setOnMouseClicked(event -> {
                long id = Long.parseLong(wrapper.getId()); // Get the VBox's ID
                noteClick(id);             // Call a function, passing the ID
            });
            noteContainer.getChildren().add(wrapper);
        }
    }
}
