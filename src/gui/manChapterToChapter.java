package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class manChapterToChapter extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField firstChapterField;
    private JTextField lastChapterField;
    private JTextField nextChapterButtonField;
    private JTextField manChapterToChapterNumberField;

    public manChapterToChapter() {
        setContentPane(contentPane);
        setModal(true);
        setTitle("Chapter to chapter navigation");
        ImageIcon favicon = new ImageIcon(getClass().getResource("/files/images/favicon.png"));
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
        manChapterToChapterNumberField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (manChapterToChapterNumberField.getText().equals("Number")) {
                    manChapterToChapterNumberField.setText("");
                    manChapterToChapterNumberField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (manChapterToChapterNumberField.getText().isEmpty()) {
                    manChapterToChapterNumberField.setForeground(Color.GRAY);
                    manChapterToChapterNumberField.setText("Number");
                }
            }
        });
    }

    public static void main() {
        manChapterToChapter dialog = new manChapterToChapter();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void onOK() {
        if ((!firstChapterField.getText().isEmpty() && firstChapterField.getText() != null) &&
                (!lastChapterField.getText().isEmpty() && lastChapterField.getText() != null) &&
                (!nextChapterButtonField.getText().isEmpty() && nextChapterButtonField.getText() != null)) {
            GUI.chapterToChapterArgs[0] = firstChapterField.getText();
            GUI.chapterToChapterArgs[1] = lastChapterField.getText();
            GUI.chapterToChapterArgs[2] = nextChapterButtonField.getText();
            if (!manChapterToChapterNumberField.getText().equals("Number")) {
                GUI.chapterToChapterNumber = Integer.valueOf(manChapterToChapterNumberField.getText());
            } else {
                GUI.chapterToChapterNumber = 1;
            }
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        manChapterToChapterNumberField = new JTextField("#");
        manChapterToChapterNumberField.setForeground(Color.GRAY);

        firstChapterField = new JTextField(GUI.chapterToChapterArgs[0]);
        lastChapterField = new JTextField(GUI.chapterToChapterArgs[1]);
        nextChapterButtonField = new JTextField(GUI.chapterToChapterArgs[2]);
        if (GUI.chapterToChapterNumber != 1) {
            manChapterToChapterNumberField.setText(String.valueOf(GUI.chapterToChapterNumber));
            manChapterToChapterNumberField.setForeground(Color.BLACK);
        }
    }
}
