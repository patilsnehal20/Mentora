package com.academic.examapp.uimodule;

import com.academic.examapp.cheating.CheatMonitor;
import com.academic.examapp.db.DBConnection;
import com.academic.examapp.main.Buffer1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Map;
import java.util.Queue;

public class AdminDashboard extends JFrame {
    private JTable logsTable;
    private DefaultTableModel model;

    public AdminDashboard() {
        setTitle("Admin Dashboard - Cheating Logs");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
         // ✅ Create top panel with logout button
         JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
         JButton logoutButton = new JButton("Logout");
          logoutButton.addActionListener(e -> {
          dispose(); // Close AdminDashboard
          Buffer1.main(new String[]{}); // Relaunch the login screen
         });

         topPanel.add(logoutButton);
         add(topPanel, BorderLayout.NORTH); // Add topPanel to NORTH

         JButton plButton = new JButton("Check Plagiarism");
         plButton.addActionListener(e -> {
         dispose(); // Close AdminDashboard
         new SubjectiveExamFrame(1).setVisible(true); // Relaunch the login screen
        });

        topPanel.add(plButton);
        add(topPanel, BorderLayout.NORTH); // Add topPanel to NORTH

        String[] columnNames = {"Student ID", "Session ID", "Timestamp", "Cheat Type", "Description"};
        model = new DefaultTableModel(columnNames, 0); // ✅ Fixed line
        logsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(logsTable);

        add(scrollPane, BorderLayout.CENTER);
        
        loadCheatingLogsFromDatabase();
        loadCheatingLogsFromMemory();

        
    
        setVisible(true);
    }

    private void loadCheatingLogsFromDatabase() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT student_id, session_id, timestamp, cheat_type, description FROM cheating_logs ORDER BY timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
            	// Formatting the timestamp
                Timestamp timestamp = rs.getTimestamp("timestamp");
                String formattedTimestamp = timestamp != null ? timestamp.toString() : "N/A";

                Object[] row = {
                    rs.getString("student_id"),
                    rs.getString("session_id"),
                    formattedTimestamp, // formatted timestamp
                    rs.getString("cheat_type"),
                    rs.getString("description")
                };
                model.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load cheating logs from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCheatingLogsFromMemory() {
        Map<String, Queue<String>> memoryLogs = CheatMonitor.getAllStudentLogs();

        for (Map.Entry<String, Queue<String>> entry : memoryLogs.entrySet()) {
            String studentId = entry.getKey();
            Queue<String> logs = entry.getValue();

            for (String log : logs) {
                Object[] row = {
                    "Memory",
                    studentId,
                    "N/A",
                    log.split(" - ")[0], // Timestamp
                    detectCheatType(log),
                    log.substring(log.indexOf(":") + 1).trim()
                };
                model.addRow(row);
            }
        }
    }

    private String detectCheatType(String log) {
        if (log.contains("Tab Switch")) return "Tab Switch";
        if (log.contains("Inactivity")) return "Inactivity";
        if (log.contains("Keystroke")) return "Keystroke Anomaly";
        return "Other";
    }
}
