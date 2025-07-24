package com.academic.examapp.uimodule;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class reviewFrame extends JFrame {
    private JLabel questionLabel;
    private JRadioButton[] options;
    private ButtonGroup group;
    private JLabel statusLabel;
    private JButton prevButton, nextButton;
    private ArrayList<ExamFrame.QuestionState> reviewList;
    private int currentIndex = 0;
    private int studentId;
    private int sessionId;

    public reviewFrame(ArrayList<ExamFrame.QuestionState> reviewList,int studentId,int sessionId) {
        this.reviewList = reviewList;
        this.studentId=studentId;
        this.sessionId=sessionId;

        setTitle("Review Test");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        questionLabel = new JLabel("Question will appear here");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        options = new JRadioButton[4];
        group = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setEnabled(false); // Disable editing
            group.add(options[i]);
            optionsPanel.add(options[i]);
        }
        add(optionsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        statusLabel = new JLabel("Correct / Incorrect");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);

        JPanel navPanel = new JPanel();
        prevButton = new JButton("Previous");
        nextButton = new JButton();

        navPanel.add(prevButton);
        navPanel.add(nextButton);
        bottomPanel.add(navPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        prevButton.addActionListener(e -> {
            if (currentIndex > 0) {
                currentIndex--;
                showQuestion(currentIndex);
            }
        });

        nextButton.addActionListener(e -> {
            if (currentIndex < reviewList.size() - 1) {
                currentIndex++;
                showQuestion(currentIndex);
            }
        });


        showQuestion(currentIndex);
        setVisible(true);
    }

    private void showQuestion(int index) {
        ExamFrame.QuestionState state = reviewList.get(index);
        questionLabel.setText("Q" + (index + 1) + ": " + state.question.questionText);

        for (int i = 0; i < 4; i++) {
            options[i].setText(state.question.options[i]);
            options[i].setSelected(false);
        }

        // Select what the student selected
        if (state.selectedOption != null) {
            switch (state.selectedOption.toLowerCase()) {
                case "a" -> options[0].setSelected(true);
                case "b" -> options[1].setSelected(true);
                case "c" -> options[2].setSelected(true);
                case "d" -> options[3].setSelected(true);
            }
        }

        boolean isCorrect = state.selectedOption != null &&
                state.selectedOption.equalsIgnoreCase(state.question.correctOption);

        statusLabel.setText("Your Answer: " + (state.selectedOption == null ? "Not Answered" : state.selectedOption.toUpperCase()) +
                " | Correct Answer: " + state.question.correctOption.toUpperCase() +
                " | " + (isCorrect ? "Correct" : "Incorrect"));

        // Update nav buttons
        prevButton.setEnabled(index > 0);

        if (index == reviewList.size() - 1) {
            nextButton.setText("Finish Review");

            // Remove old listeners
            for (ActionListener al : nextButton.getActionListeners()) {
                nextButton.removeActionListener(al);
            }

            // Finish review only when button is clicked after last question is shown
            nextButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Review Finished!");
                dispose(); // Or redirect if needed
                new SummaryUI(studentId,sessionId);
            });
        } else {
            nextButton.setText("Next");

            // Reset listener to show next question
            for (ActionListener al : nextButton.getActionListeners()) {
                nextButton.removeActionListener(al);
            }

            nextButton.addActionListener(e -> {
                if (currentIndex < reviewList.size() - 1) {
                    currentIndex++;
                    showQuestion(currentIndex);
                }
            });
        }
    }

}

