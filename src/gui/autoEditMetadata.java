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
        ImageIcon favicon = new ImageIcon(getClass().getResource("/files/images/favicon.png"));
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
                novel.metadata.bookCover = chooser.getSelectedFile().toString();
                try {
                    BufferedImage imageInput = ImageIO.read(chooser.getSelectedFile());
                    novel.metadata.bufferedCover = imageInput;
                    novel.metadata.bufferedCoverName = chooser.getSelectedFile().getName();
                    novel.metadata.bookCover = chooser.getSelectedFile().getName();
                } catch (IOException e) {
                    init.window.appendText("auto", e.getMessage());
                }
                manMetadataImageButton.setIcon(new ImageIcon(new ImageIcon(novel.metadata.bufferedCover).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
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
        novel.metadata.bookTitle = manSetMetadataTitleField.getText();
        init.window.autoBookTitle.setText(novel.metadata.bookTitle);
        // Book Description
        novel.metadata.bookDesc.set(0, autoEditMetadataDescArea.getText());
        // Book Author
        novel.metadata.bookAuthor = manSetMetadataAuthorField.getText();
        init.window.autoAuthor.setText(novel.metadata.bookAuthor);
        novel.metadata.bookSubjects = Arrays.asList(manSetMetadataTags.getText().split(","));
        // No description
        novel.options.noDescription = ignoreDescriptionCheckBox.isSelected();
        // Book Tags
        manSetMetadataTags.setText("");
        int maxNumberOfSubjects = 0;
        init.window.autoBookSubjects.setText("<html>");
        for (String eachTag : novel.metadata.bookSubjects) {
            init.window.autoBookSubjects.setText(init.window.autoBookSubjects.getText() + eachTag + ", ");
            maxNumberOfSubjects++;
            if (maxNumberOfSubjects == 4) {
                maxNumberOfSubjects = 0;
                init.window.autoBookSubjects.setText(init.window.autoBookSubjects.getText() + "<br>");
            }
        }
        if (!init.window.autoBookSubjects.getText().isEmpty()) {
            init.window.autoBookSubjects.setText(
                    init.window.autoBookSubjects.getText().substring(0,
                            init.window.autoBookSubjects.getText().lastIndexOf(",")));
        }
        // Book Cover
        if (novel.metadata.bufferedCover != null) init.window.setBufferedCover(novel.metadata.bufferedCover);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        ignoreDescriptionCheckBox = new JCheckBox();
        ignoreDescriptionCheckBox.setSelected(novel.options.noDescription);
        autoEditMetadataDescArea = new JTextArea(novel.metadata.bookDesc.get(0).toString());
        autoEditMetadataDescArea.setLineWrap(true);
        autoEditMetadataDescArea.setWrapStyleWord(true);
        autoEditMetadataDescArea.setEnabled(!ignoreDescriptionCheckBox.isSelected());
        autoEditMetadataDescScrollPane = new JScrollPane(autoEditMetadataDescArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        if (novel.metadata.bookSubjects != null && novel.metadata.bookTitle != null && novel.metadata.bookAuthor != null) {
            manSetMetadataTitleField = new JTextField(novel.metadata.bookTitle);
            manSetMetadataAuthorField = new JTextField(novel.metadata.bookAuthor);
            manSetMetadataTags = new JTextField();
            for (String tag : novel.metadata.bookSubjects) {
                manSetMetadataTags.setText(manSetMetadataTags.getText() + tag + ",");
            }
            // Removes last ',' from string
            if (!manSetMetadataTags.getText().isEmpty()) {
                manSetMetadataTags.setText(
                        manSetMetadataTags.getText().substring(0,
                                manSetMetadataTags.getText().lastIndexOf(",")));
            }
        }

        if (novel.metadata.bookCover == null) {
            manMetadataImageButton = new JButton();
            manMetadataImageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            manMetadataImageButton.setIcon(new ImageIcon(getClass().getResource("/files/images/cover_placeholder.png")));
            manMetadataImageButton.setBorder(BorderFactory.createEmptyBorder());
            manMetadataImageButton.setContentAreaFilled(false);
        } else {
            manMetadataImageButton = new JButton();
            manMetadataImageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            manMetadataImageButton.setIcon(new ImageIcon(new ImageIcon(novel.metadata.bufferedCover).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
            manMetadataImageButton.setBorder(BorderFactory.createEmptyBorder());
            manMetadataImageButton.setContentAreaFilled(false);
        }
    }
}
