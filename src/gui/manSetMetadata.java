package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class manSetMetadata extends JDialog {
    public static String[] manMetadataInfo = new String[]{"", "", "", ""};
    public static boolean noDescription = false;
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

    public manSetMetadata() {
        setContentPane(contentPane);
        setModal(true);
        setTitle("Edit EPUB metadata");
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        manMetadataImageButton.addComponentListener(new ComponentAdapter() {
        });
        // Add image
        manMetadataImageButton.addActionListener(arg01 -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open File");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg png gif", "png", "jpg", "gif");
            chooser.setFileFilter(filter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                manMetadataInfo[2] = chooser.getSelectedFile().toString();
                manMetadataImageButton.setIcon(new ImageIcon(new ImageIcon(manMetadataInfo[2]).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
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

    static void main() {
        manSetMetadata dialog = new manSetMetadata();
        dialog.pack();
        dialog.setVisible(true);
    }

    private void onOK() {
        manMetadataInfo[0] = manSetMetadataTitleField.getText();
        manMetadataInfo[1] = manSetMetadataAuthorField.getText();
        manMetadataInfo[3] = autoEditMetadataDescArea.getText();
        manMetadataTags = Arrays.asList(manSetMetadataTags.getText().split(","));
        noDescription = ignoreDescriptionCheckBox.isSelected();
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        ignoreDescriptionCheckBox = new JCheckBox();
        ignoreDescriptionCheckBox.setSelected(noDescription);
        manSetMetadataTitleField = new JTextField(manMetadataInfo[0]);
        manSetMetadataAuthorField = new JTextField(manMetadataInfo[1]);
        manSetMetadataTags = new JTextField();
        for (String tag : manMetadataTags) {
            manSetMetadataTags.setText(manSetMetadataTags.getText() + tag + ",");
        }
        if (!manSetMetadataTags.getText().isEmpty()) {
            manSetMetadataTags.setText(
                    manSetMetadataTags.getText().substring(0,
                            manSetMetadataTags.getText().lastIndexOf(",")));
        }
        if (manMetadataInfo[2].isEmpty()) {
            manMetadataImageButton = new JButton();
            manMetadataImageButton.setIcon(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
            manMetadataImageButton.setBorder(BorderFactory.createEmptyBorder());
            manMetadataImageButton.setContentAreaFilled(false);
        } else {
            manMetadataImageButton = new JButton();
            manMetadataImageButton.setIcon(new ImageIcon(new ImageIcon(manMetadataInfo[2]).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
            manMetadataImageButton.setBorder(BorderFactory.createEmptyBorder());
            manMetadataImageButton.setContentAreaFilled(false);
        }
        if (manMetadataInfo[3].isEmpty()) {
            autoEditMetadataDescArea = new JTextArea("");
            autoEditMetadataDescArea.setLineWrap(true);
            autoEditMetadataDescArea.setWrapStyleWord(true);
            autoEditMetadataDescArea.setEnabled(!ignoreDescriptionCheckBox.isSelected());
            autoEditMetadataDescScrollPane = new JScrollPane(autoEditMetadataDescArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        } else {
            autoEditMetadataDescArea = new JTextArea(manMetadataInfo[3]);
            autoEditMetadataDescArea.setLineWrap(true);
            autoEditMetadataDescArea.setWrapStyleWord(true);
            autoEditMetadataDescArea.setEnabled(!ignoreDescriptionCheckBox.isSelected());
            autoEditMetadataDescScrollPane = new JScrollPane(autoEditMetadataDescArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

    }
}
