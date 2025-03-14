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

import client.config.Config;
import client.scenes.*;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;


public class Main extends Application {
	private static MainCtrl mainCtrl;
	private static Stage primaryStage;

	private static final Injector INJECTOR = createInjector(new GuiceModule());
	private static final LoaderFXML FXML = INJECTOR.getInstance(LoaderFXML.class);
	private static final Config CONFIG = INJECTOR.getInstance(Config.class);
	private static final ServerUtils SERVER = INJECTOR.getInstance(ServerUtils.class);

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Initializes and starts the client application.
	 * This method is automatically invoked during the application startup process.
	 *
	 * @param stage the primary stage for this JavaFX application, used to set the main application window
	 */
	@Override
	public void start(Stage stage) {
		// This method catches most error from all threads EXCEPT certain initialization errors
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			handleThreadException(throwable);
		});

		primaryStage = stage;
		Locale language = CONFIG.getLanguage();
		loadApplication(language);
	}

	/**
	 * Loads the application with the specified language settings.
	 * This method configures the application to use the provided language
	 * for localization and other language-specific settings.
	 *
	 * @param language the language configuration to be applied to the application
	 */
	public static void loadApplication(Locale language) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("AppResources", language);

		Main mainInstance = new Main();
		FXMLLoader loader = new FXMLLoader(mainInstance.getClass().getResource("/fxml/main.fxml"));
		loader.setResources(resourceBundle);
		var markdownEditor = FXML.load(MarkdownEditorCtrl.class, resourceBundle,
				"client", "scenes", "MarkdownEditor.fxml");
		var sidebarEditor = FXML.load(SidebarCtrl.class, resourceBundle, "client", "scenes", "Sidebar.fxml");
		var filesEditor = FXML.load(FilesCtrl.class, resourceBundle,"client", "scenes", "Files.fxml");
		var noteEditor = FXML.load(NoteEditorCtrl.class, resourceBundle, "client", "scenes", "MainUI.fxml");
		if (mainCtrl == null) {
			mainCtrl = INJECTOR.getInstance(MainCtrl.class);
		}
		mainCtrl.initialize(primaryStage, markdownEditor,
				noteEditor, sidebarEditor, filesEditor, resourceBundle);
	}

	/**
	 * method for switching the current language and refreshing the application
	 *
	 * @param language the language configuration to be applied to the application
	 */
	public static void switchLanguage(Locale language) {
		if (language == CONFIG.getLanguage()) {
			return;
		}
		CONFIG.setLanguage(language);
		loadApplication(language);
	}

	/**
	 * Any error that reaches the default exception handler will be caught
	 * and shown to the user in the sidebar as any other error
	 * @param throwable an error that hasn't been caught manually
	 */
	private void handleThreadException(Throwable throwable) {
		if (!SERVER.isServerAvailable()) {
			try {
				mainCtrl.handleServerUnreachable();
			} catch (Exception e) {
				System.err.println("Unable to display an exception caused by unavailable server.");

				System.err.println("\n>>>> EXCEPTION CAUGHT (STATE NO SERVER) >>>>");
				System.err.println(throwable.getMessage());
				throwable.printStackTrace();
			}
		} else {
			System.err.println("\n>>>> EXCEPTION CAUGHT >>>>");
			System.err.println(throwable.getMessage());
			throwable.printStackTrace();
			try {
				mainCtrl.showMessage("Unknown exception has occurred: " + throwable.getMessage(), true);
			} catch (Exception e) {
				System.err.println("Unable to display unknown exception.");
			}
		}
	}
}
