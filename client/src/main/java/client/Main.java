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

import static com.google.inject.Guice.createInjector;

import client.scenes.SidebarCtrl;
import com.google.inject.Injector;
import client.scenes.NoteEditorCtrl;
import client.scenes.MainCtrl;
import client.scenes.MarkdownEditorCtrl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;


public class Main extends Application {
	public enum Language {
		EN,
		NL,
		ES;
	}
	private static Language currentLanguage = Language.EN;

	private static final Injector INJECTOR = createInjector(new GuiceModule());
	private static final LoaderFXML FXML = INJECTOR.getInstance(LoaderFXML.class);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("english");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
		loader.setResources(resourceBundle);

		var markdownEditor = FXML.load(MarkdownEditorCtrl.class, resourceBundle,
				"client", "scenes", "MarkdownEditor.fxml");

		var sidebarEditor = FXML.load(SidebarCtrl.class, resourceBundle, "client", "scenes", "Sidebar.fxml");
		var noteEditor = FXML.load(NoteEditorCtrl.class, resourceBundle,"client", "scenes", "MainUI.fxml");

		var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

		mainCtrl.initialize(primaryStage, markdownEditor, noteEditor, sidebarEditor);
	}

	public static void switchLanguage(Language language) throws Exception {
		currentLanguage = language;
		Main main = new Main();
		Stage stage = new Stage();
		main.start(stage);
	}
}
