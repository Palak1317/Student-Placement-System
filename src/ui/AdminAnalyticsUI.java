package ui;

import app.AppNavigator;
import database.DBConnection;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;

public class AdminAnalyticsUI {

    private VBox view;

    public AdminAnalyticsUI() {

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> AppNavigator.goBack());

        Button homeBtn = new Button("🏠 Home");
        homeBtn.setOnAction(e -> {
            DashboardUI dashboard = new DashboardUI("admin", 0);
            AppNavigator.navigate(new Scene(dashboard.getView(), 600, 400));
        });

        HBox header = new HBox(10, backBtn, homeBtn);

        Label title = new Label("Admin Analytics");

        // 🔹 EXISTING LABELS (UNCHANGED)
        Label s = new Label();
        Label j = new Label();
        Label a = new Label();

        // 🔥 NEW LABELS (ADDED)
        Label placementRate = new Label();
        Label topCompany = new Label();
        Label topSkills = new Label();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // ================= EXISTING =================
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM students");
            rs1.next();
            s.setText("Students: " + rs1.getInt(1));

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM jobs");
            rs2.next();
            j.setText("Jobs: " + rs2.getInt(1));

            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM applications");
            rs3.next();
            a.setText("Applications: " + rs3.getInt(1));

            // ================= NEW FEATURES =================

            // 📊 Placement Rate
            ResultSet rs4 = stmt.executeQuery("""
                SELECT 
                (COUNT(CASE WHEN status='Selected' THEN 1 END) * 100.0 / COUNT(*)) AS rate
                FROM applications
            """);

            if (rs4.next()) {
                placementRate.setText("Placement Rate: " +
                        String.format("%.2f", rs4.getDouble("rate")) + "%");
            } else {
                placementRate.setText("Placement Rate: 0%");
            }

            // 🏢 Top Company
            ResultSet rs5 = stmt.executeQuery("""
                SELECT j.company, COUNT(*) AS total
                FROM applications a
                JOIN jobs j ON a.job_id = j.id
                WHERE a.status='Selected'
                GROUP BY j.company
                ORDER BY total DESC
                LIMIT 1
            """);

            if (rs5.next()) {
                topCompany.setText("Top Company: " + rs5.getString("company"));
            } else {
                topCompany.setText("Top Company: N/A");
            }

            // 🧠 Top Skills
            ResultSet rs6 = stmt.executeQuery("""
                SELECT skills, COUNT(*) AS total
                FROM students
                GROUP BY skills
                ORDER BY total DESC
                LIMIT 1
            """);

            if (rs6.next()) {
                topSkills.setText("Most Common Skills: " + rs6.getString("skills"));
            } else {
                topSkills.setText("Most Common Skills: N/A");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 🔥 ADDING NEW LABELS (WITHOUT REMOVING OLD ONES)
        view.getChildren().addAll(
                header,
                title,
                s, j, a,                 // existing
                placementRate,           // new
                topCompany,              // new
                topSkills                // new
        );
    }

    public VBox getView() {
        return view;
    }
}