package client.utils;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class LanguageListCel extends ListCell<LanguageOption> {
    @Override
    protected void updateItem(LanguageOption item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            setGraphic(new ImageView(item.getImage()));
        }
    }
}
