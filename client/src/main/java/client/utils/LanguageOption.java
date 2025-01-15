package client.utils;

import javafx.scene.image.Image;

public class LanguageOption {
    private final String name;
    private final Image image;

    public LanguageOption(String name, String path) {
        this.name = name;
        image = new Image(path);
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }
}
