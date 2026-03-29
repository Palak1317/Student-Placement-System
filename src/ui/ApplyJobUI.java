package ui;

import app.AppNavigator;
import database.DBConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Job;
import service.MatchingService;

import java.sql.*;

public class ApplyJobUI {

    private VBox view;

    public ApplyJobUI(int userId) {

        view = new VBox(10);
        view.setPadding(new Insets(20));


        // 🔙 Navigation
        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> AppNavigator.goBack());

        Button homeBtn = new Button("🏠 Home");
        homeBtn.setOnAction(e -> {
            DashboardUI dashboard = new DashboardUI("student", userId);
            AppNavigator.navigate(new Scene(dashboard.getView(), 600, 400));
        });

        HBox header = new HBox(10, backBtn, homeBtn);

        Label title = new Label("Apply for Job");

        // 📊 Table
        TableView<Job> table = new TableView<>();

        TableColumn<Job, String> titleCol = new TableColumn<>("Job Title");
        titleCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getJobTitle()));

        TableColumn<Job, String> companyCol = new TableColumn<>("Company");
        companyCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCompany()));

        table.getColumns().addAll(titleCol, companyCol);

        ObservableList<Job> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM jobs");

            while (rs.next()) {
                list.add(new Job(
                        rs.getInt("id"),
                        rs.getString("job_title"),
                        rs.getString("company"),
                        rs.getString("skills_required"),
                        rs.getDouble("min_cgpa"),
                        rs.getInt("experience_required"),
                        rs.getString("deadline")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        table.setItems(list);

        // 🔄 Loading Indicator
        Label loading = new Label("Processing...");
        loading.setVisible(false);

        Button applyBtn = new Button("Apply");
        Label message = new Label();

        applyBtn.setOnAction(e -> {

            Job job = table.getSelectionModel().getSelectedItem();

            // ❌ Validation
            if (job == null) {
                message.setText("⚠ Please select a job first!");
                return;
            }

            loading.setVisible(true);
            message.setText("");

            new Thread(() -> {
                try (Connection conn = DBConnection.getConnection()) {

                    // ❗ Check duplicate application
                    PreparedStatement checkStmt = conn.prepareStatement(
                            "SELECT * FROM applications WHERE student_id=? AND job_id=?");

                    checkStmt.setInt(1, userId);
                    checkStmt.setInt(2, job.getId());

                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        Platform.runLater(() -> {
                            loading.setVisible(false);
                            message.setText("❌ Already applied to this job!");
                        });
                        return;
                    }

                    // ✅ Calculate score
                    double score = MatchingService.calculateMatchScore(userId, job.getId());

                    // ✅ Insert application
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO applications (student_id, job_id, match_score, status) VALUES (?, ?, ?, ?)");

                    stmt.setInt(1, userId);
                    stmt.setInt(2, job.getId());
                    stmt.setDouble(3, score);
                    stmt.setString(4, "Applied");

                    stmt.executeUpdate();

                    Platform.runLater(() -> {
                        loading.setVisible(false);
                        message.setText("✅ Applied Successfully!");
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        loading.setVisible(false);
                        message.setText("❌ Error applying!");
                    });
                }
            }).start();
        });

        view.getChildren().addAll(header, title, table, applyBtn, loading, message);
    }

    public VBox getView() {
        return view;
    }
}