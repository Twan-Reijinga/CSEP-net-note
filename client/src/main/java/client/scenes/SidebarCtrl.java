package client.scenes;

import client.utils.NoteTitle;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;


public class SidebarCtrl {

    @FXML
    public VBox noteContainer;

    public SidebarCtrl() {

    }

    public void refresh() {
        noteContainer.getChildren().clear();

        List<NoteTitle> titles = NoteTitle.getDefaultNoteTitles();
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

    private void noteClick(Long id) {
        System.out.println("Clicked on note " + id);
    }

}
