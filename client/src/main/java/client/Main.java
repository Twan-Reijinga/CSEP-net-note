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

import client.scenes.*;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ResourceBundle;


public class Main extends Application {

	private static final Injector INJECTOR = createInjector(new MyModule());
	private static final MyFXML FXML = new MyFXML(INJECTOR);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ResourceBundle englishBundle = ResourceBundle.getBundle("english");
		ResourceBundle dutchBundle = ResourceBundle.getBundle("dutch");
		ResourceBundle spanishBundle = ResourceBundle.getBundle("spanish");
		var markdownEditor = FXML.load(MarkdownEditorCtrl.class, englishBundle,"client", "scenes", "MarkdownEditor.fxml");
		var sidebarEditor = FXML.load(SidebarCtrl.class, englishBundle, "client", "scenes", "Sidebar.fxml");
		var filesEditor = FXML.load(FilesCtrl.class, englishBundle,"client", "scenes", "Files.fxml");
		var noteEditor = FXML.load(NoteEditorCtrl.class, englishBundle,"client", "scenes", "MainUI.fxml");
		var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
		mainCtrl.initialize(primaryStage, markdownEditor, noteEditor, sidebarEditor, filesEditor);
	}
}