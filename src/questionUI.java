package com.academic.examapp.questiongenerator;

import javax.swing.*;

import com.academic.examapp.main.Buffer1;
import com.academic.examapp.model.Question;
import com.academic.examapp.uimodule.StudentDashboard;

import java.awt.*;
import java.util.*;

public class QuestionUI extends JFrame {
    private JTextArea questionArea;
    private JTextArea answerArea;
    private JButton loadButton;
    private JComboBox<String> topicBox;
    private JComboBox<String> subTopicBox;
    private JComboBox<String> difficultyBox;
    private JButton filterButton;
    private JButton resetFiltersButton;
    private JTextField studentIdField;
    private JButton suggestButton;
    private JButton showAnswerButton;
    private JButton logoutButton;
    private JButton BackButton;
    private int studentId;
    private String studentName;
    private Question currentQuestion = null;
    private Set<Integer> askedQuestionIds = new HashSet<>();
    private ArrayList<Question> lastFilteredQuestions = new ArrayList<>();

    private final Map<String, String[]> topicSubTopicMap = new HashMap<>() {{
        put("Operating Systems", new String[]{"Process Scheduling", "Memory Management", "File Systems"});
        put("Computer Networks", new String[]{"TCP/IP", "Routing Protocols", "DNS", "FTP", "HTTP"});
        put("DBMS", new String[]{"SQL Queries", "Joins", "Transactions (ACID)", "Normalization"});
        put("Computer Organization", new String[]{"Number Systems", "Instruction Cycles", "Memory Hierarchy"});
    }};

