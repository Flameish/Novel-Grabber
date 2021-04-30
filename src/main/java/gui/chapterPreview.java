package gui;

import grabber.Chapter;
import grabber.GrabberUtils;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;

public class chapterPreview extends JDialog {
    private JPanel contentPane;
    private JEditorPane chapterContentPane;
    private JTextField urlField;
    private JTextField nameField;
    private JButton closeButton;
    private JButton saveButton;
    private JButton visitButton;
    private Chapter chapter;

    public chapterPreview(Chapter chapter) {
        this.chapter = chapter;
        setContentPane(contentPane);
        setTitle(chapter.name);
        setModal(true);

        saveButton.addActionListener(e -> {
            chapter.name = nameField.getText();
            chapter.chapterURL = urlField.getText();
            chapter.chapterContent = chapterContentPane.getText();
            dispose();
        });
        closeButton.addActionListener(e -> dispose());
        visitButton.addActionListener(e -> {
            try {
                GrabberUtils.openWebpage(new URI(urlField.getText()));
            } catch (URISyntaxException ex) {
                GrabberUtils.err(ex.getMessage(), ex);
            }
        });
    }

    public static void main(Chapter chapter) {
        chapterPreview dialog = new chapterPreview(chapter);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        chapterContentPane = new JEditorPane("text/html", chapter.chapterContent);
        chapterContentPane.setCaretPosition(0);
        nameField = new JTextField(chapter.name);
        urlField = new JTextField(chapter.chapterURL);
    }
}
