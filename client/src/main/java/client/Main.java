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

import client.scenes.PrimaryCtrl;
import client.scenes.SidebarCtrl;
import com.google.inject.Injector;
import client.scenes.NoteEditorCtrl;
import com.google.inject.Injector;
import client.scenes.MainCtrl;
import javafx.application.Application;
import javafx.stage.Stage;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

	private static final Injector INJECTOR = createInjector(new MyModule());
	private static final MyFXML FXML = new MyFXML(INJECTOR);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		var sidebar = FXML.load(SidebarCtrl.class, "client", "Sidebar.fxml");
		var pc = INJECTOR.getInstance(PrimaryCtrl.class);
		pc.init(primaryStage, sidebar);
//		var fxml = new FXMLLoader();
//		fxml.setLocation(getLocation("client/scenes/Sidebar.fxml"));
//		var scene = new Scene(fxml.load());
//		primaryStage.setScene(scene);
//		primaryStage.show();

		/*var serverUtils = INJECTOR.getInstance(ServerUtils.class);
		if (!serverUtils.isServerAvailable()) {
			var msg = "Server needs to be started before the client, but it does not seem to be available. Shutting down.";
			System.err.println(msg);
			return;
		}*/
		var noteEditor = FXML.load(NoteEditorCtrl.class, "client", "scenes", "mocktemplate.fxml");
		var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
		mainCtrl.initialize(primaryStage, noteEditor);
	}

	private static URL getLocation(String path) {
		return Main.class.getClassLoader().getResource(path);
	}
}