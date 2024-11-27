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
package client;

import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.Label;

public class Main extends Application {

	@FXML
	public VBox noteContainer;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		var fxml = new FXMLLoader();
		fxml.setLocation(getLocation("client/scenes/Sidebar.fxml"));
		var scene = new Scene(fxml.load());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private static URL getLocation(String path) {
		return Main.class.getClassLoader().getResource(path);
	}

	public void refresh() {
		noteContainer.getChildren().clear();

		List<String> titles = getNoteTitles();
		for (String title : titles) {
			Label label = new Label(title);
			noteContainer.getChildren().add(label);
		}
	}

	private List<String> getNoteTitles() {
		// TODO: GET notes in collection
		return List.of("Title #1", "Title #2", "Title #3", "Title #4");
	}

}