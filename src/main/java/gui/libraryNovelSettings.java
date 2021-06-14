package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import grabber.GrabberUtils;
import library.Library;
import library.LibraryNovel;
import system.init;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

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
    private JCheckBox checkThisNovelCheckBox;
    private JButton checkNowBtn;
    private JLabel checkingBusyLbl;
    private JLabel tresholdLbl;
    private JTextField waitTimeField;
    private JCheckBox getImagesCheckBox;
    private JCheckBox displayChapterTitleCheckBox;

    public libraryNovelSettings(LibraryNovel libraryNovel) {
        this.libraryNovel = libraryNovel;
        $$$setupUI$$$();
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
            if (input == 0) {
                library.removeNovel(libraryNovel.getNovelUrl());
                library.writeLibraryFile();
                init.gui.buildLibHostComboBox();
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
        downloadChaptersCheckBox.addActionListener(e -> {
            attachmentCheckBox.setVisible(downloadChaptersCheckBox.isSelected());
            tresholdLbl.setVisible(downloadChaptersCheckBox.isSelected());
            tresholdSpinner.setVisible(downloadChaptersCheckBox.isSelected());

        });
        visitPageBtn.addActionListener(e -> {
            try {
                GrabberUtils.openWebpage(new URI(urlField.getText()));
            } catch (URISyntaxException ex) {
                GrabberUtils.err(ex.getMessage(), ex);
            }
        });
        checkNowBtn.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
            try {
                checkNowBtn.setVisible(false);
                checkingBusyLbl.setVisible(true);
                Library.getInstance().checkNovel(libraryNovel);
            } catch (ClassNotFoundException | IOException ex) {
                init.gui.showPopup(ex.getMessage(), "error");
                GrabberUtils.err(ex.getMessage(), ex);
            } finally {
                checkingBusyLbl.setVisible(false);
                checkNowBtn.setVisible(true);
            }
        }));
    }

    private void onOK() {
        if (waitTimeField.getText().isEmpty()) {
            init.gui.showPopup("Wait time cannot be empty.", "warning");
            return;
        }
        if (!waitTimeField.getText().matches("\\d+") && !waitTimeField.getText().isEmpty()) {
            init.gui.showPopup("Wait time must contain numbers.", "warning");
            return;
        }
        libraryNovel.setNovelUrl(urlField.getText());
        libraryNovel.setSaveLocation(saveLocationField.getText());
        libraryNovel.setUpdateLast(updateLastChapterCheckBox.isSelected());
        libraryNovel.setCheckingActive(checkThisNovelCheckBox.isSelected());
        libraryNovel.setUseAccount(useLoginCheckBox.isSelected());
        libraryNovel.setAutoDownloadEnabled(downloadChaptersCheckBox.isSelected());
        libraryNovel.setSendAttachmentEnabled(attachmentCheckBox.isSelected());
        libraryNovel.setSendEmailNotification(emailNotificationCheckBox.isSelected());
        libraryNovel.setSendDesktopNotification(desktopNotificationCheckBox.isSelected());
        libraryNovel.setThreshold((Integer) tresholdSpinner.getValue());
        libraryNovel.setWaitTime(Integer.valueOf(waitTimeField.getText()));
        libraryNovel.setGetImages(getImagesCheckBox.isSelected());
        libraryNovel.setDisplayChapterTitle(displayChapterTitleCheckBox.isSelected());
        Library.getInstance().writeLibraryFile();
        dispose();
        init.gui.buildLibrary();
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

        getImagesCheckBox = new JCheckBox();
        getImagesCheckBox.setSelected(libraryNovel.isGetImages());

        displayChapterTitleCheckBox = new JCheckBox();
        displayChapterTitleCheckBox.setSelected(libraryNovel.isDisplayChapterTitle());

        waitTimeField = new JTextField();
        waitTimeField.setText(String.valueOf(libraryNovel.getWaitTime()));
        waitTimeField.setHorizontalAlignment(SwingConstants.CENTER);

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

        checkThisNovelCheckBox = new JCheckBox();
        checkThisNovelCheckBox.setSelected(libraryNovel.isCheckingActive());

        removeBtn = new JButton(new ImageIcon(getClass().getResource("/images/remove_icon.png")));
        removeBtn.setBorder(BorderFactory.createEmptyBorder());
        removeBtn.setContentAreaFilled(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.setToolTipText("Remove novel from library");

        checkNowBtn = new JButton(new ImageIcon(getClass().getResource("/images/check_icon.png")));
        checkNowBtn.setBorder(BorderFactory.createEmptyBorder());
        checkNowBtn.setContentAreaFilled(false);
        checkNowBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkNowBtn.setToolTipText("Check for new releases now");

        checkingBusyLbl = new JLabel(new ImageIcon(getClass().getResource("/images/busy.gif")));
        checkingBusyLbl.setBorder(BorderFactory.createEmptyBorder());
        checkingBusyLbl.setVisible(false);

    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMinimumSize(new Dimension(530, 450));
        contentPane.setPreferredSize(new Dimension(530, 450));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("Save");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeBtn.setText("");
        panel1.add(removeBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkNowBtn.setText("");
        panel1.add(checkNowBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkingBusyLbl.setText("");
        panel1.add(checkingBusyLbl, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 5, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 2, new Insets(0, 10, 10, 10), -1, -1));
        panel3.add(panel4, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(null, "Notifications", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        emailNotificationCheckBox.setText("Email");
        panel4.add(emailNotificationCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        desktopNotificationCheckBox.setText("Desktop");
        panel4.add(desktopNotificationCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        novelInfoPanel = new JPanel();
        novelInfoPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 10, 10, 10), -1, -1));
        panel3.add(novelInfoPanel, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        novelInfoPanel.setBorder(BorderFactory.createTitledBorder(null, "Novel", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 3, new Insets(0, 10, 5, 10), -1, -1));
        novelInfoPanel.add(panel5, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(null, "Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        getImagesCheckBox.setText("Get images");
        getImagesCheckBox.setToolTipText("Download potential images from a chapter");
        panel5.add(getImagesCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayChapterTitleCheckBox.setText("Add chapter title");
        panel5.add(displayChapterTitleCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        useLoginCheckBox.setText("Use account");
        panel5.add(useLoginCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Wait time:");
        panel6.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        waitTimeField.setToolTipText("Wait time between each chapter call in milliseconds. Please use appropriate wait times to not flood the host server.");
        panel6.add(waitTimeField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Novel URL:");
        novelInfoPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        novelInfoPanel.add(urlField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Save location:");
        novelInfoPanel.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        novelInfoPanel.add(saveLocationField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        browseSaveLocationBtn.setMargin(new Insets(0, 0, 0, 10));
        browseSaveLocationBtn.setText("");
        browseSaveLocationBtn.setToolTipText("Browse files");
        novelInfoPanel.add(browseSaveLocationBtn, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        visitPageBtn.setMargin(new Insets(0, 0, 0, 10));
        visitPageBtn.setText("");
        visitPageBtn.setToolTipText("Open link in browser");
        novelInfoPanel.add(visitPageBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 3, new Insets(0, 10, 10, 10), -1, -1));
        panel3.add(panel7, new GridConstraints(1, 0, 2, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(null, "Library", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        updateLastChapterCheckBox.setText("Update last chapter number");
        updateLastChapterCheckBox.setToolTipText("When checking this option the \"local\" last chapter number will be updated to reflect the newest online. ");
        panel7.add(updateLastChapterCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkThisNovelCheckBox.setText("Check this novel");
        panel7.add(checkThisNovelCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel8, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tresholdLbl = new JLabel();
        tresholdLbl.setText("Treshold (new chapters):");
        tresholdLbl.setToolTipText("Set how many new chapters must amount to.");
        tresholdLbl.setVisible(false);
        panel8.add(tresholdLbl, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tresholdSpinner.setVisible(false);
        panel8.add(tresholdSpinner, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        downloadChaptersCheckBox.setText("Download new chapters");
        downloadChaptersCheckBox.setToolTipText("Automatically download new chapters if the comulative amount of new releases pass set treshold.");
        panel8.add(downloadChaptersCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel8.add(separator1, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        attachmentCheckBox.setText("Send novel file to email");
        attachmentCheckBox.setVisible(false);
        panel8.add(attachmentCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel7.add(spacer3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JSeparator separator2 = new JSeparator();
        contentPane.add(separator2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
