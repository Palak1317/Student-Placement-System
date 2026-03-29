package ui;

import service.CSVService;
import service.EmailService;
import app.AppNavigator;
import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.PDFService;

import java.sql.*;

public class ManageApplicationsUI {

    private VBox view;

    public ManageApplicationsUI() {

        view = new VBox(10);
        view.setPadding(new Insets(20));

        // 🔙 Navigation
        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> AppNavigator.goBack());

        Button homeBtn = new Button("🏠 Home");
        homeBtn.setOnAction(e -> {
            DashboardUI dashboard = new DashboardUI("admin", 0);
            AppNavigator.navigate(new Scene(dashboard.getView(), 600, 400));
        });

        HBox header = new HBox(10, backBtn, homeBtn);

        Label title = new Label("Manage Applications");

        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> col1 = new TableColumn<>("Student");
        col1.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));

        TableColumn<String[], String> col2 = new TableColumn<>("Job");
        col2.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));

        TableColumn<String[], String> col3 = new TableColumn<>("Score");
        col3.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        TableColumn<String[], String> col4 = new TableColumn<>("Status");
        col4.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        table.getColumns().addAll(col1, col2, col3, col4);

        ObservableList<String[]> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection()) {

            String query = """
                SELECT s.name, j.job_title, a.match_score, a.status, a.id
                FROM applications a
                JOIN students s ON a.student_id = s.id
                JOIN jobs j ON a.job_id = j.id
            """;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("name"),
                        rs.getString("job_title"),
                        String.valueOf(rs.getDouble("match_score")),
                        rs.getString("status"),
                        String.valueOf(rs.getInt("id"))
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        table.setItems(list);

        // 🔽 DECLARE FIRST (IMPORTANT FIX)
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Applied", "Shortlisted", "Selected", "Rejected");

        Label message = new Label();

        Button updateBtn = new Button("Update Status");

        // ✅ UPDATE STATUS
        updateBtn.setOnAction(e -> {

            String[] selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a row first!");
                return;
            }

            String newStatus = statusBox.getValue();

            if (newStatus == null) {
                message.setText("Select status!");
                return;
            }

            int applicationId = Integer.parseInt(selected[4]);

            try (Connection conn = DBConnection.getConnection()) {

                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE applications SET status=? WHERE id=?");

                stmt.setString(1, newStatus);
                stmt.setInt(2, applicationId);
                stmt.executeUpdate();

                message.setText("Status Updated!");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // 🔥 BULK EMAIL BUTTON (NOW CORRECT)
        Button bulkEmailBtn = new Button("Send Email to All");

        bulkEmailBtn.setOnAction(e -> {

            String status = statusBox.getValue();

            if (status == null) {
                message.setText("Select status first!");
                return;
            }

            EmailService.sendBulkEmails(status);

            message.setText("📧 Emails sent to all " + status + " students!");
        });
        MenuButton downloadBtn = new MenuButton("Download File");

        MenuItem pdfItem = new MenuItem("Export as PDF");
        MenuItem csvItem = new MenuItem("Export as CSV");

        pdfItem.setOnAction(e -> {
            PDFService.exportTable(table, "admin_applications.pdf");
            message.setText("📄 PDF Downloaded!");
        });

        csvItem.setOnAction(e -> {
            CSVService.exportTable(table, "admin_applications.csv");
            message.setText("📊 CSV Downloaded!");
        });

        downloadBtn.getItems().addAll(pdfItem, csvItem);

        view.getChildren().addAll(
                header,
                title,
                table,
                statusBox,
                updateBtn,
                bulkEmailBtn,
                downloadBtn,
                message
        );
    }

    public VBox getView() {
        return view;
    }
}