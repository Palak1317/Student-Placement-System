package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import service.AuthService;
import app.AppNavigator;
import javafx.scene.Scene;
public class LoginUI {

    private VBox view;

    public LoginUI() {
        view = new VBox(10);
        view.setPadding(new Insets(20));

        Label title = new Label("Login");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");

        Label message = new Label();

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            int userId = AuthService.login(username, password);

            if (userId != -1) {

                String role = AuthService.getRole(userId);

                DashboardUI dashboard = new DashboardUI(role, userId);
                AppNavigator.navigate(new Scene(dashboard.getView(), 600, 400));

            } else {
                message.setText("Invalid Credentials");
            }
        });
        registerBtn.setOnAction(e -> {
            RegisterUI registerUI = new RegisterUI();
            AppNavigator.navigate(new Scene(registerUI.getView(), 600, 500));
        });

        //view.getChildren().addAll(title, usernameField, passwordField, loginBtn, message);
        view.getChildren().addAll(title, usernameField, passwordField, loginBtn, registerBtn, message);
    }

    public VBox getView() {
        return view;
    }
}