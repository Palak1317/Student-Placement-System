package ui;

import app.AppNavigator;
import database.DBConnection;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.ValidationService;

import java.sql.*;

public class RegisterUI {

    private VBox view;

    public RegisterUI() {

        view = new VBox(10);
        view.setPadding(new Insets(20));

        // 🔙 Back
        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> AppNavigator.goBack());

        HBox header = new HBox(10, backBtn);

        Label title = new Label("Student Registration");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        TextField name = new TextField();
        name.setPromptText("Full Name");

        TextField email = new TextField();
        email.setPromptText("Email");

        TextField branch = new TextField();
        branch.setPromptText("Branch");

        TextField skills = new TextField();
        skills.setPromptText("Skills (comma separated)");

        TextField cgpa = new TextField();
        cgpa.setPromptText("CGPA");

        TextField experience = new TextField();
        experience.setPromptText("Experience");

        Button registerBtn = new Button("Register");

        Label message = new Label();

        registerBtn.setOnAction(e -> {

            // Validation
            if (ValidationService.isEmpty(
                    username.getText(),
                    password.getText(),
                    name.getText(),
                    email.getText()
            )) {
                message.setText("All fields required!");
                return;
            }

            if (!ValidationService.isValidEmail(email.getText())) {
                message.setText("Invalid email!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {

                conn.setAutoCommit(false); // transaction start

                // 🔹 Insert into users
                String userQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

                PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
                userStmt.setString(1, username.getText());
                userStmt.setString(2, password.getText());
                userStmt.setString(3, "student");

                userStmt.executeUpdate();

                ResultSet rs = userStmt.getGeneratedKeys();

                if (rs.next()) {

                    int userId = rs.getInt(1); // same ID for student

                    // 🔹 Insert into students
                    String studentQuery = "INSERT INTO students (id, name, email, branch, skills, experience, cgpa) VALUES (?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement studentStmt = conn.prepareStatement(studentQuery);

                    studentStmt.setInt(1, userId);
                    studentStmt.setString(2, name.getText());
                    studentStmt.setString(3, email.getText());
                    studentStmt.setString(4, branch.getText());
                    studentStmt.setString(5, skills.getText());
                    studentStmt.setInt(6, Integer.parseInt(experience.getText()));
                    studentStmt.setDouble(7, Double.parseDouble(cgpa.getText()));

                    studentStmt.executeUpdate();

                    conn.commit();

                    message.setText("Registration Successful!");

                }

            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Registration Failed");
            }
        });

        view.getChildren().addAll(
                header,
                title,
                username,
                password,
                name,
                email,
                branch,
                skills,
                cgpa,
                experience,
                registerBtn,
                message
        );
    }

    public VBox getView() {
        return view;
    }
}