package com.academic.examapp.uimodule;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.academic.examapp.db.DBConnection;

public class Login_Frame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    public Login_Frame() {
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400); // Increased height for logo
        setLocationRelativeTo(null); // Center the frame
        setLayout(null);
        // 🔷 LOGO
        JLabel logoLabel = new JLabel();
        logoLabel.setBounds(110, 10, 150, 80); // Adjust size and position as needed
        // Load image (make sure the image file is in your project path)
        //ImageIcon icon = new ImageIcon("D:/BUFFER/logo_ferrari.jpg"); // Update the path as needed
        ImageIcon icon = new ImageIcon("D:/WhatsApp Image 2025-04-17 at 18.59.42_c4f31137.jpg");
        Image scaledImage = icon.getImage().getScaledInstance(150, 80, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledImage));
        add(logoLabel);
        // 🔷 LOGIN PANEL
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(30, 100, 420, 230);
        panel.setBorder(BorderFactory.createTitledBorder("Please Login"));
        add(panel);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 30, 100, 25);
        lblUsername.setFont(labelFont);
        panel.add(lblUsername);
        usernameField = new JTextField();
        usernameField.setBounds(150, 30, 200, 25);
        usernameField.setFont(labelFont);
        panel.add(usernameField);
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 70, 100, 25);
        lblPassword.setFont(labelFont);
        panel.add(lblPassword);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 200, 25);
        passwordField.setFont(labelFont);
        panel.add(passwordField);
        JLabel lblRole = new JLabel("Role:");
        lblRole.setBounds(50, 110, 100, 25);
        lblRole.setFont(labelFont);
        panel.add(lblRole);
        String[] roles = {"Student", "Admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(150, 110, 200, 25);
        roleComboBox.setFont(labelFont);
        panel.add(roleComboBox);
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(160, 160, 100, 30);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(59, 89, 182));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        panel.add(loginButton);
         loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
            try (Connection con = DBConnection.getConnection()) {
                String query;
                PreparedStatement pst;
                if (role.equals("Student")) {
                    query = "SELECT * FROM students WHERE email = ? AND password = ?";
                } else {
                    query = "SELECT * FROM admin_users WHERE username = ? AND password = ?";
                }
                pst = con.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful as " + role);
                    dispose();
                    if (role.equals("Student")) {
                        int studentId = rs.getInt("student_id");
                        String studentName = rs.getString("name");
                        new StudentDashboard(studentId, studentName);
                    }
                        else if (role.equals("Admin")) {
                            SwingUtilities.invokeLater(() -> new AdminDashboard());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error connecting to DB", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        setVisible(true);
      
    }
 }
 