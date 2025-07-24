package com.academic.examapp.db;

import java.sql.*;


public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/examdb";
    private static final String USER = "root";
    private static final String PASSWORD = "Ssu@2005";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public static int getLastSessionId(int studentId) {
        int sessionId = -1;
        try (Connection conn = getConnection()) {
            String sql = "SELECT session_id FROM exam_sessions WHERE student_id = ? ORDER BY session_id DESC LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                sessionId = rs.getInt("session_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionId;
    }
    
}
