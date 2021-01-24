package gui;

import grabber.Chapter;
import grabber.Novel;
import system.init;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class autoChapterOrder extends JDialog {
    private static autoChapterOrder dialog;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    static DefaultListModel<Chapter> chapterListModel;
    private JScrollPane chapterListScrollPane;
    private JList<Chapter> GUIChapterList;
    private JButton removeChapter;
    private JButton editChapterListBtn;
    private JButton invertButton;
    private Novel novel;


    private autoChapterOrder(Novel novel) {
        this.novel = novel;
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Remove chapters
        removeChapter.addActionListener(arg0 -> {
            int[] indices = GUIChapterList.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                chapterListModel.removeElementAt(indices[i]);
            }
        });
        editChapterListBtn.addActionListener(actionEvent -> editChapterList.main("auto"));
        invertButton.addActionListener(actionEvent -> {
            List<Chapter> tempList = new ArrayList<>();
            for (int i = 0; i < chapterListModel.size(); i++) {
                tempList.add(chapterListModel.get(i));
            }
            Collections.reverse(tempList);
            chapterListModel.removeAllElements();
            for (Chapter chapter: tempList) {
                chapterListModel.addElement(chapter);
            }
        });
    }

    static void main(Novel novel) {
        dialog = new autoChapterOrder(novel);
        dialog.setTitle("Edit chapter order");
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void onOK() {
        List<Chapter> newChapters = new ArrayList<>();
        for (int i = 0; i < chapterListModel.size(); i++) {
            newChapters.add(chapterListModel.get(i));
        }
        novel.chapterList = newChapters;
        // Update chapter counter label
        init.gui.autoChapterAmount.setText(String.valueOf(newChapters.size()));
        dialog.dispose();
    }

    private void onCancel() {
        dialog.dispose();
    }

    private void createUIComponents() {
        editChapterListBtn = new JButton("Add");
        removeChapter = new JButton("Remove");

        chapterListModel = new DefaultListModel<>();
        for (Chapter chapter: novel.chapterList) {
            chapterListModel.addElement(chapter);
        }

        GUIChapterList = new JList<>(chapterListModel);
        GUIChapterList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        GUIChapterList.setDropMode(DropMode.INSERT);
        GUIChapterList.setDragEnabled(true);
        GUIChapterList.setTransferHandler(new ListItemTransferHandler());
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Chapter selectedChapter = GUIChapterList.getSelectedValue();
                    chapterListScrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    selectedChapter.saveChapter(novel);
                    chapterListScrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    chapterPreview.main(selectedChapter);
                }
            }
        };
        GUIChapterList.addMouseListener(mouseListener);
        chapterListScrollPane = new JScrollPane(GUIChapterList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
