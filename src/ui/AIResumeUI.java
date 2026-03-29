package ui;

import app.AppNavigator;
import database.DBConnection;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.OpenAIService;
import service.PDFService;

import java.sql.*;

public class AIResumeUI {

    private VBox view;

    public AIResumeUI(int userId) {

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

        Label title = new Label("AI Resume Builder");

        // 🔽 JOB DROPDOWN
        Label jobLabel = new Label("Select Job");
        ComboBox<String> jobDropdown = new ComboBox<>();
        ObservableList<String> jobList = FXCollections.observableArrayList();

        // LOAD JOBS FROM DB
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM jobs");

            while (rs.next()) {
                String display = rs.getInt("id") + " - " +
                        rs.getString("job_title") + " (" +
                        rs.getString("company") + ")";
                jobList.add(display);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        jobDropdown.setItems(jobList);

        // 👤 USER INPUTS
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField skillsField = new TextField();
        skillsField.setPromptText("Your Skills");

        TextArea educationArea = new TextArea();
        educationArea.setPromptText("Education");

        TextArea expArea = new TextArea();
        expArea.setPromptText("Experience");

        // 🎯 JOB DETAILS (AUTO FILLED)
        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Job Title");

        TextField companyField = new TextField();
        companyField.setPromptText("Company");

        TextField jobSkillsField = new TextField();
        jobSkillsField.setPromptText("Required Skills");

        // 🔥 AUTO-FILL LOGIC
        jobDropdown.setOnAction(e -> {

            String selected = jobDropdown.getValue();
            if (selected == null) return;

            int jobId = Integer.parseInt(selected.split(" - ")[0]);

            try (Connection conn = DBConnection.getConnection()) {

                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT * FROM jobs WHERE id=?");

                stmt.setInt(1, jobId);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    jobTitleField.setText(rs.getString("job_title"));
                    companyField.setText(rs.getString("company"));
                    jobSkillsField.setText(rs.getString("skills_required"));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // 📄 OUTPUT
        TextArea outputArea = new TextArea();
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(200);

        Button generateBtn = new Button("Generate Resume");
        Button autoGenerateBtn = new Button("Auto Generate Resume");
        Button downloadBtn = new Button("Download PDF");

        Label message = new Label();

        // 🤖 GENERATE RESUME
        generateBtn.setOnAction(e -> {

            String result = OpenAIService.generateResume(
                    nameField.getText(),
                    skillsField.getText(),
                    educationArea.getText(),
                    expArea.getText(),
                    jobTitleField.getText(),
                    companyField.getText(),
                    jobSkillsField.getText()
            );

            outputArea.setText(result);
            message.setText("Resume Generated!");
        });
        autoGenerateBtn.setOnAction(e -> {

            String selected = jobDropdown.getValue();

            if (selected == null) {
                message.setText("Select a job first!");
                return;
            }

            int jobId = Integer.parseInt(selected.split(" - ")[0]);

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
                String education = studentRs.getString("branch"); // you can customize
                String experience = String.valueOf(studentRs.getInt("experience"));

                // 🔹 FETCH JOB DATA
                PreparedStatement jobStmt = conn.prepareStatement(
                        "SELECT * FROM jobs WHERE id=?");
                jobStmt.setInt(1, jobId);

                ResultSet jobRs = jobStmt.executeQuery();

                if (!jobRs.next()) {
                    message.setText("Job not found!");
                    return;
                }

                String jobTitle = jobRs.getString("job_title");
                String company = jobRs.getString("company");
                String jobSkills = jobRs.getString("skills_required");

                // 🔥 CALL OPENAI
                String result = OpenAIService.generateResume(
                        name,
                        skills,
                        education,
                        experience,
                        jobTitle,
                        company,
                        jobSkills
                );

                outputArea.setText(result);
                message.setText("Auto Resume Generated!");

            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Error generating resume");
            }
        });

        // 📥 DOWNLOAD PDF
        downloadBtn.setOnAction(e -> {

            String content = outputArea.getText();

            if (content.isEmpty()) {
                message.setText("Generate resume first!");
                return;
            }

            PDFService.exportTextToPDF(content, "ai_resume.pdf");
            message.setText("Resume Downloaded!");
        });

        // 🧱 FINAL LAYOUT
        view.getChildren().addAll(
                header,
                title,
                jobLabel,
                jobDropdown,
                nameField,
                skillsField,
                educationArea,
                expArea,
                jobTitleField,
                companyField,
                jobSkillsField,
                generateBtn,
                autoGenerateBtn,
                outputArea,
                downloadBtn,
                message
        );
    }

    // ✅ IMPORTANT FIX (SCROLL ENABLED)
    public ScrollPane getView() {
        ScrollPane scroll = new ScrollPane(view);
        scroll.setFitToWidth(true);
        return scroll;
    }
}