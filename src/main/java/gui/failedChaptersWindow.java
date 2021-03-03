package gui;

import grabber.Chapter;
import grabber.formats.EPUB;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.formats.Text;
import system.Config;
import system.init;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Executors;

public class failedChaptersWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOutput;
    private JButton buttonRetry;
    static DefaultListModel<Chapter> chapterListModel;
    private JScrollPane chapterListScrollPane;
    private JList GUIChapterList;
    private JButton buttonClose;
    private Novel novel;

    public failedChaptersWindow(Novel novel) {
        this.novel = novel;
        setTitle("Download finished with failed chapters");
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getRootPane().setDefaultButton(buttonOutput);

        buttonOutput.addActionListener(e -> {
            // EPUB
            if(Config.getInstance().getOutputFormat() == 0) {
                EPUB book = new EPUB(novel);
                book.write();
            }
            // Text
            if(Config.getInstance().getOutputFormat() == 1) {
                Text book = new Text(novel);
                book.write();
            }
            dispose();
        });
        buttonRetry.addActionListener(e -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    if(novel.window.equals("auto")) {
                        init.gui.autoDownloadInProgress(true);
                    }
                    if(novel.window.equals("manual")) {
                        init.gui.manDownloadInProgress(true);
                    }
                    novel.retry();
                } catch (Exception exception) {
                    GrabberUtils.err(novel.window, exception.getMessage());
                }
                if(novel.window.equals("auto")) {
                    init.gui.autoDownloadInProgress(false);
                }
                if(novel.window.equals("manual")) {
                    init.gui.manDownloadInProgress(false);
                }
            });
            dispose();
        });
        buttonClose.addActionListener(e -> dispose());
    }

    public static void main(Novel novel) {
        failedChaptersWindow dialog = new failedChaptersWindow(novel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        chapterListModel = new DefaultListModel<>();
        for (Chapter chapter: novel.failedChapters) {
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
                    Chapter selectedChapter = (Chapter) GUIChapterList.getSelectedValue();
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
