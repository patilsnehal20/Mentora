package com.academic.examapp.questiongenerator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.academic.examapp.db.DBConnection;
import com.academic.examapp.model.Question;

public class QuestionManager {

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questions = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions_pract");

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
                questions.add(q);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }
}


