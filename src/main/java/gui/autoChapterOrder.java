package gui;

import grabber.Chapter;
import grabber.Novel;
import system.init;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class autoChapterOrder extends JDialog {
    private static autoChapterOrder dialog;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    static DefaultListModel<Chapter> chapterListModel;
    private JScrollPane chapterListScrollPane;
    private JList<Chapter> chapterList;
    private JButton removeChapter;
    private JButton editChapterListBtn;
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
            int[] indices = chapterList.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                chapterListModel.removeElementAt(indices[i]);
            }
        });
        editChapterListBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editChapterList.main("auto");
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

        chapterList = new JList<>(chapterListModel);
        chapterList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        chapterList.setDropMode(DropMode.INSERT);
        chapterList.setDragEnabled(true);
        chapterList.setTransferHandler(new ListItemTransferHandler());
        chapterListScrollPane = new JScrollPane(chapterList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
