package client.utils;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class LanguageListCell implements Callback<ListView<LanguageOption>, ListCell<LanguageOption>> {
    @Override
    public ListCell<LanguageOption> call(ListView<LanguageOption> param) {
        return new ListCell<LanguageOption>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(LanguageOption item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(item.getImage());
                    imageView.setFitHeight(24);
                    imageView.setFitWidth(32);
                    setText(item.getName());
                    setGraphic(imageView);
                }
            }
        };
    }
}

