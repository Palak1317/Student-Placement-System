package ui;

import app.AppNavigator;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DashboardUI {

    private VBox view;
    private int userId;

    public DashboardUI(String role, int userId) {
        this.userId = userId;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Dashboard - " + role.toUpperCase());

        Button btn1 = new Button();
        Button btn2 = new Button();
        Button btn3 = new Button();
        Button btn4 = new Button();

        // 🔓 Logout Button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            AppNavigator.clearHistory();
            LoginUI loginUI = new LoginUI();
            AppNavigator.navigate(new Scene(loginUI.getView(), 400, 300));
        });

        // ================= ADMIN =================
        if ("admin".equals(role)) {

            btn1.setText("Add Job");
            btn2.setText("Manage Applications");
            btn3.setText("Analytics");

            btn1.setOnAction(e -> {
                AddJobUI addJobUI = new AddJobUI();
                AppNavigator.navigate(new Scene(addJobUI.getView(), 600, 400));
            });

            btn2.setOnAction(e -> {
                ManageApplicationsUI manageUI = new ManageApplicationsUI();
                AppNavigator.navigate(new Scene(manageUI.getView(), 700, 500));
            });

            btn3.setOnAction(e -> {
                AdminAnalyticsUI analyticsUI = new AdminAnalyticsUI();
                AppNavigator.navigate(new Scene(analyticsUI.getView(), 600, 400));
            });
            view.getChildren().addAll(title, btn1, btn2, btn3, logoutBtn);

        }
        // ================= STUDENT =================
        else {

            btn1.setText("View Jobs");
            btn2.setText("Apply for Job");
            btn3.setText("Recommended Jobs");
            btn4.setText("Upload Resume");

            btn1.setOnAction(e -> {
                ViewJobsUI viewJobsUI = new ViewJobsUI(userId, role);
                AppNavigator.navigate(new Scene(viewJobsUI.getView(), 600, 400));
            });

            btn2.setOnAction(e -> {
                ApplyJobUI applyJobUI = new ApplyJobUI(userId);
                AppNavigator.navigate(new Scene(applyJobUI.getView(), 600, 400));
            });

            btn3.setOnAction(e -> {
                RecommendationUI recommendationUI = new RecommendationUI(userId);
                AppNavigator.navigate(new Scene(recommendationUI.getView(), 600, 400));
            });

            btn4.setOnAction(e -> {
                ResumeUploadUI resumeUI = new ResumeUploadUI(userId);
                AppNavigator.navigate(new Scene(resumeUI.getView(), 600, 400));
            });

            Button btnSaved = new Button("Saved Jobs");

            btnSaved.setOnAction(e -> {
                SavedJobsUI savedJobsUI = new SavedJobsUI(userId);
                AppNavigator.navigate(new Scene(savedJobsUI.getView(), 600, 400));
            });

            view.getChildren().addAll(title, btn1, btn2, btn3, btn4, btnSaved, logoutBtn);
        }

        //view.getChildren().addAll(title, btn1, btn2, btn3, btn4, logoutBtn);
    }

    public VBox getView() {
        return view;
    }
}