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

import java.sql.*;

public class SavedJobsUI {

    private VBox view;

    public SavedJobsUI(int userId) {

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

        Label title = new Label("Saved Jobs");

        TableView<Job> table = new TableView<>();

        TableColumn<Job, String> col1 = new TableColumn<>("Job");
        col1.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getJobTitle()));

        TableColumn<Job, String> col2 = new TableColumn<>("Company");
        col2.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getCompany()));

        table.getColumns().addAll(col1, col2);

        ObservableList<Job> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT j.* FROM saved_jobs s
                JOIN jobs j ON s.job_id = j.id
                WHERE s.student_id=?
            """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

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

        // ❌ REMOVE BUTTON
        Button removeBtn = new Button("Remove Bookmark");
        Label message = new Label();

        removeBtn.setOnAction(e -> {

            Job job = table.getSelectionModel().getSelectedItem();

            if (job == null) {
                message.setText("Select a job!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {

                PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM saved_jobs WHERE student_id=? AND job_id=?");

                stmt.setInt(1, userId);
                stmt.setInt(2, job.getId());

                stmt.executeUpdate();

                table.getItems().remove(job);

                message.setText("Removed!");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        view.getChildren().addAll(header, title, table, removeBtn, message);
    }

    public VBox getView() {
        return view;
    }
}