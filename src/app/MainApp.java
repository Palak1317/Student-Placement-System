package app;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.LoginUI;
import app.AppNavigator;
import service.EmailService;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        AppNavigator.setStage(stage);

        LoginUI loginUI = new LoginUI();

        Scene scene = new Scene(loginUI.getView(), 400, 300);

        stage.setTitle("Placement System");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}