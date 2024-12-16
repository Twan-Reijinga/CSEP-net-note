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
import client.utils.DialogBoxUtils;
import com.google.inject.Injector;
import client.scenes.NoteEditorCtrl;
import client.scenes.MainCtrl;
import client.scenes.MarkdownEditorCtrl;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Main extends Application {

	private static final Injector INJECTOR = createInjector(new MyModule());
	private static final MyFXML FXML = new MyFXML(INJECTOR);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.setProperty("javafx.sg.warn", "true");

		var markdownEditor = FXML.load(MarkdownEditorCtrl.class, "client", "scenes", "MarkdownEditor.fxml");
		var sidebarEditor = FXML.load(SidebarCtrl.class, "client", "scenes", "Sidebar.fxml");
		var noteEditor = FXML.load(NoteEditorCtrl.class, "client", "scenes", "MainUI.fxml");
		var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

		mainCtrl.initialize(primaryStage, noteEditor, markdownEditor, sidebarEditor);

		var editor = FXML.load(MarkdownEditorCtrl.class, "client", "scenes", "MarkdownEditor.fxml");
		DialogBoxUtils.createSimpleDialog("Information", "",
						"Random Divider", e-> { e.consume(); editor.getKey().setDivider(0.4); },
						"Ok", e-> { System.out.println("Ok"); },
						"Cancel", e-> { System.out.println("Cancel"); })
				.appendContent(editor.getValue())
				.showAndWait();

		var box = DialogBoxUtils.createYesNoDialog("", "", b -> { System.out.println(b ? "YES" : "NO"); })
				.allowResize(false)
				.withTitle("You sure about that?")
				.withMessage("Are you sure you want to this? This action cannot be undone. (Probably...)\n")
				.appendContent(new ImageView(new Image("https://filmschoolrejects.com/wp-content/uploads/2019/08/rock_driving.jpg")));

		box.mainStage.getIcons().add(new Image("https://cdn-icons-png.freepik.com/256/13776/13776446.png?semt=ais_hybrid"));
		box.appendContent(editor.getValue());

		box.show();
		//box.showAndWait();
	}
}