    public QuestionUI(int studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        setTitle("Smart Question Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Top Panel ---
        JLabel title = new JLabel("Smart Question Generator", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadButton = new JButton("Load All Questions");
        loadButton.setFont(new Font("Arial", Font.PLAIN, 16));
        loadButton.addActionListener(e -> loadQuestions());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(loadButton, BorderLayout.SOUTH);

        JPanel northWrapper = new JPanel();
        northWrapper.setLayout(new BoxLayout(northWrapper, BoxLayout.Y_AXIS));
        northWrapper.add(topPanel);

        // --- Filter Panel ---
        JPanel filterPanel = new JPanel(new FlowLayout());

        topicBox = new JComboBox<>();
        topicBox.addItem("--");
        for (String topic : topicSubTopicMap.keySet()) {
            topicBox.addItem(topic);
        }

        subTopicBox = new JComboBox<>();
        subTopicBox.addItem("--");

        difficultyBox = new JComboBox<>(new String[]{"--", "1", "2", "3"});
        filterButton = new JButton("\uD83D\uDD0D Filter");
        resetFiltersButton = new JButton("Reset Filters");
        suggestButton = new JButton("Suggest");
        showAnswerButton = new JButton("Show Answer");

        // ✅ Initialize these buttons BEFORE adding
        logoutButton = new JButton("Logout");
        BackButton = new JButton("Back");

        // Add components to filter panel
        filterPanel.add(new JLabel("Topic:"));
        filterPanel.add(topicBox);
        filterPanel.add(new JLabel("Sub-topic:"));
        filterPanel.add(subTopicBox);
        filterPanel.add(new JLabel("Difficulty:"));
        filterPanel.add(difficultyBox);
        filterPanel.add(filterButton);
        filterPanel.add(resetFiltersButton);
        filterPanel.add(suggestButton);
        filterPanel.add(showAnswerButton);
        filterPanel.add(logoutButton);
        filterPanel.add(BackButton);

        northWrapper.add(filterPanel);
        add(northWrapper, BorderLayout.NORTH);

        // --- Question Display ---
        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(questionArea), BorderLayout.CENTER);

        // --- Answer Display ---
        answerArea = new JTextArea(16, 50);
        answerArea.setEditable(false);
        answerArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        answerArea.setForeground(Color.BLUE);
        add(new JScrollPane(answerArea), BorderLayout.SOUTH);

        // --- Listeners ---
        topicBox.addActionListener(e -> {
            String selectedTopic = topicBox.getSelectedItem().toString();
            if (!selectedTopic.equals("--")) {
                updateSubTopics(selectedTopic);
            } else {
                subTopicBox.removeAllItems();
                subTopicBox.addItem("--");
            }
        });

        logoutButton.addActionListener(e -> {
            dispose();
            JOptionPane.showMessageDialog(null, "You have been logged out.");
            Buffer1.main(new String[]{});
        });

        BackButton.addActionListener(e -> {
            dispose();
            new StudentDashboard(studentId, studentName);
        });

        filterButton.addActionListener(e -> updateFilteredQuestions());

        showAnswerButton.addActionListener(e -> {
            if (lastFilteredQuestions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No filtered questions to show answers for.");
                return;
            }
            StringBuilder answerText = new StringBuilder();
            int count = 1;
            for (Question q : lastFilteredQuestions) {
                answerText.append("Q").append(count++).append(": ").append(q.getQuestionText()).append("\n");
                if ("Subjective".equalsIgnoreCase(q.getQuestionType())) {
                    answerText.append("(Subjective Question)\n");
                    answerText.append("Answer: ").append(q.getAnswer()).append("\n");
                } else {
                    answerText.append("Correct Option: ").append(q.getCorrectOption()).append("\n");
                }
                answerText.append("--------------------------------------------------------\n\n");
            }
            answerArea.setText(answerText.toString());
        });

        suggestButton.addActionListener(e -> {
            String topic = topicBox.getSelectedItem().toString();
            String subTopic = subTopicBox.getSelectedItem().toString();
            SmartQuestionSelector selector = new SmartQuestionSelector();
            ArrayList<Question> suggested = selector.getSuggestionsByPerformance(String.valueOf(studentId), topic, subTopic);
            questionArea.setText("");
            for (Question q : suggested) {
                questionArea.append("Q" + q.getQuestionId() + " [" + q.getMarks() + " Marks]\n");
                questionArea.append(q.getQuestionText() + "\n");
                if (!q.getQuestionType().equalsIgnoreCase("Subjective")) {
                    questionArea.append("A) " + q.getOptionA() + "\n");
                    questionArea.append("B) " + q.getOptionB() + "\n");
                    questionArea.append("C) " + q.getOptionC() + "\n");
                    questionArea.append("D) " + q.getOptionD() + "\n");
                } else {
                    questionArea.append("(Subjective Question)\n");
                }
                questionArea.append("--------------------------------------------------------\n\n");
            }
        });

        resetFiltersButton.addActionListener(e -> {
            topicBox.setSelectedIndex(0);
            subTopicBox.removeAllItems();
            subTopicBox.addItem("--");
            difficultyBox.setSelectedIndex(0);
            questionArea.setText("");
            answerArea.setText("");
            lastFilteredQuestions.clear();
        });

        updateSubTopics(topicBox.getItemAt(0));
        setVisible(true);
    }

    private void updateSubTopics(String topic) {
        subTopicBox.removeAllItems();
        subTopicBox.addItem("--");
        String[] subTopics = topicSubTopicMap.getOrDefault(topic, new String[0]);
        for (String sub : subTopics) {
            subTopicBox.addItem(sub);
        }
    }

    private void loadQuestions() {
        QuestionManager qm = new QuestionManager();
        ArrayList<Question> questions = qm.getAllQuestions();
        lastFilteredQuestions.clear();
        lastFilteredQuestions.addAll(questions);

        StringBuilder output = new StringBuilder();
        for (Question q : questions) {
            output.append("Topic: ").append(q.getTopic()).append(" | Sub-topic: ").append(q.getSubTopic()).append("\n");
            output.append("Difficulty: ").append(q.getDifficultyLevel()).append(" | Marks: ").append(q.getMarks()).append("\n");
            output.append("Q").append(q.getQuestionId()).append(" [").append(q.getMarks()).append(" Marks]\n");
            output.append(q.getQuestionText()).append("\n");

            if ("Subjective".equalsIgnoreCase(q.getQuestionType())) {
                output.append("(Subjective Question)\n");
            } else {
                if (q.getOptionA() != null) output.append("A) ").append(q.getOptionA()).append("\n");
                if (q.getOptionB() != null) output.append("B) ").append(q.getOptionB()).append("\n");
                if (q.getOptionC() != null) output.append("C) ").append(q.getOptionC()).append("\n");
                if (q.getOptionD() != null) output.append("D) ").append(q.getOptionD()).append("\n");
            }

            output.append("--------------------------------------------------------\n\n");
        }
        questionArea.setText(output.toString());
    }

    private void updateFilteredQuestions() {
        if (topicBox.getSelectedItem() == null || subTopicBox.getSelectedItem() == null || difficultyBox.getSelectedItem() == null) return;

        String topic = topicBox.getSelectedItem().toString();
        String subTopic = subTopicBox.getSelectedItem().toString();
        String difficultyText = difficultyBox.getSelectedItem().toString();

        if (topic.equals("--") && subTopic.equals("--") && difficultyText.equals("--")) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select at least one filter: Topic, Sub-topic or Difficulty level.");
            return;
        }

        Integer difficulty = difficultyText.equals("--") ? null : Integer.parseInt(difficultyText);

        SmartQuestionSelector selector = new SmartQuestionSelector();
        ArrayList<Question> filtered = selector.getFilteredQuestionsFlexible(topic, subTopic, difficulty);
        questionArea.setText("");
        lastFilteredQuestions.clear();
        lastFilteredQuestions.addAll(filtered);

        int count = 1;
        for (Question q : filtered) {
            questionArea.append("Q" + count++ + ": [" + q.getMarks() + " Marks]\n");
            questionArea.append(q.getQuestionText() + "\n");

            if ("Subjective".equalsIgnoreCase(q.getQuestionType())) {
                questionArea.append("(Subjective Question)\n");
            } else {
                if (q.getOptionA() != null) questionArea.append("A) " + q.getOptionA() + "\n");
                if (q.getOptionB() != null) questionArea.append("B) " + q.getOptionB() + "\n");
                if (q.getOptionC() != null) questionArea.append("C) " + q.getOptionC() + "\n");
                if (q.getOptionD() != null) questionArea.append("D) " + q.getOptionD() + "\n");
            }

            questionArea.append("--------------------------------------------------------\n\n");
        }

        currentQuestion = !filtered.isEmpty() ? filtered.get(0) : null;
    }
}
