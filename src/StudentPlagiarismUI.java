package Buffer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PlagiarismComparisonUI {


    private JPanel mainPanel;


    public PlagiarismComparisonUI() {
        mainPanel = new JPanel(new BorderLayout());


        JLabel titleLabel = new JLabel("Plagiarism Checker", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));


        JTextArea textArea1 = new JTextArea();
        JTextArea textArea2 = new JTextArea();


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(textArea1), new JScrollPane(textArea2));
        splitPane.setDividerLocation(500);


        JButton compareButton = new JButton("Compare");
        JLabel resultLabel = new JLabel("Match Percentage: ", JLabel.CENTER);


        compareButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text1 = textArea1.getText();
                String text2 = textArea2.getText();
                int match = calculateMatchPercentage(text1, text2);
                resultLabel.setText("Match Percentage: " + match + "%");
            }
        });


        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(compareButton, BorderLayout.CENTER);
        bottomPanel.add(resultLabel, BorderLayout.SOUTH);


        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }


    // Method to return the main panel to Main.java
    public JPanel getPanel() {
        return mainPanel;
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


        int total = Math.max(words1.length, words2.length);
        return total == 0 ? 0 : (match * 100 / total);
    }
}
