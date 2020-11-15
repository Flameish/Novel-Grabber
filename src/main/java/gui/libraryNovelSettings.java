package gui;

import system.data.library.LibrarySettings;
import system.data.library.LibraryNovel;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.event.*;

public class libraryNovelSettings extends JDialog {
    private LibraryNovel libraryNovel;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField cliStringTextfield;
    private JCheckBox downloadChaptersCheckBox;
    private JSpinner tresholdSpinner;
    private JCheckBox emailNotificationCheckBox;
    private JCheckBox attachmentCheckBox;
    private JCheckBox updateLastChapterCheckBox;
    private JCheckBox desktopNotificationCheckBox;

    public libraryNovelSettings(LibraryNovel libraryNovel) {
        this.libraryNovel = libraryNovel;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle(libraryNovel.getTitle());
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
    }

    private void onOK() {
        libraryNovel.setUpdateLast(updateLastChapterCheckBox.isSelected());
        libraryNovel.setAutoDownloadEnabled(downloadChaptersCheckBox.isSelected());
        libraryNovel.setCliString(cliStringTextfield.getText());
        libraryNovel.setSendAttachmentEnabled(attachmentCheckBox.isSelected());
        libraryNovel.setSendEmailNotification(emailNotificationCheckBox.isSelected());
        libraryNovel.setSendDesktopNotification(desktopNotificationCheckBox.isSelected());
        libraryNovel.setThreshold((Integer) tresholdSpinner.getValue());
        LibrarySettings.getInstance().save();
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(LibraryNovel libraryNovel) {
        libraryNovelSettings dialog = new libraryNovelSettings(libraryNovel);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        cliStringTextfield = new JTextField(libraryNovel.getCliString());

        downloadChaptersCheckBox = new JCheckBox();
        downloadChaptersCheckBox.setSelected(libraryNovel.isAutoDownloadEnabled());

        tresholdSpinner = new JSpinner();
        tresholdSpinner.setValue(libraryNovel.getThreshold());
        JComponent comp = tresholdSpinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        field.setColumns(4);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);

        emailNotificationCheckBox = new JCheckBox();
        emailNotificationCheckBox.setSelected(libraryNovel.isSendEmailNotification());

        desktopNotificationCheckBox = new JCheckBox();
        desktopNotificationCheckBox.setSelected(libraryNovel.isSendDesktopNotification());

        attachmentCheckBox = new JCheckBox();
        attachmentCheckBox.setSelected(libraryNovel.isSendAttachmentEnabled());

        updateLastChapterCheckBox = new JCheckBox();
        updateLastChapterCheckBox.setSelected(libraryNovel.isUpdateLast());

    }
}
