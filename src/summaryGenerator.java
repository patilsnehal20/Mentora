package com.academic.examapp.reportingsystem;

import com.academic.examapp.db.DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryGenerator {

    public HashMap<String, Integer> generateSummaryForSession(int studentId, int sessionId) throws SQLException {
        HashMap<String, Integer> topicScores = new HashMap<>();
        System.out.println("Summary called");
    
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT q.topic, sr.is_correct FROM student_response sr " +
                           "JOIN questions q ON sr.question_id = q.question_id " +
                           "WHERE sr.student_id = ? AND sr.sessionId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            ps.setInt(2, sessionId);
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                String topic = rs.getString("topic");
                boolean isCorrect = rs.getBoolean("is_correct");
    
                topicScores.putIfAbsent(topic, 0);
                if (isCorrect) {
                    topicScores.put(topic, topicScores.get(topic) + 1);
                }
            }
    
            // ✅ Use computed scores, not from database
            Map<String, Integer> performanceData = topicScores;
    
            // Start transaction
            conn.setAutoCommit(false);
    
            System.out.println("Performance Summary for Student " + studentId);
            for (Map.Entry<String, Integer> entry : performanceData.entrySet()) {
                String topic = entry.getKey();
                int score = entry.getValue();
                System.out.println("Topic: " + topic + " | Score: " + score);
            }
    
            // Identify weak topics
            List<String> weakTopics = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : performanceData.entrySet()) {
                if (entry.getValue() == 0) {
                    weakTopics.add(entry.getKey());
                }
            }
    
            // Insert performance summary
            for (Map.Entry<String, Integer> entry : performanceData.entrySet()) {
                String topic = entry.getKey();
                int score = entry.getValue();
                boolean isWeak = weakTopics.contains(topic);
                int res = isWeak ? 1 : 0;
    
                System.out.println("Inserting: " + topic + " | " + score + " | Weak: " + res);
    
                try {
                    PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO performance_summary(session_id, student_id, topic, avg_score, weak_flag) VALUES (?, ?, ?, ?, ?)"
                    );
                    insert.setInt(1, sessionId);
                    insert.setInt(2, studentId);
                    insert.setString(3, topic);
                    insert.setInt(4, score);
                    insert.setInt(5, res);
                    insert.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace(); // Helpful for debugging
                }
            }
    
            conn.commit(); // Commit transaction
        }
    
        return topicScores;
    }
}    