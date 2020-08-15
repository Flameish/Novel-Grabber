package gui;

import grabber.Novel;
import system.init;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class autoEditMetadata extends JDialog {
    public static List<String> manMetadataTags = new ArrayList<>();
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton manMetadataImageButton;
    private JTextField manSetMetadataTitleField;
    private JTextField manSetMetadataAuthorField;
    private JTextField manSetMetadataTags;
    private JScrollPane autoEditMetadataDescScrollPane;
    private JTextArea autoEditMetadataDescArea;
    private JCheckBox ignoreDescriptionCheckBox;
    private Novel novel;

    private autoEditMetadata(Novel novel) {
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
        manMetadataImageButton.addComponentListener(new ComponentAdapter() {
        });
        manMetadataImageButton.addActionListener(arg01 -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open File");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg png gif", "png", "jpg", "gif");
            chooser.setFileFilter(filter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                novel.bookCover = chooser.getSelectedFile().toString();
                try {
                    BufferedImage imageInput = ImageIO.read(chooser.getSelectedFile());
                    novel.bufferedCover = imageInput;
                    novel.bufferedCoverName = chooser.getSelectedFile().getName();
                    novel.bookCover = chooser.getSelectedFile().getName();
                } catch (IOException e) {
                    init.gui.appendText("auto", e.getMessage());
                }
                manMetadataImageButton.setIcon(new ImageIcon(new ImageIcon(novel.bufferedCover).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
            }
        });
        ignoreDescriptionCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ignoreDescriptionCheckBox.isSelected()) autoEditMetadataDescArea.setEnabled(false);
                else {
                    autoEditMetadataDescArea.setEnabled(true);
                }
            }
        });
    }

    static void main(Novel currGrab) {
        autoEditMetadata dialog = new autoEditMetadata(currGrab);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void onOK() {
        // Adjust the metadata on the GUI
        // Book Title
        novel.bookTitle = manSetMetadataTitleField.getText();
        init.gui.autoBookTitle.setText(novel.bookTitle);
        // Book Description
        novel.bookDesc = autoEditMetadataDescArea.getText();
        // Book Author
        novel.bookAuthor = manSetMetadataAuthorField.getText();
        init.gui.autoAuthor.setText(novel.bookAuthor);
        novel.bookSubjects = Arrays.asList(manSetMetadataTags.getText().split(","));
        // No description
        novel.noDescription = ignoreDescriptionCheckBox.isSelected();
        // Book Tags
        manSetMetadataTags.setText("");
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
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        ignoreDescriptionCheckBox = new JCheckBox();
        ignoreDescriptionCheckBox.setSelected(novel.noDescription);
        autoEditMetadataDescArea = new JTextArea(novel.bookDesc);
        autoEditMetadataDescArea.setLineWrap(true);
        autoEditMetadataDescArea.setWrapStyleWord(true);
        autoEditMetadataDescArea.setEnabled(!ignoreDescriptionCheckBox.isSelected());
        autoEditMetadataDescScrollPane = new JScrollPane(autoEditMetadataDescArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        if (novel.bookSubjects != null && novel.bookTitle != null && novel.bookAuthor != null) {
            manSetMetadataTitleField = new JTextField(novel.bookTitle);
            manSetMetadataAuthorField = new JTextField(novel.bookAuthor);
            manSetMetadataTags = new JTextField();
            for (String tag : novel.bookSubjects) {
                manSetMetadataTags.setText(manSetMetadataTags.getText() + tag + ",");
            }
            // Removes last ',' from string
            if (!manSetMetadataTags.getText().isEmpty()) {
                manSetMetadataTags.setText(
                        manSetMetadataTags.getText().substring(0,
                                manSetMetadataTags.getText().lastIndexOf(",")));
            }
        }

        if (novel.bookCover == null) {
            manMetadataImageButton = new JButton();
            manMetadataImageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            manMetadataImageButton.setIcon(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
            manMetadataImageButton.setBorder(BorderFactory.createEmptyBorder());
            manMetadataImageButton.setContentAreaFilled(false);
        } else {
            manMetadataImageButton = new JButton();
            manMetadataImageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            manMetadataImageButton.setIcon(new ImageIcon(new ImageIcon(novel.bufferedCover).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
            manMetadataImageButton.setBorder(BorderFactory.createEmptyBorder());
            manMetadataImageButton.setContentAreaFilled(false);
        }
    }
}
