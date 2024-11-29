package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class PrimaryCtrl {
    private Stage primaryStage;
    private Scene sidebarScene;

    public void init(Stage primaryStage,
                     Pair<SidebarCtrl, Parent> sidebar) {
        this.primaryStage = primaryStage;
        this.sidebarScene = new Scene(sidebar.getValue());
        showSidebar();
        primaryStage.show();
    }

    private void showSidebar() {
        primaryStage.setTitle("Sidebar scene");
        primaryStage.setScene(sidebarScene);
    }
}
