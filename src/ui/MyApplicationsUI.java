package ui;

import app.AppNavigator;
import database.DBConnection;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.PDFService;
import service.CSVService;

import java.sql.*;

public class MyApplicationsUI {

    private VBox view;

    public MyApplicationsUI(int userId) {

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

        Label title = new Label("My Applications");

        // ✅ TABLE FIRST (IMPORTANT)
        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> c1 = new TableColumn<>("Job");
        c1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));

        TableColumn<String[], String> c2 = new TableColumn<>("Company");
        c2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));

        TableColumn<String[], String> c3 = new TableColumn<>("Score");
        c3.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        TableColumn<String[], String> c4 = new TableColumn<>("Status");
        c4.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        table.getColumns().addAll(c1, c2, c3, c4);

        // ✅ MESSAGE LABEL
        Label message = new Label();

        // ✅ DOWNLOAD DROPDOWN
        MenuButton downloadBtn = new MenuButton("Download File");

        MenuItem pdfItem = new MenuItem("Export as PDF");
        MenuItem csvItem = new MenuItem("Export as CSV");

        pdfItem.setOnAction(e -> {
            PDFService.exportTable(table, "applications.pdf");
            message.setText("📄 PDF Downloaded!");
        });

        csvItem.setOnAction(e -> {
            CSVService.exportTable(table, "applications.csv");
            message.setText("📊 CSV Downloaded!");
        });

        downloadBtn.getItems().addAll(pdfItem, csvItem);

        // 📦 LOAD DATA
        ObservableList<String[]> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement("""
                SELECT j.job_title, j.company, a.match_score, a.status
                FROM applications a
                JOIN jobs j ON a.job_id = j.id
                WHERE a.student_id = ?
            """);

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString(1),
                        rs.getString(2),
                        String.valueOf(rs.getDouble(3)),
                        rs.getString(4)
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        table.setItems(list);

        // ✅ FINAL UI
        view.getChildren().addAll(header, title, table, downloadBtn, message);
    }

    public VBox getView() {
        return view;
    }
}