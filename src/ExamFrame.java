package com.academic.examapp.uimodule;

import java.util.HashSet;
import java.util.Stack;
import javax.swing.*;
import com.academic.examapp.db.DBConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Collections;
import java.util.HashMap;

class ExamFrame extends JFrame implements ActionListener, KeyListener, MouseListener,WindowFocusListener, WindowListener {
    private boolean examFinished = false;
    private final int MAX_QUESTIONS = 5;
    private int questionsAnswered = 0;
    private HashSet<Integer> askedQuestionIds = new HashSet<>();
    private int studentId;
    private int sessionId;
    private int totalScore = 0;
    private int timeLeft = 60;
    private Stack<QuestionState> questionHistory = new Stack<>();
    private ArrayList<Question> allQuestions = new ArrayList<>();
    private HashMap<String, ArrayList<Question>> difficultyMap = new HashMap<>();
    private Question currentQuestion;
    private JLabel questionLabel;
    private JRadioButton[] options;
    private ButtonGroup group;
    private JButton nextButton;
    private JButton prevButton;
    private JLabel timerLabel;
    private String currentDifficulty = "Easy"; // Start with Easy
    private long lastActivityTime;
    private javax.swing.Timer afkTimer;
    private JButton powerUpButton;
    private boolean isPowerUpActive = false;  // Tracks if power-up is active for the current question
    private int baseScore = 10;  // Set a base score value if needed (modify as per your logic)
  

    public ExamFrame(int studentId) {
            this.studentId = studentId;

            // Event listeners
            addWindowFocusListener(this);
            addWindowListener(this);
            addMouseListener(this);
            addKeyListener(this);
            setFocusable(true);

            // Frame setup
            setTitle("📝 Exam Panel");
            setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout(20, 20));
            getContentPane().setBackground(new Color(245, 245, 245));

            // Title / Question area
            questionLabel = new JLabel("Loading question...");
            questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            questionLabel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
            add(questionLabel, BorderLayout.NORTH);

            // Options Panel
            JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 15, 15));
            optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));
            optionsPanel.setBackground(new Color(245, 245, 245));

            options = new JRadioButton[4];
            group = new ButtonGroup();
            for (int i = 0; i < 4; i++) {
                options[i] = new JRadioButton("Option " + (i + 1));
                options[i].setFont(new Font("Segoe UI", Font.PLAIN, 20));
                options[i].setBackground(Color.WHITE);
                options[i].setFocusPainted(false);
                options[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
                group.add(options[i]);
                optionsPanel.add(options[i]);
            }
            add(optionsPanel, BorderLayout.CENTER);

            // Bottom Panel (Timer + Navigation)
            JPanel bottomPanel = new JPanel(new BorderLayout(30, 30));
            bottomPanel.setBackground(Color.WHITE);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

            // Timer Label
            timerLabel = new JLabel("⏳ Time Left: 60s");
            timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            bottomPanel.add(timerLabel, BorderLayout.WEST);

            // Navigation Buttons
            JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
            navButtons.setBackground(Color.WHITE);

            prevButton = new JButton("< Previous");
            nextButton = new JButton("Next >");

            JButton[] navArray = {prevButton, nextButton};
            for (JButton btn : navArray) {
                btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
                btn.setBackground(new Color(66, 133, 244));
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(180, 50));
                navButtons.add(btn);
            }

            prevButton.addActionListener(e -> undoLastAction());
            nextButton.addActionListener(e -> recordAnswer());

            bottomPanel.add(navButtons, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            // Load questions + start session
            loadQuestionsFromDB();
            startExamSession();
         // Declare the power-up button
          
         // In your constructor or setup method
            powerUpButton = new JButton("$ Power-Up");
            powerUpButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
            powerUpButton.setBackground(new Color(0, 204, 102));  // Green color
            powerUpButton.setForeground(Color.WHITE);
            powerUpButton.setFocusPainted(false);
            powerUpButton.setPreferredSize(new Dimension(200, 50));

            // Add action listener to the power-up button
            powerUpButton.addActionListener(e -> {
                isPowerUpActive = true;  // Activate the power-up effect for the current question
                System.out.println("Power-Up Activated!");
            });

            // Add the button to your layout (for example, add it to the bottom panel or somewhere visible)
            bottomPanel.add(powerUpButton, BorderLayout.EAST);

            // Set the frame visible
            setVisible(true);

            // Initialize lastActivityTime
            lastActivityTime = System.currentTimeMillis();

            // Start AFK detection timer
            afkTimer = new javax.swing.Timer(5000, e -> checkAFK());
            afkTimer.start();
    }
    private void checkAFK() {
            if (examFinished) return; // don't check after exam is finished
       
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastActivityTime >= 15000) {
                // AFK detected
                logCheatingEvent("AFK","AFK for more than 15 seconds");
                lastActivityTime = currentTime; // reset after logging
            }
        }
    @Override
    public void keyPressed(KeyEvent e) {
        lastActivityTime = System.currentTimeMillis();
    }
 @Override
 public void mouseClicked(MouseEvent e) {
     lastActivityTime = System.currentTimeMillis();
 }
