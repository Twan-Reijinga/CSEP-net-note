package client.scenes;

import client.utils.NoteTitle;
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

    private PrimaryCtrl pc;

    @Inject
    public SidebarCtrl(PrimaryCtrl pc, ServerUtils server) {
        this.pc = pc;
        this.server = server;
    }

    public void refresh() {
        noteContainer.getChildren().clear();

        List<NoteTitle> titles = server.getNoteTitles();
        for (NoteTitle title : titles) {
            Label label = new Label(title.getTitle());
            VBox wrapper = new VBox(label);
            wrapper.setId(title.getId() + "");
            wrapper.setPadding(new Insets(5, 10, 5, 10));
            wrapper.setOnMouseClicked(event -> {
                String id = wrapper.getId(); // Get the VBox's ID
                noteClick(id);             // Call a function, passing the ID
            });
            noteContainer.getChildren().add(wrapper);
        }
    }

    private void noteClick(String id) {
        System.out.println("Clicked on note " + id);
    }

}
