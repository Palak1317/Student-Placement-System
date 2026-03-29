package service;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MatchingService {

    public static double calculateMatchScore(int studentId, int jobId) {

        double score = 0;

        try (Connection conn = DBConnection.getConnection()) {

            // Get student data
            String studentQuery = "SELECT * FROM students WHERE id=?";
            PreparedStatement sStmt = conn.prepareStatement(studentQuery);
            sStmt.setInt(1, studentId);
            ResultSet sRs = sStmt.executeQuery();

            // Get job data
            String jobQuery = "SELECT * FROM jobs WHERE id=?";
            PreparedStatement jStmt = conn.prepareStatement(jobQuery);
            jStmt.setInt(1, jobId);
            ResultSet jRs = jStmt.executeQuery();

            if (sRs.next() && jRs.next()) {

                String studentSkills = sRs.getString("skills").toLowerCase();
                String jobSkills = jRs.getString("skills_required").toLowerCase();

                double studentCgpa = sRs.getDouble("cgpa");
                double jobCgpa = jRs.getDouble("min_cgpa");

                int studentExp = sRs.getInt("experience");
                int jobExp = jRs.getInt("experience_required");

                String studentBranch = sRs.getString("branch").toLowerCase();

                // 🔹 Skill match (50%)
                int matched = 0;
                String[] jobSkillList = jobSkills.split(",");

                for (String skill : jobSkillList) {
                    if (studentSkills.contains(skill.trim())) {
                        matched++;
                    }
                }

                double skillScore = (double) matched / jobSkillList.length;
                score += skillScore * 50;

                // 🔹 CGPA match (20%)
                if (studentCgpa >= jobCgpa) {
                    score += 20;
                }

                // 🔹 Experience match (20%)
                if (studentExp >= jobExp) {
                    score += 20;
                }

                // 🔹 Branch match (10%)
                if (studentBranch.contains("cse")) {
                    score += 10;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return score;
    }
}