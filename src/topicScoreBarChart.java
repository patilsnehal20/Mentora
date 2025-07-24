package com.academic.examapp.reportingsystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class TopicScoreBarChartFromDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/your_database_name"; // replace with your DB
        String user = "root"; // your MySQL username
        String password = "ySsu@2005"; // your MySQL password

        int studentId = 1; // Example student ID, replace or fetch dynamically

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            String query = "SELECT topic, avg_score FROM performance_summary WHERE student_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String topic = rs.getString("topic");
                double avgScore = rs.getDouble("avg_score");
                dataset.addValue(avgScore, "Score", topic);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create and display chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Topic-wise Performance",
                "Topic",
                "Average Score",
                dataset
        );
        ChartFrame frame = new ChartFrame("Performance Chart", chart);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}


