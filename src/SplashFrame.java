package com.academic.examapp.model;
import javax.swing.*;
import com.academic.examapp.uimodule.Login_Frame;

import java.awt.*;
public class SplashFrame extends JFrame {
   public SplashFrame() {
       setTitle("Welcome");
       setSize(500, 350);
       setLocationRelativeTo(null);
       setLayout(null);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       // — your logo
       JLabel logo = new JLabel(new ImageIcon(
    new ImageIcon("D:/WhatsApp Image 2025-04-17 at 18.59.42_c4f31137.jpg")
        .getImage()
        .getScaledInstance(150, 80, Image.SCALE_SMOOTH)
));
logo.setBounds(175, 20, 150, 80);
add(logo);

       // — your welcome text
       JLabel welcome = new JLabel("Welcome to the Adaptive Exam System", SwingConstants.CENTER);
       welcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
       welcome.setBounds(50, 120, 400, 30);
       add(welcome);
       JLabel prompt = new JLabel("Please log in to continue", SwingConstants.CENTER);
       prompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
       prompt.setBounds(50, 160, 400, 25);
       add(prompt);
       // — “Log In” button
       JButton loginBtn = new JButton("Log In");
       loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
       loginBtn.setBackground(new Color(59, 89, 182));
       loginBtn.setForeground(Color.WHITE);
       loginBtn.setFocusPainted(false);
       loginBtn.setBounds(200, 210, 100, 35);
       add(loginBtn);
       // when clicked: close splash and open your login frame
       loginBtn.addActionListener(e -> {
           dispose();
           new Login_Frame();   // your existing login screen
       });
       setVisible(true);
   }
}
