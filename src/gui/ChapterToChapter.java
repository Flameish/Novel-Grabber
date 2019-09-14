package gui;

import javax.swing.*;
import java.awt.event.*;

public class ChapterToChapter extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField firstChapterField;
    private JTextField lastChapterField;
    private JTextField nextChapterButtonField;

    public ChapterToChapter() {
        setContentPane(contentPane);
        setModal(true);
        setTitle("Chapter to chapter navigation");
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void main() {
        ChapterToChapter dialog = new ChapterToChapter();
        dialog.pack();
        dialog.setVisible(true);
    }

    private void onOK() {
        if ((!firstChapterField.getText().isEmpty() && firstChapterField.getText() != null) &&
                (!lastChapterField.getText().isEmpty() && lastChapterField.getText() != null) &&
                (!nextChapterButtonField.getText().isEmpty() && nextChapterButtonField.getText() != null)) {
            GUI.chapterToChapterArgs[0] = firstChapterField.getText();
            GUI.chapterToChapterArgs[1] = lastChapterField.getText();
            GUI.chapterToChapterArgs[2] = nextChapterButtonField.getText();
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        firstChapterField = new JTextField(GUI.chapterToChapterArgs[0]);
        lastChapterField = new JTextField(GUI.chapterToChapterArgs[1]);
        nextChapterButtonField = new JTextField(GUI.chapterToChapterArgs[2]);
    }
}
