package gui;

import grabber.AutoNovel;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class getChapterNumber extends JDialog {
    private static getChapterNumber dialog;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private static DefaultListModel<String> chapterListModel;
    private JScrollPane chapterListScrollPane;
    private JList<String> chapterList;
    private JButton removeChapter;
    private JButton addChapter;

    private getChapterNumber(GUI gui, AutoNovel novel) {
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK(gui, novel));

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        removeChapter.addActionListener(arg0 -> {
            int selectedIndex = chapterList.getSelectedIndex();
            if (selectedIndex != -1) {
                chapterListModel.remove(selectedIndex);
            }
        });
        addChapter.addActionListener(arg0 -> {
            String chapterName = JOptionPane.showInputDialog(this,
                    "Enter chapter name:", "Add a new chapter", JOptionPane.PLAIN_MESSAGE);
            String chapterLink = JOptionPane.showInputDialog(this,
                    "Enter chapter URL:", "Add a new chapter", JOptionPane.PLAIN_MESSAGE);
            if (!(chapterName == null) && !(chapterLink == null)) {
                if (!chapterName.isEmpty() && !chapterLink.isEmpty()) {
                    chapterListModel.addElement(chapterName + "   |   " + chapterLink);
                }
            }
        });
    }


    static void main(GUI gui, AutoNovel novel) {
        chapterListModel = new DefaultListModel<>();
        for (int i = 0; i < novel.chapterLinks.size(); i++) {
            chapterListModel.addElement(novel.chaptersNames.get(i) + "   |   " + novel.chapterLinks.get(i));
        }

        dialog = new getChapterNumber(gui, novel);
        dialog.setTitle("Edit chapter order");
        dialog.pack();
        dialog.setVisible(true);
    }

    private void onOK(GUI gui, AutoNovel novel) {
        List<String> newNames = new ArrayList<>();
        List<String> newLinks = new ArrayList<>();
        String[] substring;
        for (int i = 0; i < chapterListModel.size(); i++) {
            substring = chapterListModel.get(i).split("   \\|   ");
            newNames.add(substring[0]);
            newLinks.add(substring[1]);
        }
        novel.chaptersNames = newNames;
        novel.chapterLinks = newLinks;
        if (!novel.chapterLinks.isEmpty()) {
            gui.autoChapterAmount.setText(String.valueOf(novel.chapterLinks.size()));
        }
        dialog.dispose();
    }

    private void onCancel() {
        dialog.dispose();
    }

    private void createUIComponents() {
        addChapter = new JButton("Add");
        removeChapter = new JButton("Remove");

        chapterList = new JList<>(chapterListModel);
        chapterList.setDragEnabled(true);
        chapterList.setDropMode(DropMode.INSERT);
        chapterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chapterList.setTransferHandler(new TransferHandler() {
            private int index;
            private boolean beforeIndex = false; //Start with `false` therefore if it is removed from or added to the list it still works

            @Override
            public int getSourceActions(JComponent comp) {
                return MOVE;
            }

            @Override
            public Transferable createTransferable(JComponent comp) {
                index = chapterList.getSelectedIndex();
                return new StringSelection(chapterList.getSelectedValue());
            }

            @Override
            public void exportDone(JComponent comp, Transferable trans, int action) {
                if (action == MOVE) {
                    if (beforeIndex)
                        chapterListModel.remove(index + 1);
                    else
                        chapterListModel.remove(index);
                }
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                try {
                    String s = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                    chapterListModel.add(dl.getIndex(), s);
                    beforeIndex = dl.getIndex() < index;
                    return true;
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
        chapterListScrollPane = new JScrollPane(chapterList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}