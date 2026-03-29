package ui;

import app.AppNavigator;
import database.DBConnection;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Job;
import service.CSVService;
import service.MatchingService;
import service.PDFService;
import service.SkillGapService;

import java.sql.*;
import java.util.Comparator;

public class RecommendationUI {

    private VBox view;

    public RecommendationUI(int userId) {

        view = new VBox(10);
        view.setPadding(new Insets(20));

        // 🔙 Back + 🏠 Home
        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> AppNavigator.goBack());

        Button homeBtn = new Button("🏠 Home");
        homeBtn.setOnAction(e -> {
            DashboardUI dashboard = new DashboardUI("student", userId);
            AppNavigator.navigate(new Scene(dashboard.getView(), 600, 400));
        });

        HBox header = new HBox(10, backBtn, homeBtn);

        Label title = new Label("Recommended Jobs");

        TableView<Job> table = new TableView<>();

        // 🔹 Columns
        TableColumn<Job, String> titleCol = new TableColumn<>("Job Title");
        titleCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getJobTitle()));

        TableColumn<Job, String> companyCol = new TableColumn<>("Company");
        companyCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getCompany()));

        TableColumn<Job, String> scoreCol = new TableColumn<>("Match %");
        scoreCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(MatchingService.calculateMatchScore(userId, d.getValue().getId()))
                ));
        TableColumn<Job, String> labelCol = new TableColumn<>("Match Level");
        labelCol.setCellValueFactory(d -> {

            double score = MatchingService.calculateMatchScore(userId, d.getValue().getId());

            String level;

            if (score >= 85) level = "Best Match";
            else if (score >= 60) level = "Good Match";
            else level = "Low Match";

            return new javafx.beans.property.SimpleStringProperty(level);
        });

        // 🔥 FIXED SKILL GAP COLUMN
        TableColumn<Job, String> gapCol = new TableColumn<>("Skill Gap");
        gapCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        SkillGapService.getSkillGap(userId, d.getValue().getId())
                ));

        // ✅ ADD ALL COLUMNS
        table.getColumns().addAll(titleCol, companyCol, scoreCol, gapCol, labelCol);

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

        // 🔥 Sort by match score
        list.sort(Comparator.comparingDouble(j ->
                -MatchingService.calculateMatchScore(userId, j.getId())
        ));

        table.setItems(list);
        MenuButton downloadBtn = new MenuButton("Download File");

        MenuItem pdfItem = new MenuItem("Export as PDF");
        MenuItem csvItem = new MenuItem("Export as CSV");

        pdfItem.setOnAction(e -> PDFService.exportTable(table, "recommendations.pdf"));
        csvItem.setOnAction(e -> CSVService.exportTable(table, "recommendations.csv"));

        downloadBtn.getItems().addAll(pdfItem, csvItem);

        view.getChildren().addAll(header, title, table,downloadBtn);
    }

    public VBox getView() {
        return view;
    }
}