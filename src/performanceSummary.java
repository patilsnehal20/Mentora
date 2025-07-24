package com.academic.examapp.uimodule;

import java.sql.*;
import java.util.*;

public class PerformanceSummary {

    public static void generateSummary(int studentId) {
        Map<String, Float> performanceData = new HashMap<>();
        TopicGraph graph = new TopicGraph();

        // Fetch performance data from database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/examdb", "root", "password");
             PreparedStatement pst = conn.prepareStatement("SELECT topic, avg_score FROM performance_summary WHERE student_id = ?")) {

            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String topic = rs.getString("topic");
                float score = rs.getFloat("avg_score");
                performanceData.put(topic, score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Generate the performance summary
        System.out.println("Performance Summary for Student " + studentId);
        for (Map.Entry<String, Float> entry : performanceData.entrySet()) {
            String topic = entry.getKey();
            float score = entry.getValue();
            System.out.println("Topic: " + topic + " | Score: " + score);
        }

        // Analyze weak topics based on scores
        List<String> weakTopics = new ArrayList<>();
        for (Map.Entry<String, Float> entry : performanceData.entrySet()) {
            if (entry.getValue() < 50.0) {  // Weak if score is less than 50%
                weakTopics.add(entry.getKey());
            }
        }

        System.out.println("Weak Topics: " + weakTopics);
    }
}



