package app;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

public class AppNavigator {

    private static Stage stage;
    private static Stack<Scene> history = new Stack<>();

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void navigate(Scene newScene) {
        if (stage.getScene() != null) {
            history.push(stage.getScene());
        }
        stage.setScene(newScene);
    }

    public static void goBack() {
        if (!history.isEmpty()) {
            stage.setScene(history.pop());
        }
    }

    public static void clearHistory() {
        history.clear();
    }
}