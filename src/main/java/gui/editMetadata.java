package gui;

import grabber.Novel;
import system.init;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class editMetadata extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton coverBtn;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField subjectsField;
    private JScrollPane descScrollPane;
    private JTextArea descArea;
    private Novel novel;

    public editMetadata(Novel novel) {
        this.novel = novel;
        setContentPane(contentPane);
        setModal(true);
        setTitle("Edit EPUB metadata");
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        coverBtn.addComponentListener(new ComponentAdapter() {
        });

        // Edit cover image
        coverBtn.addActionListener(arg01 -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open File");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".jpg .png .gif .jpeg .svg", "png", "jpg", "gif", "jpeg", "svg");
            chooser.setFileFilter(filter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                novel.bookCover = chooser.getSelectedFile().toString();
                coverBtn.setIcon(new ImageIcon(new ImageIcon(novel.bookCover).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
            }
        });

    }

    static void main(Novel novel) {
        editMetadata dialog = new editMetadata(novel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void onOK() {
        novel.bookTitle = titleField.getText();
        novel.bookAuthor = authorField.getText();
        novel.bookDesc = descArea.getText();
        novel.bookSubjects = Arrays.asList(subjectsField.getText().split(","));
        if(novel.window.equals("auto")) {
            init.gui.autoBookTitle.setText(novel.bookTitle);
            init.gui.autoAuthor.setText(novel.bookAuthor);
            int maxNumberOfSubjects = 0;
            init.gui.autoBookSubjects.setText("<html>");
            for (String eachTag : novel.bookSubjects) {
                init.gui.autoBookSubjects.setText(init.gui.autoBookSubjects.getText() + eachTag + ", ");
                maxNumberOfSubjects++;
                if (maxNumberOfSubjects == 4) {
                    maxNumberOfSubjects = 0;
                    init.gui.autoBookSubjects.setText(init.gui.autoBookSubjects.getText() + "<br>");
                }
            }
            if (!init.gui.autoBookSubjects.getText().isEmpty()) {
                init.gui.autoBookSubjects.setText(
                        init.gui.autoBookSubjects.getText().substring(0,
                                init.gui.autoBookSubjects.getText().lastIndexOf(",")));
            }
            // Book Cover
            if (novel.bufferedCover != null) init.gui.setBufferedCover(novel.bufferedCover);

        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        titleField = new JTextField(novel.bookTitle);

        authorField = new JTextField(novel.bookAuthor);

        subjectsField = new JTextField();
        if(!novel.bookSubjects.isEmpty()) {
            for (String tag : novel.bookSubjects) {
                subjectsField.setText(subjectsField.getText() + tag + ",");
            }
        }

        coverBtn = new JButton();
        if (novel.bookCover.isEmpty()) {
            coverBtn.setIcon(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
        } else {
            coverBtn.setIcon(new ImageIcon(new ImageIcon(novel.bookCover).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
        }
        coverBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        coverBtn.setBorder(BorderFactory.createEmptyBorder());
        coverBtn.setContentAreaFilled(false);

        if (novel.bookDesc.isEmpty()) {
            descArea = new JTextArea("");
        } else {
            descArea = new JTextArea(novel.bookDesc);
        }
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descScrollPane = new JScrollPane(descArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
}
