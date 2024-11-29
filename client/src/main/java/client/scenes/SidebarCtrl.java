package client.scenes;

import client.utils.NoteTitle;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;


public class SidebarCtrl {

    @FXML
    public VBox noteContainer;

    private PrimaryCtrl pc;

    @Inject
    public SidebarCtrl(PrimaryCtrl pc) {
        this.pc = pc;
    }

    public void refresh() {
        noteContainer.getChildren().clear();

        List<NoteTitle> titles = NoteTitle.getDefaultNoteTitles();
        for (NoteTitle title : titles) {
            Label label = new Label(title.getTitle());
            label.setId(title.getId() + "");
            VBox wrapper = new VBox(label);
            wrapper.setPadding(new Insets(5, 10, 5, 10));
            noteContainer.getChildren().add(wrapper);
        }
    }

}
