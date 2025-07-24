package Buffer;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Exam App Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setSize(screenSize);
            frame.setLocationRelativeTo(null);


            JTabbedPane tabs = new JTabbedPane();

            StudentPlagiarismUI dbCompareUI = new StudentPlagiarismUI();
            tabs.addTab("Student Plagiarism (DB)", dbCompareUI.getPanel());


            frame.add(tabs);
            frame.setVisible(true);
        });
    }
}



