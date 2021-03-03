package gui;

import grabber.GrabberUtils;
import library.Library;
import library.LibraryNovel;
import system.init;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class libraryNovelSettings extends JDialog {
    private LibraryNovel libraryNovel;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox downloadChaptersCheckBox;
    private JSpinner tresholdSpinner;
    private JCheckBox emailNotificationCheckBox;
    private JCheckBox attachmentCheckBox;
    private JCheckBox updateLastChapterCheckBox;
    private JCheckBox desktopNotificationCheckBox;
    private JButton removeBtn;
    private JPanel novelInfoPanel;
    private JTextField urlField;
    private JTextField saveLocationField;
    private JButton browseSaveLocationBtn;
    private JCheckBox useLoginCheckBox;
    private JButton visitPageBtn;

    public libraryNovelSettings(LibraryNovel libraryNovel) {
        this.libraryNovel = libraryNovel;
        Library library = Library.getInstance();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle(libraryNovel.getMetadata().getTitle());
        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        removeBtn.addActionListener(actionEvent -> {
            int input = JOptionPane.showConfirmDialog(
                    null,
                    "Remove novel from library?",
                    libraryNovel.getMetadata().getTitle(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if(input == 0) {
                library.removeNovel(libraryNovel.getNovelUrl());
                library.writeLibraryFile();
                init.gui.buildLibrary();
                dispose();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        browseSaveLocationBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(GrabberUtils.getCurrentPath()));
            chooser.setDialogTitle("Choose destination directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                saveLocationField.setText(chooser.getSelectedFile().toString());
            }
        });
        // Disable send attachment if download chapters is disabled
        downloadChaptersCheckBox.addActionListener(e -> attachmentCheckBox.setEnabled(downloadChaptersCheckBox.isSelected()));
        visitPageBtn.addActionListener(e -> {
            try {
                GrabberUtils.openWebpage(new URI(urlField.getText()));
            } catch (URISyntaxException ex) {
                GrabberUtils.err(ex.getMessage(), ex);
            }
        });
    }

    private void onOK() {
        libraryNovel.setNovelUrl(urlField.getText());
        libraryNovel.setSaveLocation(saveLocationField.getText());
        libraryNovel.setUpdateLast(updateLastChapterCheckBox.isSelected());
        libraryNovel.setUseAccount(useLoginCheckBox.isSelected());
        libraryNovel.setAutoDownloadEnabled(downloadChaptersCheckBox.isSelected());
        libraryNovel.setSendAttachmentEnabled(attachmentCheckBox.isSelected());
        libraryNovel.setSendEmailNotification(emailNotificationCheckBox.isSelected());
        libraryNovel.setSendDesktopNotification(desktopNotificationCheckBox.isSelected());
        libraryNovel.setThreshold((Integer) tresholdSpinner.getValue());
        Library.getInstance().writeLibraryFile();
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(LibraryNovel libraryNovel) {
        libraryNovelSettings dialog = new libraryNovelSettings(libraryNovel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        urlField = new JTextField(libraryNovel.getNovelUrl());

        saveLocationField = new JTextField(libraryNovel.getSaveLocation());

        browseSaveLocationBtn = new JButton(new ImageIcon(getClass().getResource("/images/folder_icon.png")));
        browseSaveLocationBtn.setBorder(BorderFactory.createEmptyBorder());
        browseSaveLocationBtn.setContentAreaFilled(false);
        browseSaveLocationBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        visitPageBtn = new JButton(new ImageIcon(getClass().getResource("/images/website_icon.png")));
        visitPageBtn.setBorder(BorderFactory.createEmptyBorder());
        visitPageBtn.setContentAreaFilled(false);
        visitPageBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        useLoginCheckBox = new JCheckBox();
        useLoginCheckBox.setSelected(libraryNovel.isUseAccount());

        downloadChaptersCheckBox = new JCheckBox();
        downloadChaptersCheckBox.setSelected(libraryNovel.isAutoDownloadEnabled());

        tresholdSpinner = new JSpinner();
        tresholdSpinner.setValue(libraryNovel.getThreshold());
        JComponent comp = tresholdSpinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        field.setColumns(2);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        tresholdSpinner.setValue(1);

        emailNotificationCheckBox = new JCheckBox();
        emailNotificationCheckBox.setSelected(libraryNovel.isSendEmailNotification());

        desktopNotificationCheckBox = new JCheckBox();
        desktopNotificationCheckBox.setSelected(libraryNovel.isSendDesktopNotification());

        attachmentCheckBox = new JCheckBox();
        attachmentCheckBox.setEnabled(libraryNovel.isAutoDownloadEnabled());
        attachmentCheckBox.setSelected(libraryNovel.isSendAttachmentEnabled());

        updateLastChapterCheckBox = new JCheckBox();
        updateLastChapterCheckBox.setSelected(libraryNovel.isUpdateLast());

        removeBtn = new JButton(new ImageIcon(getClass().getResource("/images/remove_icon.png")));
        removeBtn.setBorder(BorderFactory.createEmptyBorder());
        removeBtn.setContentAreaFilled(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.setToolTipText("Remove novel from library");
    }
}
