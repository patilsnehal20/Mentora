package com.academic.examapp.uimodule;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.PriorityQueue;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.academic.examapp.db.DBConnection;
import com.academic.examapp.questiongenerator.QuestionUI;

import java.awt.*;
import java.sql.*;
import java.util.PriorityQueue;
import javax.swing.*;
public class StudentDashboard extends JFrame {
   private int studentId;
   public StudentDashboard(int studentId, String studentName) {
       this.studentId = studentId;
       // 1) Frame setup
       setTitle("Student Dashboard");
       setSize(600, 500);
       setLocationRelativeTo(null);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setLayout(new BorderLayout());
       // 2) Top banner
       JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
       top.setBackground(Color.WHITE);
       ImageIcon icon = new ImageIcon("D:/WhatsApp Image 2025-04-17 at 18.59.42_c4f31137.jpg");
       Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
       top.add(new JLabel(new ImageIcon(img)));
       JLabel welcome = new JLabel("Welcome, " + studentName + "!");
       welcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
       welcome.setForeground(new Color(45, 45, 45));
       top.add(welcome);
       add(top, BorderLayout.NORTH);
       // 3) Center panel (Start Test + Leaderboard)
       JPanel center = new JPanel();
       center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
       center.setBackground(new Color(240, 240, 240));
       center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
       // Start Test button
       JButton startTest = new JButton("Start Test");
       startTest.setAlignmentX(Component.CENTER_ALIGNMENT);
       startTest.setFont(new Font("Segoe UI", Font.BOLD, 16));
       startTest.setBackground(new Color(79, 129, 189));
       startTest.setForeground(Color.WHITE);
       startTest.setFocusPainted(false);
       startTest.setMaximumSize(new Dimension(200, 50));
       startTest.addMouseListener(new java.awt.event.MouseAdapter() {
           public void mouseEntered(java.awt.event.MouseEvent e) {
               startTest.setBackground(new Color(60, 110, 160));
           }
           public void mouseExited(java.awt.event.MouseEvent e) {
               startTest.setBackground(new Color(79, 129, 189));
           }
       });
       startTest.addActionListener(e -> {
           new ExamFrame(studentId);
           dispose();
       });
       center.add(Box.createVerticalGlue());
       center.add(startTest);
       JButton reviseButton = new JButton("Revise");
       reviseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
       reviseButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
       reviseButton.setBackground(new Color(79, 129, 189));
       reviseButton.setForeground(Color.WHITE);
       reviseButton.setFocusPainted(false);
       reviseButton.setMaximumSize(new Dimension(200, 50));
       reviseButton.addMouseListener(new java.awt.event.MouseAdapter() {
           public void mouseEntered(java.awt.event.MouseEvent e) {
            reviseButton.setBackground(new Color(60, 110, 160));
           }
           public void mouseExited(java.awt.event.MouseEvent e) {
            reviseButton.setBackground(new Color(79, 129, 189));
           }
       });
       reviseButton.addActionListener(e -> {
           new QuestionUI(studentId,studentName);
           dispose();
       });
       center.add(Box.createVerticalGlue());
       center.add(reviseButton);
       center.add(Box.createVerticalStrut(30));
       // 🏆 Leaderboard Panel
       JPanel leaderboardPanel = new JPanel();
       leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
       leaderboardPanel.setBorder(BorderFactory.createTitledBorder("🏆 Top 3 Students"));
       leaderboardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
       leaderboardPanel.setBackground(Color.WHITE);
       PriorityQueue<StudentScore> maxHeap = new PriorityQueue<>(
           (a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore())
       );
       try (Connection con = DBConnection.getConnection()) {
           String leaderboardQuery = "SELECT student_id, SUM(total_score) AS total_score FROM exam_sessions GROUP BY student_id";
           PreparedStatement pst = con.prepareStatement(leaderboardQuery);
           ResultSet rs = pst.executeQuery();
           while (rs.next()) {
               int id = rs.getInt("student_id");
               int score = rs.getInt("total_score");
               maxHeap.offer(new StudentScore(id, score));
           }
           int rank = 1;
           while (!maxHeap.isEmpty() && rank <= 3) {
               StudentScore s = maxHeap.poll();
               JLabel label = new JLabel(rank + ". Student ID: " + s.getStudentId() + " - " + s.getTotalScore() + " pts");
               label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
               leaderboardPanel.add(label);
               rank++;
           }
           rs.close();
           pst.close();
       } catch (Exception e) {
           e.printStackTrace();
           leaderboardPanel.add(new JLabel("Error loading leaderboard."));
       }
       center.add(leaderboardPanel);
       center.add(Box.createVerticalGlue());
       add(center, BorderLayout.CENTER);
       // 4) Footer
       JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
       footer.setBackground(Color.WHITE);
       JLabel lastScore = new JLabel("Last Score: " + fetchLastScore(studentId));
       lastScore.setFont(new Font("Segoe UI", Font.PLAIN, 14));
       footer.add(lastScore);
       add(footer, BorderLayout.SOUTH);
       setVisible(true);
   }
   private String fetchLastScore(int studentId) {
       String score = "N/A";
       String sql = "SELECT total_score FROM exam_sessions " +
                    "WHERE student_id = ? AND total_score IS NOT NULL " +
                    "ORDER BY end_time DESC LIMIT 1";
       try (Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)) {
           pst.setInt(1, studentId);
           try (ResultSet rs = pst.executeQuery()) {
               if (rs.next()) {
                   score = String.valueOf(rs.getInt("total_score"));
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return score;
   }
}
class StudentScore {
   int studentId;
   int totalScore;
   public StudentScore(int studentId, int totalScore) {
       this.studentId = studentId;
       this.totalScore = totalScore;
   }
   public int getStudentId() {
       return studentId;
   }
   public int getTotalScore() {
       return totalScore;
   }
}
