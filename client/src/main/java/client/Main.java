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
import client.utils.LanguagePreference;
import com.google.inject.Injector;
import client.scenes.NoteEditorCtrl;
import client.scenes.MainCtrl;
import client.scenes.MarkdownEditorCtrl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.ResourceBundle;


public class Main extends Application {
	public enum Language {
		EN,
		NL,
		ES;
	}

	private static MainCtrl mainCtrl;
	private static Stage primaryStage;

	private static final Injector INJECTOR = createInjector(new GuiceModule());
	private static final LoaderFXML FXML = INJECTOR.getInstance(LoaderFXML.class);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		Language language = LanguagePreference.getLanguage();
		loadApplication(language);
	}

	public static void loadApplication(Language language) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle(
			switch (language) {
				case NL -> "Dutch";
				case ES -> "Spanish";
				default -> "English";
			}
		);

		double width = primaryStage.getWidth();
		double height = primaryStage.getHeight();

		Main mainInstance = new Main();
		FXMLLoader loader = new FXMLLoader(mainInstance.getClass().getResource("/fxml/main.fxml"));
		loader.setResources(resourceBundle);
		var markdownEditor = FXML.load(MarkdownEditorCtrl.class, resourceBundle,
				"client", "scenes", "MarkdownEditor.fxml");
		var sidebarEditor = FXML.load(SidebarCtrl.class, resourceBundle, "client", "scenes", "Sidebar.fxml");
		var noteEditor = FXML.load(NoteEditorCtrl.class, resourceBundle,"client", "scenes", "MainUI.fxml");
		if (mainCtrl == null) {
			mainCtrl = INJECTOR.getInstance(MainCtrl.class);
		}
		mainCtrl.initialize(primaryStage, markdownEditor, noteEditor, sidebarEditor, resourceBundle);
		primaryStage.setWidth(width);
		primaryStage.setHeight(height);
	}

	public static void switchLanguage(Language language) {
		if (language == LanguagePreference.getLanguage()) return;
		LanguagePreference.saveLanguage(language);
		loadApplication(language);
	}
}
