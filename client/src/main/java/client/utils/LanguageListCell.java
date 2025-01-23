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
                    imageView.setFitHeight(19);
                    imageView.setFitWidth(32);
                    setText(item.getName());
                    setGraphic(imageView);
                }
            }
        };
    }
}

