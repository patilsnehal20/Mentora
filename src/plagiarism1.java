package Buffer;


import java.sql.*;
import java.util.*;
//import javax.swing.*;


public class plagarism {


    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/examdb", "root", "Ssu@2005");
    }


    public Map<String, Integer> checkPlagiarismBetweenStudents() {
        Map<String, Integer> plagiarismMap = new LinkedHashMap<>();


        try (Connection conn = connect()) {
            Map<Integer, List<String>> studentAnswers = new HashMap<>();


            // Get answers for each student
            String query = "SELECT student_id, answer FROM subjective_answers";
            ResultSet rs = conn.createStatement().executeQuery(query);


            while (rs.next()) {
                int sid = rs.getInt("student_id");
                String answer = rs.getString("answer");


                studentAnswers.computeIfAbsent(sid, _ -> new ArrayList<>()).add(answer);
            }


            // Compare each pair
            List<Integer> ids = new ArrayList<>(studentAnswers.keySet());
            for (int i = 0; i < ids.size(); i++) {
                for (int j = i + 1; j < ids.size(); j++) {
                    int id1 = ids.get(i);
                    int id2 = ids.get(j);


                    int totalMatch = 0;
                    int count = 0;


                    List<String> ans1 = studentAnswers.get(id1);
                    List<String> ans2 = studentAnswers.get(id2);


                    for (int k = 0; k < Math.min(ans1.size(), ans2.size()); k++) {
                        int match = calculateMatchPercentage(ans1.get(k), ans2.get(k));
                        totalMatch += match;
                        count++;
                    }


                    int average = (count > 0) ? totalMatch / count : 0;
                    String pair = "Student " + id1 + " vs Student " + id2;
                    plagiarismMap.put(pair, average);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return plagiarismMap;
    }


    private int calculateMatchPercentage(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) return 0;


        String[] words1 = a.toLowerCase().split("\\s+");
        String[] words2 = b.toLowerCase().split("\\s+");


        int match = 0;
        for (String w1 : words1) {
            for (String w2 : words2) {
                if (w1.equals(w2)) {
                    match++;
                    break;
                }
            }
        }


        return (int) ((match * 2.0 / (words1.length + words2.length)) * 100);
    }
}



