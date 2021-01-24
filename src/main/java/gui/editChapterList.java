package gui;

import grabber.Chapter;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class editChapterList extends JDialog {
    private JPanel contentPane;
    private JLabel chapterNamesAreaLbl;
    private JLabel chapterLinksAreaLbl;
    private JButton buttonSave;
    private JButton buttonClose;
    private JTextArea chapterNamesArea;
    private JTextArea chapterLinksArea;
    private String window;

    public editChapterList(String window) {
        this.window = window;
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonSave);
        setModal(true);

        buttonSave.addActionListener(actionEvent -> {
            // Remove empty lines from textAreas
            chapterNamesArea.setText(chapterNamesArea.getText().replaceAll("(?m)^\\s+$", ""));
            chapterLinksArea.setText(chapterLinksArea.getText().replaceAll("(?m)^\\s+$", ""));
            List<String> chapterNames = new ArrayList<>(Arrays.asList(chapterNamesArea.getText().split("\\n")));
            List<String> chapterLinks = new ArrayList<>(Arrays.asList(chapterLinksArea.getText().split("\\n")));
            if(chapterNames.size() == chapterLinks.size()) {
                if(window.equals("auto")) {
                    autoChapterOrder.chapterListModel.clear();
                    for(int i = 0; i < chapterNames.size(); i++) {
                        autoChapterOrder.chapterListModel.addElement(new Chapter(chapterNames.get(i), chapterLinks.get(i)));
                    }
                }
                else {
                    GUI.manLinkListModel.clear();
                    for(int i = 0; i < chapterNames.size(); i++) {
                        GUI.manLinkListModel.addElement(new Chapter(chapterNames.get(i), chapterLinks.get(i)));
                    }
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(contentPane, "Lists are not the same length.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonClose.addActionListener(actionEvent -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String window) {
        editChapterList dialog = new editChapterList(window);
        dialog.setTitle("Edit chapter list");
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        chapterNamesArea = new JTextArea();
        chapterLinksArea = new JTextArea();

        if(window.equals("auto")) {
            for(int i = 0; i < autoChapterOrder.chapterListModel.size(); i++) {
                chapterNamesArea.append(autoChapterOrder.chapterListModel.get(i).name + "\n");
                chapterLinksArea.append(autoChapterOrder.chapterListModel.get(i).chapterURL + "\n");
            }
        } else {
            for(int i = 0; i < GUI.manLinkListModel.size(); i++) {
                chapterNamesArea.append(GUI.manLinkListModel.get(i).name + "\n");
                chapterLinksArea.append(GUI.manLinkListModel.get(i).chapterURL + "\n");
            }
        }
    }
}
