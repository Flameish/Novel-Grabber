package gui;

import grabber.Chapter;
import grabber.Novel;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class autoChapterOrder extends JDialog {
    private static autoChapterOrder dialog;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private static DefaultListModel<Chapter> chapterListModel;
    private JScrollPane chapterListScrollPane;
    private JList<Chapter> chapterList;
    private JButton removeChapter;
    private JButton addChapter;
    private Novel novel;


    private autoChapterOrder(Novel novel) {
        this.novel = novel;
        ImageIcon favicon = new ImageIcon(getClass().getResource("/files/images/favicon.png"));
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

        // Remove chapter
        removeChapter.addActionListener(arg0 -> {
            int[] indices = chapterList.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                chapterListModel.removeElementAt(indices[i]);
            }
        });

        // Add new chapter
        addChapter.addActionListener(arg0 -> {
            String chapterName = JOptionPane.showInputDialog(this,
                    "Enter chapter name:", "Add a new chapter", JOptionPane.PLAIN_MESSAGE);
            String chapterLink = JOptionPane.showInputDialog(this,
                    "Enter chapter URL:", "Add a new chapter", JOptionPane.PLAIN_MESSAGE);
            if (!(chapterName == null) && !(chapterLink == null)) {
                if (!chapterName.isEmpty() && !chapterLink.isEmpty()) {
                    chapterListModel.addElement(new Chapter(chapterName, chapterLink));
                }
            }
        });
    }


    static void main(Novel novel) {
        dialog = new autoChapterOrder(novel);
        dialog.setTitle("Edit chapter order");
        dialog.pack();
        dialog.setVisible(true);
    }

    private void onOK() {
        List<Chapter> newChapters = new ArrayList<>();
        for (int i = 0; i < chapterListModel.size(); i++) {
            newChapters.add(chapterListModel.get(i));
        }
        novel.gui.autoChapterAmount.setText(String.valueOf(newChapters.size()));
        novel.chapters = newChapters;
        dialog.dispose();
    }

    private void onCancel() {
        dialog.dispose();
    }

    private void createUIComponents() {
        addChapter = new JButton("Add");
        removeChapter = new JButton("Remove");

        chapterListModel = new DefaultListModel<>();
        for (Chapter chapter: novel.chapters) {
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
