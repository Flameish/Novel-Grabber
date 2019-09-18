package gui;

import grabber.Download;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private Download currGrab;

    private autoEditMetadata(Download currGrab) {
        this.currGrab = currGrab;
        setContentPane(contentPane);
        setModal(true);
        setTitle("Edit metadata");
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
                currGrab.bookCover = chooser.getSelectedFile().toString();
                try {
                    BufferedImage imageInput = ImageIO.read(chooser.getSelectedFile());
                    currGrab.bufferedCover = imageInput;
                    currGrab.bufferedCoverName = chooser.getSelectedFile().getName();
                    currGrab.bookCover = chooser.getSelectedFile().getName();
                } catch (IOException e) {
                    currGrab.gui.appendText("auto", e.getMessage());
                }
                manMetadataImageButton.setIcon(new ImageIcon(new ImageIcon(currGrab.bufferedCover).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
            }
        });
    }

    static void main(Download currGrab) {
        autoEditMetadata dialog = new autoEditMetadata(currGrab);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void onOK() {
        // Adjust the metadata on the GUI
        // Book Title
        currGrab.bookTitle = manSetMetadataTitleField.getText();
        currGrab.gui.autoBookTitle.setText(currGrab.bookTitle);
        // Book Author
        currGrab.bookAuthor = manSetMetadataAuthorField.getText();
        currGrab.gui.autoAuthor.setText(currGrab.bookAuthor);
        currGrab.bookSubjects = Arrays.asList(manSetMetadataTags.getText().split(","));
        // Book Tags
        manSetMetadataTags.setText("");
        int maxNumberOfSubjects = 0;
        currGrab.gui.autoBookSubjects.setText("<html>");
        for (String eachTag : currGrab.bookSubjects) {
            currGrab.gui.autoBookSubjects.setText(currGrab.gui.autoBookSubjects.getText() + eachTag + ", ");
            maxNumberOfSubjects++;
            if (maxNumberOfSubjects == 4) {
                maxNumberOfSubjects = 0;
                currGrab.gui.autoBookSubjects.setText(currGrab.gui.autoBookSubjects.getText() + "<br>");
            }
        }
        if (!currGrab.gui.autoBookSubjects.getText().isEmpty()) {
            currGrab.gui.autoBookSubjects.setText(
                    currGrab.gui.autoBookSubjects.getText().substring(0,
                            currGrab.gui.autoBookSubjects.getText().lastIndexOf(",")));
        }
        // Book Cover
        if (currGrab.bufferedCover != null) currGrab.gui.setBufferedCover(currGrab.bufferedCover);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        if (currGrab.bookSubjects != null && currGrab.bookTitle != null && currGrab.bookAuthor != null) {
            manSetMetadataTitleField = new JTextField(currGrab.bookTitle);
            manSetMetadataAuthorField = new JTextField(currGrab.bookAuthor);
            manSetMetadataTags = new JTextField();
            for (String tag : currGrab.bookSubjects) {
                manSetMetadataTags.setText(manSetMetadataTags.getText() + tag + ",");
            }
            // Removes last ',' from string
            if (!manSetMetadataTags.getText().isEmpty()) {
                manSetMetadataTags.setText(
                        manSetMetadataTags.getText().substring(0,
                                manSetMetadataTags.getText().lastIndexOf(",")));
            }
        }

        if (currGrab.bookCover == null) {
            manMetadataImageButton = new JButton();
            manMetadataImageButton.setIcon(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
            manMetadataImageButton.setBorder(BorderFactory.createEmptyBorder());
            manMetadataImageButton.setContentAreaFilled(false);
        } else {
            manMetadataImageButton = new JButton();
            manMetadataImageButton.setIcon(new ImageIcon(new ImageIcon(currGrab.bufferedCover).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
            manMetadataImageButton.setBorder(BorderFactory.createEmptyBorder());
            manMetadataImageButton.setContentAreaFilled(false);
        }
    }
}
