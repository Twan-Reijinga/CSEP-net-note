package client.utils;

import javafx.scene.image.Image;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LanguageOption that = (LanguageOption) o;
        return Objects.equals(name, that.name) && Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, image);
    }

    @Override
    public String toString() {
        return "LanguageOption{" +
                "name='" + name + '\'' +
                ", image=" + image +
                '}';
    }
}
