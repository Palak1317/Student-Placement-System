package ui;

import database.DBConnection;
import app.AppNavigator;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddJobUI {

    private VBox view;

    public AddJobUI() {

        view = new VBox(10);
        view.setPadding(new Insets(20));

        // 🔙 BACK BUTTON
        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> AppNavigator.goBack());

        // 🏠 HOME BUTTON (Admin dashboard)
        Button homeBtn = new Button("🏠 Home");
        homeBtn.setOnAction(e -> {
            DashboardUI dashboard = new DashboardUI("admin", 0); // admin user
            AppNavigator.navigate(new Scene(dashboard.getView(), 600, 400));
        });

        // Header
        HBox header = new HBox(10);
        header.getChildren().addAll(backBtn, homeBtn);

        Label title = new Label("Add Job");

        TextField jobTitle = new TextField();
        jobTitle.setPromptText("Job Title");

        TextField company = new TextField();
        company.setPromptText("Company");

        TextField skills = new TextField();
        skills.setPromptText("Skills Required (comma separated)");

        TextField cgpa = new TextField();
        cgpa.setPromptText("Minimum CGPA");

        TextField experience = new TextField();
        experience.setPromptText("Experience Required");

        TextField deadline = new TextField();
        deadline.setPromptText("Deadline (YYYY-MM-DD)");

        Button submit = new Button("Add Job");

        Label message = new Label();

        // 🔥 VALIDATION + INSERT
        submit.setOnAction(e -> {

            // Validation
            if (jobTitle.getText().isEmpty() ||
                    company.getText().isEmpty() ||
                    skills.getText().isEmpty() ||
                    cgpa.getText().isEmpty() ||
                    experience.getText().isEmpty() ||
                    deadline.getText().isEmpty()) {

                message.setText("Please fill all fields!");
                return;
            }

            try {
                double cgpaVal = Double.parseDouble(cgpa.getText());
                int expVal = Integer.parseInt(experience.getText());

                try (Connection conn = DBConnection.getConnection()) {

                    String query = "INSERT INTO jobs (job_title, company, skills_required, min_cgpa, experience_required, deadline) VALUES (?, ?, ?, ?, ?, ?)";

                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, jobTitle.getText());
                    stmt.setString(2, company.getText());
                    stmt.setString(3, skills.getText());
                    stmt.setDouble(4, cgpaVal);
                    stmt.setInt(5, expVal);
                    stmt.setString(6, deadline.getText());

                    stmt.executeUpdate();

                    message.setText("Job Added Successfully!");

                    // Clear fields
                    jobTitle.clear();
                    company.clear();
                    skills.clear();
                    cgpa.clear();
                    experience.clear();
                    deadline.clear();
                }

            } catch (NumberFormatException ex) {
                message.setText("Invalid number format!");
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Error adding job");
            }
        });

        view.getChildren().addAll(
                header,
                title,
                jobTitle,
                company,
                skills,
                cgpa,
                experience,
                deadline,
                submit,
                message
        );
    }

    public VBox getView() {
        return view;
    }
}