@Override
public void actionPerformed(ActionEvent e) {
    // You can leave this empty if you're not using it directly
}
 // Empty but required for interfaces
 @Override public void keyReleased(KeyEvent e) {}
 @Override public void keyTyped(KeyEvent e) {}
 @Override public void mousePressed(MouseEvent e) {}
 @Override public void mouseReleased(MouseEvent e) {}
 @Override public void mouseEntered(MouseEvent e) {}
 @Override public void mouseExited(MouseEvent e) {}

    private void loadQuestionsFromDB() {
        try (Connection con = DBConnection.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions");
            while (rs.next()) {
                int id = rs.getInt("question_id");
                String questionText = rs.getString("question_text");
                String[] opts = {
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d")
                };
                String correctOption = rs.getString("correct_option");
                int marks = rs.getInt("marks");
                String difficulty = rs.getString("difficulty");

                Question q = new Question(id, questionText, opts, correctOption, marks, difficulty);
                allQuestions.add(q);

                difficultyMap.computeIfAbsent(difficulty, k -> new ArrayList<>()).add(q);
            }
            for (ArrayList<Question> qList : difficultyMap.values()) {
                Collections.shuffle(qList);
            }

            loadNextQuestionBasedOnDifficulty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private final HashMap<String, Integer> difficultyTimeMap = new HashMap<>() {{
        put("Easy", 15);
        put("Medium", 30);
        put("Hard", 59);
    }};
    private void showQuestion(Question q) {
        currentQuestion = q;
        questionLabel.setText("Q" + (questionsAnswered + 1) + ": " + q.questionText);
        for (int i = 0; i < 4; i++) {
            options[i].setText(q.options[i]);
        }
        group.clearSelection();
        int secondsForThisQuestion = difficultyTimeMap.getOrDefault(q.difficulty, 30);
        startTimer(secondsForThisQuestion);
    }
    private void undoLastAction() {
        if (questionHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ No previous question to undo.");
            return;
        }
        QuestionState prevState = questionHistory.pop();
        currentQuestion = prevState.question;
        totalScore = prevState.scoreBefore;
        currentDifficulty = prevState.difficultyBefore;
        questionsAnswered--;
        askedQuestionIds.remove(currentQuestion.id);
        showQuestion(currentQuestion);
        if (prevState.selectedOption != null) {
            switch (prevState.selectedOption.toLowerCase()) {
                case "a" -> options[0].setSelected(true);
                case "b" -> options[1].setSelected(true);
                case "c" -> options[2].setSelected(true);
                case "d" -> options[3].setSelected(true);
            }
        }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM student_response WHERE student_id = ? AND question_id = ?");
            pst.setInt(1, studentId);
            pst.setInt(2, currentQuestion.id);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void recordAnswer() {
        String selectedOption = null;
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected()) {
                selectedOption = getOptionLetter(i);
                break;
            }
        }

        if (selectedOption == null && timeLeft > 0) {
            JOptionPane.showMessageDialog(this, "⚠ Please select an option.");
            return;
        }

        if (questionsAnswered >= MAX_QUESTIONS) {
            finishExam();
            return;
        }

        if (timeLeft <= 0 && selectedOption == null) {
            System.out.println("Question skipped due to timeout.");
            askedQuestionIds.add(currentQuestion.id);
            questionsAnswered++;
            loadNextQuestionBasedOnDifficulty();
            return;
        }

        questionHistory.push(new QuestionState(currentQuestion, selectedOption, totalScore, currentDifficulty));

        boolean isCorrect = selectedOption.equalsIgnoreCase(currentQuestion.correctOption);
        int marksAwarded = 0;

        if (isPowerUpActive) {
            if (isCorrect) {
                marksAwarded = 2 * currentQuestion.marks;
                totalScore += marksAwarded;
                System.out.println("✅ Correct with Power-Up! Score doubled.");
            } else {
                marksAwarded = -1;
                totalScore += marksAwarded;
                System.out.println("❌ Wrong with Power-Up! -1 score.");
            }
            isPowerUpActive = false; // Reset power-up after use
        } else {
            if (isCorrect) {
                marksAwarded = currentQuestion.marks;
                totalScore += marksAwarded;
                System.out.println("✅ Correct without Power-Up.");
            } else {
                marksAwarded = 0;
                System.out.println("❌ Wrong without Power-Up.");
            }
        }

        // Difficulty adjustment (independent of power-up)
        if (isCorrect) {
            if (currentDifficulty.equals("Easy")) currentDifficulty = "Medium";
            else if (currentDifficulty.equals("Medium")) currentDifficulty = "Hard";
        } else {
            if (currentDifficulty.equals("Hard")) currentDifficulty = "Medium";
            else if (currentDifficulty.equals("Medium")) currentDifficulty = "Easy";
        }

        // Save response to DB
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO student_response (student_id, question_id, selected_option, is_correct, marks_awarded, timestamp,sessionId) VALUES (?, ?, ?, ?, ?, ?,?)"
            );
            pst.setInt(1, studentId);
            pst.setInt(2, currentQuestion.id);
            pst.setString(3, selectedOption);
            pst.setBoolean(4, isCorrect);
            pst.setInt(5, marksAwarded);
            pst.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            pst.setInt(7,sessionId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        questionsAnswered++;
        if (questionsAnswered >= MAX_QUESTIONS) {
            finishExam();
        } else {
            loadNextQuestionBasedOnDifficulty();
        }
    }
    private void loadNextQuestionBasedOnDifficulty() {
        if (questionsAnswered >= MAX_QUESTIONS) {
            finishExam();
            return;
        }

        ArrayList<Question> difficultyQuestions = difficultyMap.getOrDefault(currentDifficulty, new ArrayList<>());

        for (Question q : difficultyQuestions) {
            if (!askedQuestionIds.contains(q.id)) {
                currentQuestion = q;
                askedQuestionIds.add(q.id); // ✅ Prevent repetition
                showQuestion(q);            // ✅ Now show it
                return;
            }
        }

        // Fallback: search across all difficulties
        for (ArrayList<Question> pool : difficultyMap.values()) {
            for (Question q : pool) {
                if (!askedQuestionIds.contains(q.id)) {
                    currentQuestion = q;
                    askedQuestionIds.add(q.id); // ✅ Prevent repetition
                    showQuestion(q);
                    return;
                }
            }
        }

        finishExam();
    }
    private String getOptionLetter(int index) {
        return switch (index) {
            case 0 -> "a";
            case 1 -> "b";
            case 2 -> "c";
            case 3 -> "d";
            default -> null;
        };
    }
    private void startExamSession() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO exam_sessions (student_id, start_time) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            pst.setInt(1, studentId);
            pst.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                sessionId = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void finishExam() {
        if (examFinished) return; // Prevent double trigger
        examFinished = true;
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "UPDATE exam_sessions SET end_time = ?, total_score = ? WHERE session_id = ?"
            );
            pst.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pst.setInt(2, totalScore);
            pst.setInt(3, sessionId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(new JLabel("✅ Exam Finished! Total Score: " + totalScore));
        JButton SummaryButton = new JButton("Summary");
        JButton reviewButton = new JButton("Review the Test");
        panel.add(reviewButton);
        JDialog dialog = new JDialog(this, "Exam Summary", true);
        dialog.setLayout(new FlowLayout());
        dialog.add(panel);
        dialog.add(SummaryButton);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        SummaryButton.addActionListener(e -> {
            dialog.dispose();
            dispose();
            new SummaryUI(studentId,sessionId);
        });

        reviewButton.addActionListener(e -> {
            dialog.dispose();
            dispose();
            new reviewFrame(new ArrayList<>(questionHistory),studentId,sessionId);
        });
        dialog.setVisible(true);
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // Do nothing when focus is regained
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!examFinished) {
                    System.out.println(" Window lost focus detected!");
                    logCheatingEvent("Tab Switching", "User switched window or minimized during the exam.");
                } else {
                    System.out.println("✔ Focus lost after exam finished — no logging.");
                }
            }
        });
    }
    private void logCheatingEvent(String cheatType, String description) {
        System.out.println("logCheatingEvent triggered!");
   
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("Debug: sessionId=" + sessionId + ", studentId=" + studentId);


            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO cheating_logs (session_id, student_id, cheat_type, description) VALUES (?, ?, ?, ?)"
            );
            pst.setInt(1, sessionId);
            pst.setInt(2, studentId);
            pst.setString(3, cheatType);
            pst.setString(4, description);
   
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(" Cheating event logged successfully:");
                System.out.println("    Student ID: " + studentId);
                System.out.println("    Session ID: " + sessionId);
                System.out.println("    Cheat Type: " + cheatType);
                System.out.println("    Description: " + description);
            } else {
                System.out.println(" No rows inserted. Something went wrong.");
            }
        } catch (Exception ex) {
            System.out.println(" Error logging cheating event:");
            ex.printStackTrace();
        }
    }

    private Timer currentTimer;
    private void startTimer(int seconds) {
        timeLeft = seconds;
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        currentTimer = new Timer();
        currentTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                timeLeft--;
                SwingUtilities.invokeLater(() -> timerLabel.setText("Time Left: " + timeLeft + "s"));
                if (timeLeft <= 0) {
                    currentTimer.cancel();
                    recordAnswer();
                }
            }
        }, 1000, 1000);
    }
    class QuestionState {
        Question question;
        String selectedOption;
        int scoreBefore;
        String difficultyBefore;
        public QuestionState(Question question, String selectedOption, int scoreBefore, String difficultyBefore) {
            this.question = question;
            this.selectedOption = selectedOption;
            this.scoreBefore = scoreBefore;
            this.difficultyBefore = difficultyBefore;
        }
    }
    class Question {
        int id;
        String questionText;
        String[] options;
        String correctOption;
        int marks;
        String difficulty;
        public Question(int id, String questionText, String[] options, String correctOption, int marks, String difficulty) {
            this.id = id;
            this.questionText = questionText;
            this.options = options;
            this.correctOption = correctOption;
            this.marks = marks;
            this.difficulty = difficulty;
        }
    }


    @Override
    public void windowGainedFocus(WindowEvent e) {
        // Optional
    }
   
    @Override
    public void windowLostFocus(WindowEvent e) {
        System.out.println("😈 Window lost focus detected!");
        logCheatingEvent("WindowSwitch", "Student switched windows during the exam.");
    }
   
    @Override
    public void windowOpened(WindowEvent e) {}
   
    @Override
    public void windowClosing(WindowEvent e) {}
   
    @Override
    public void windowClosed(WindowEvent e) {}
   
    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("🕵️‍♂️ Window minimized!");
        logCheatingEvent("WindowMinimized", "Student minimized the exam window.");
    }
   
    @Override
    public void windowDeiconified(WindowEvent e) {}
   
    @Override    
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (!examFinished) {
            System.out.println("🚨 Window deactivated!");
            logCheatingEvent("WindowDeactivated", "Student switched to another window.");
        }
    }
 // Method to activate power-up
    public void activatePowerUp() {
        isPowerUpActive = true;
        System.out.println("Power-Up Activated!");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExamFrame(1));
    }
}








