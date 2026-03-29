package ui;

import database.DBConnection;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;

public class ProfileUI {

    private VBox view;

    public ProfileUI(int userId) {

        view = new VBox(10);

        TextField skills = new TextField();
        TextField cgpa = new TextField();

        Button update = new Button("Update");

        update.setOnAction(e -> {
            try (Connection conn = DBConnection.getConnection()) {

                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE students SET skills=?, cgpa=? WHERE id=?");

                stmt.setString(1, skills.getText());
                stmt.setDouble(2, Double.parseDouble(cgpa.getText()));
                stmt.setInt(3, userId);

                stmt.executeUpdate();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        view.getChildren().addAll(skills, cgpa, update);
    }

    public VBox getView() {
        return view;
    }
}