package com.academic.examapp.uimodule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class PerformanceSummaryUI {
    
    private JFrame frame;
    private JTable resultTable;
    private DefaultTableModel model;

    public PerformanceSummaryUI(int studentId) {
        System.out.println("Doneeee!!!!retegfdgfdg!!!");
        frame = new JFrame("Performance Summary");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Layout for UI
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Performance Summary", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table to display performance results
        resultTable = new JTable();
        model = new DefaultTableModel(new Object[]{"Topic", "Average Score", "Needs Improvement?"}, 0);
        resultTable.setModel(model);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load performance summary data into the table
        loadPerformanceSummary(studentId);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Load performance summary data from the database
    private void loadPerformanceSummary(int studentId) {
        Vector<Vector<Object>> rows = new Vector<>();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/examdb", "root", "password");
             PreparedStatement pst = conn.prepareStatement("SELECT topic, avg_score, weak_flag FROM performance_summary WHERE student_id = ?")) {

            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();

            // Fetch performance data
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("topic"));
                row.add(rs.getFloat("avg_score"));
                row.add(rs.getBoolean("weak_flag") ? "Yes" : "No");
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update the table with fetched data
        for (Vector<Object> row : rows) {
            model.addRow(row);
        }
    }
}
