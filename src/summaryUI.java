package com.academic.examapp.uimodule;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.*;
import java.util.Vector;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.academic.examapp.main.Buffer1;
import com.academic.examapp.questiongenerator.QuestionUI;
import com.academic.examapp.reportingsystem.SummaryGenerator;


// import com.academic.examapp.reportingsystem.ChartUpdater;


public class SummaryUI {
    JFrame frame;
    JPanel mainPanel; // New field to hold the main content
    JTable table;
    DefaultTableModel model;
    Vector<Vector<Object>> allRows;
    ChartPanel chartPanel;


    public SummaryUI(int studentId,int sessionID) {
        frame = new JFrame("Performance Summary");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        mainPanel = new JPanel(new BorderLayout());
    
        // Top Panel with Filter Checkbox
        JPanel topPanel = new JPanel(new FlowLayout());
        JCheckBox weakOnlyCheck = new JCheckBox("Show Only Weak Topics");
        topPanel.add(weakOnlyCheck);
        mainPanel.add(topPanel, BorderLayout.NORTH);
    
        // Table and Scroll Pane
        String[] columns = {"Topic", "Average Score", "Needs Improvement?"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        SummaryGenerator s=new SummaryGenerator();
        try {
            s.generateSummaryForSession(studentId,sessionID);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("EROR ON SUUMARYIUI");
        }
        // s.generateSummaryForSession(studentId,sessionID);
        // Load data first
        loadData(studentId,sessionID);
    
        // Create Chart based on loaded data
        chartPanel = createChartPanel(allRows);
    
        // Bottom Panel with Chart + Buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(chartPanel, BorderLayout.CENTER);
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backToLearningBtn = new JButton("📘 Back to Learning");
        JButton logoutBtn = new JButton("🚪 Logout");
      
        backToLearningBtn.addActionListener(e -> {
            frame.dispose();
            String studentName=fetchStudentName(studentId);
            new QuestionUI(studentId,studentName);
            JOptionPane.showMessageDialog(null, "Redirecting to Learning Module...");
            frame.dispose();
        });
    
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                frame.dispose();
                
                JOptionPane.showMessageDialog(null, "You have been logged out.");
                Buffer1.main(new String[]{}); 
                
            }
        });
    
        buttonPanel.add(backToLearningBtn);
        buttonPanel.add(logoutBtn);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    
        // Checkbox filtering
        weakOnlyCheck.addActionListener(_ -> {
            boolean showWeakOnly = weakOnlyCheck.isSelected();
            Vector<Vector<Object>> filteredRows = new Vector<>();
    
            if (showWeakOnly) {
                for (Vector<Object> row : allRows) {
                    if ("Yes".equalsIgnoreCase(row.get(2).toString())) {
                        filteredRows.add(row);
                    }
                }
            } else {
                filteredRows = allRows;
            }
    
            refreshTable(filteredRows);
            updateChart(filteredRows);
        });
    
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    


    public JPanel getPanel() {
        return mainPanel;
    }


    void loadData(int studentId,int sessionID) {
        allRows = new Vector<>();
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/examdb", "root", "Ssu@2005")) {


            String query = "SELECT topic, avg_score, weak_flag FROM performance_summary WHERE student_id = ? and session_id=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, studentId);
            pst.setInt(2, sessionID);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("topic"));
                row.add(rs.getFloat("avg_score"));
                row.add(rs.getBoolean("weak_flag") ? "Yes" : "No");
                allRows.add(row);
            }
            refreshTable(allRows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void refreshTable(Vector<Vector<Object>> data) {
        model.setRowCount(0);
        for (Vector<Object> row : data) {
            model.addRow(row);
        }
    }


    ChartPanel createChartPanel(Vector<Vector<Object>> rows) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Vector<Object> row : rows) {
            dataset.addValue(Float.parseFloat(row.get(1).toString()), "Score", row.get(0).toString());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Performance Chart", "Topic", "Average Score", dataset,
                PlotOrientation.VERTICAL, false, true, false);
        return new ChartPanel(chart);
    }


    void updateChart(Vector<Vector<Object>> rows) {
        mainPanel.remove(chartPanel);
        chartPanel = createChartPanel(rows);
        mainPanel.add(chartPanel, BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    String fetchStudentName(int studentId) {
        String name = "Student";
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/examdb", "root", "Ssu@2005")) {
    
            String query = "SELECT name FROM students WHERE student_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
    
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
    
}





