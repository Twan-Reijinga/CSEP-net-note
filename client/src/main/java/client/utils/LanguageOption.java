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
