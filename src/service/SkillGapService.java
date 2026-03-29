package service;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class SkillGapService {

    public static String getSkillGap(int studentId, int jobId) {

        try (Connection conn = DBConnection.getConnection()) {

            // 🔹 Get student skills
            String studentQuery = "SELECT skills FROM students WHERE id=?";
            PreparedStatement stmt1 = conn.prepareStatement(studentQuery);
            stmt1.setInt(1, studentId);

            ResultSet rs1 = stmt1.executeQuery();

            // 🔹 Get job skills
            String jobQuery = "SELECT skills_required FROM jobs WHERE id=?";
            PreparedStatement stmt2 = conn.prepareStatement(jobQuery);
            stmt2.setInt(1, jobId);

            ResultSet rs2 = stmt2.executeQuery();

            if (rs1.next() && rs2.next()) {

                Set<String> studentSkills = new HashSet<>(
                        Arrays.asList(rs1.getString("skills").toLowerCase().split(","))
                );

                Set<String> jobSkills = new HashSet<>(
                        Arrays.asList(rs2.getString("skills_required").toLowerCase().split(","))
                );

                List<String> missing = new ArrayList<>();

                for (String skill : jobSkills) {
                    if (!studentSkills.contains(skill.trim())) {
                        missing.add(skill.trim());
                    }
                }

                if (missing.isEmpty()) {
                    return "No skill gap 🎉";
                }

                return "Missing: " + String.join(", ", missing);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";
    }
}