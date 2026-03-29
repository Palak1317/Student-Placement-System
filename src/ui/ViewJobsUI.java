package ui;

import app.AppNavigator;
import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewJobsUI {

    private VBox view;

    public ViewJobsUI(int userId, String role) {

        view = new VBox(10);
        view.setPadding(new Insets(20));

        // 🔙 Back + 🏠 Home
        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> AppNavigator.goBack());

        Button homeBtn = new Button("🏠 Home");
        homeBtn.setOnAction(e -> {
            DashboardUI dashboard = new DashboardUI(role, userId);
            AppNavigator.navigate(new Scene(dashboard.getView(), 600, 400));
        });

        HBox header = new HBox(10, backBtn, homeBtn);

        Label title = new Label("Job Listings");

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");

        TableView<Job> table = new TableView<>();

        TableColumn<Job, String> titleCol = new TableColumn<>("Job Title");
        titleCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getJobTitle()));

        TableColumn<Job, String> companyCol = new TableColumn<>("Company");
        companyCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCompany()));

        TableColumn<Job, String> savedCol = new TableColumn<>("Saved");

        savedCol.setCellValueFactory(data -> {
            boolean isSaved = isJobSaved(userId, data.getValue().getId());
            return new javafx.beans.property.SimpleStringProperty(isSaved ? "⭐ Saved" : "");
        });

        table.getColumns().addAll(titleCol, companyCol, savedCol);

        ObservableList<Job> jobList = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM jobs");

            while (rs.next()) {
                jobList.add(new Job(
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

        table.setItems(jobList);

        // 🔍 Search filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                table.setItems(jobList);
                return;
            }

            ObservableList<Job> filtered = FXCollections.observableArrayList();
            String search = newVal.toLowerCase();

            for (Job job : jobList) {
                if (job.getJobTitle().toLowerCase().contains(search) ||
                        job.getCompany().toLowerCase().contains(search) ||
                        job.getSkillsRequired().toLowerCase().contains(search)) {

                    filtered.add(job);
                }
            }

            table.setItems(filtered);
        });
        Button saveBtn = new Button("⭐ Save Job");
        Label message = new Label();
        saveBtn.setOnAction(e -> {

            Job job = table.getSelectionModel().getSelectedItem();

            if (job == null) {
                message.setText("⚠ Select a job first!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {

                PreparedStatement check = conn.prepareStatement(
                        "SELECT * FROM saved_jobs WHERE student_id=? AND job_id=?");

                check.setInt(1, userId);
                check.setInt(2, job.getId());

                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    message.setText("❌ Already saved!");
                    return;
                }

                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO saved_jobs (student_id, job_id) VALUES (?, ?)");

                stmt.setInt(1, userId);
                stmt.setInt(2, job.getId());

                stmt.executeUpdate();
                message.setText("✅ Job Saved!");
                table.refresh();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        //view.getChildren().addAll(header, title, searchField, table);
        view.getChildren().addAll(header, title, searchField, table, saveBtn, backBtn, message);
    }
    private boolean isJobSaved(int userId, int jobId) {

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM saved_jobs WHERE student_id=? AND job_id=?");

            stmt.setInt(1, userId);
            stmt.setInt(2, jobId);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public VBox getView() {
        return view;
    }
}