package gui;

import javax.swing.*;

public class chapterPreview extends JDialog {
    private JPanel contentPane;
    private JEditorPane chapterContentPane;

    public chapterPreview() {
        setContentPane(contentPane);
        setModal(true);
    }

    public static void main(String chapterContent) {
        chapterPreview dialog = new chapterPreview();
        dialog.chapterContentPane.setText(chapterContent);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }
}
