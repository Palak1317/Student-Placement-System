package ui;

import app.AppNavigator;
import database.DBConnection;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import service.OpenAIService;
import service.PDFService;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ResumeUploadUI {

    private VBox view;

    public ResumeUploadUI(int userId) {

        view = new VBox(15);
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

        Label title = new Label("Upload Resume");

        Label fileLabel = new Label("No file selected");

        Button chooseBtn = new Button("Choose File");

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        final File[] selectedFile = new File[1];

        chooseBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                selectedFile[0] = file;
                fileLabel.setText(file.getName());
            }
        });

        Button uploadBtn = new Button("Upload");
        Label message = new Label();

        uploadBtn.setOnAction(e -> {

            if (selectedFile[0] == null) {
                message.setText("⚠ Please select a file!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {

                String query = "UPDATE students SET resume=? WHERE id=?";

                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, selectedFile[0].getAbsolutePath());
                stmt.setInt(2, userId);

                stmt.executeUpdate();

                message.setText("✅ Resume Uploaded Successfully!");

            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("❌ Error uploading resume");
            }
        });

        Button viewBtn = new Button("View Resume");

        viewBtn.setOnAction(e -> {
            try (Connection conn = DBConnection.getConnection()) {

                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT resume FROM students WHERE id=?");

                stmt.setInt(1, userId);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String path = rs.getString("resume");

                    if (path == null || path.isEmpty()) {
                        message.setText("⚠ No resume uploaded!");
                        return;
                    }

                    File file = new File(path);

                    if (!file.exists()) {
                        message.setText("❌ File not found!");
                        return;
                    }

                    java.awt.Desktop.getDesktop().open(file);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("❌ Error opening resume");
            }
        });

        // 🔥 NEW BUTTON (AI RESUME GENERATION)
        Button generateBtn = new Button("Generate Resume (AI)");

        generateBtn.setOnAction(e -> {

            try (Connection conn = DBConnection.getConnection()) {

                // 🔹 FETCH STUDENT DATA
                PreparedStatement studentStmt = conn.prepareStatement(
                        "SELECT * FROM students WHERE id=?");
                studentStmt.setInt(1, userId);

                ResultSet studentRs = studentStmt.executeQuery();

                if (!studentRs.next()) {
                    message.setText("Student data not found!");
                    return;
                }

                String name = studentRs.getString("name");
                String skills = studentRs.getString("skills");
                String education = studentRs.getString("branch");
                String experience = String.valueOf(studentRs.getInt("experience"));

                // 🔹 FETCH BEST APPLIED JOB
                PreparedStatement jobStmt = conn.prepareStatement("""
                        SELECT j.*
                        FROM applications a
                        JOIN jobs j ON a.job_id = j.id
                        WHERE a.student_id = ?
                        ORDER BY a.match_score DESC
                        LIMIT 1
                """);

                jobStmt.setInt(1, userId);

                ResultSet jobRs = jobStmt.executeQuery();

                if (!jobRs.next()) {
                    message.setText("⚠ Apply for a job first!");
                    return;
                }

                String jobTitle = jobRs.getString("job_title");
                String company = jobRs.getString("company");
                String jobSkills = jobRs.getString("skills_required");

                // 🔥 GENERATE AI RESUME
                String result = OpenAIService.generateResume(
                        name,
                        skills,
                        education,
                        experience,
                        jobTitle,
                        company,
                        jobSkills
                );

                // 🔥 AUTO DOWNLOAD
                PDFService.exportTextToPDF(result, "auto_generated_resume.pdf");

                message.setText("✅ Resume Generated & Downloaded!");

            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("❌ Error generating resume");
            }
        });

        // ✅ FINAL LAYOUT (ONLY ADD NEW BUTTON)
        view.getChildren().addAll(
                header,
                title,
                fileLabel,
                chooseBtn,
                uploadBtn,
                viewBtn,
                generateBtn,   // 🔥 ADDED
                message
        );
    }

    public VBox getView() {
        return view;
    }
}