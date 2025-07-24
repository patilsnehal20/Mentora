package com.academic.examapp.questiongenerator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import com.academic.examapp.db.DBConnection;
import com.academic.examapp.model.Question;

public class SmartQuestionSelector {

    // Method to fetch and print all questions
    public void fetchAndPrintQuestions() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions_pract");

            Set<Integer> seenIds = new HashSet<>();

            while (rs.next()) {
                int id = rs.getInt("question_id");

                if (!seenIds.contains(id)) {
                    seenIds.add(id); // mark this question as seen

                    System.out.println("Question ID: " + id);
                    System.out.println("Topic: " + rs.getString("topic"));
                    System.out.println("Sub-topic: " + rs.getString("sub_topic"));
                    System.out.println("Difficulty: " + rs.getInt("difficulty_level"));
                    System.out.println("Q" + id + " [" + rs.getInt("marks") + " Marks]");
                    System.out.println(rs.getString("question_text"));

                    System.out.println("A) " + rs.getString("option_a"));
                    System.out.println("B) " + rs.getString("option_b"));
                    System.out.println("C) " + rs.getString("option_c"));
                    System.out.println("D) " + rs.getString("option_d"));
                    System.out.println("Type: " + rs.getString("question_type"));
                    System.out.println("Answer: " + rs.getString("answer"));
                    System.out.println("------------------------");
                }
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to get filtered questions using Set & HashMap
    public ArrayList<Question> getFilteredQuestions(String topic, String subTopic, int difficulty) {
        ArrayList<Question> questions = new ArrayList<>();
        Set<Integer> seenIds = new HashSet<>();
        HashMap<Integer, Question> questionMap = new HashMap<>();

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM questions_pract WHERE topic = ? AND sub_topic = ? AND difficulty_level = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, topic);
            stmt.setString(2, subTopic);
            stmt.setInt(3, difficulty);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("question_id");
                if (!seenIds.contains(id)) {
                    Question q = new Question(
                        id,
                        rs.getString("topic"),
                        rs.getString("sub_topic"),
                        rs.getInt("difficulty_level"),
                        rs.getInt("marks"),
                        rs.getString("question_text"),
                        rs.getString("correct_option"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("question_type"),
                        rs.getString("answer")
                    );

                    seenIds.add(id);
                    questions.add(q);
                    questionMap.put(id, q); // useful if you want to access by ID later
                }
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }

    // Method to get suggestions by student performance
    public ArrayList<Question> getSuggestionsByPerformance(String studentId, String topic, String subTopic) {
        ArrayList<Question> suggestions = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();

            // Debug log
            System.out.println("Getting suggestions for student: " + studentId + ", topic: " + topic + ", subTopic: " + subTopic);

            // Step 1: Fetch average score for this student and sub-topic
            String perfQuery = "SELECT AVG(score) AS avg_score FROM student_performance WHERE student_id = ? AND sub_topic = ?";
            PreparedStatement perfStmt = conn.prepareStatement(perfQuery);
            perfStmt.setString(1, studentId);
            perfStmt.setString(2, subTopic);
            ResultSet perfRs = perfStmt.executeQuery();

            int suggestedDifficulty = 2; // default difficulty
            if (perfRs.next()) {
                double avg = perfRs.getDouble("avg_score");
                System.out.println("Average score: " + avg);
                if (avg >= 80) suggestedDifficulty = 3;
                else if (avg >= 40) suggestedDifficulty = 2;
                else suggestedDifficulty = 1;
            } else {
                System.out.println("No performance record found for this student and sub-topic.");
            }

            // Step 2: Fetch questions matching suggested difficulty
            String qQuery = "SELECT * FROM questions_pract WHERE topic = ? AND sub_topic = ? AND difficulty_level = ?";
            PreparedStatement stmt = conn.prepareStatement(qQuery);
            stmt.setString(1, topic);
            stmt.setString(2, subTopic);
            stmt.setInt(3, suggestedDifficulty);
            ResultSet rs = stmt.executeQuery();

            // Step 3: Use PriorityQueue to sort by marks (higher first)
            PriorityQueue<Question> pq = new PriorityQueue<>(Comparator.comparingInt(Question::getMarks).reversed());

            while (rs.next()) {
                Question q = new Question(
                    rs.getInt("question_id"),
                    rs.getString("topic"),
                    rs.getString("sub_topic"),
                    rs.getInt("difficulty_level"),
                    rs.getInt("marks"),
                    rs.getString("question_text"),
                    rs.getString("correct_option"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("question_type"),
                    rs.getString("answer")
                );
                pq.add(q);
            }

            while (!pq.isEmpty()) {
                suggestions.add(pq.poll());
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return suggestions;
    }
    public ArrayList<Question> getFilteredQuestionsFlexible(String topic, String subTopic, Integer difficulty) {
    	QuestionManager qm = new QuestionManager();
    	ArrayList<Question> all = qm.getAllQuestions();

        ArrayList<Question> result = new ArrayList<>();

        for (Question q : all) {
            if (!topic.equals("--") && !q.getTopic().equalsIgnoreCase(topic)) continue;
            if (!subTopic.equals("--") && !q.getSubTopic().equalsIgnoreCase(subTopic)) continue;
            if (difficulty != null && q.getDifficultyLevel() != difficulty) continue;
            result.add(q);
        }

        return result;
    }

}
