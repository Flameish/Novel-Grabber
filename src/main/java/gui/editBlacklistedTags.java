package gui;

import javax.swing.*;
import java.util.List;

public class editBlacklistedTags extends JDialog {
    DefaultListModel blacklistedTagsListModel;
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField blacklistedTagField;
    private JButton addButton;
    private JButton setBlacklistRemoveButton;
    private JList list1;
    private JScrollPane scrollPane1;
    private List<String> blacklistedTags;

    private editBlacklistedTags(List<String> blacklistedTags) {
        this.blacklistedTags = blacklistedTags;
        setContentPane(contentPane);
        setModal(true);
        setTitle("Edit blacklisted tags");
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addButton.addActionListener(e -> {
            if (!(blacklistedTagField.getText() == null || blacklistedTagField.getText().isEmpty())) {
                blacklistedTags.add(blacklistedTagField.getText());
            }
            blacklistedTagsListModel.clear();
            for (String tag : blacklistedTags) {
                blacklistedTagsListModel.addElement(tag);
            }
        });
        setBlacklistRemoveButton.addActionListener(arg1 -> {
            if (!blacklistedTagsListModel.isEmpty()) {
                int[] indices = list1.getSelectedIndices();
                for (int i = indices.length - 1; i >= 0; i--) {
                    blacklistedTagsListModel.removeElementAt(indices[i]);
                    blacklistedTags.remove(indices[i]);
                }
            }
        });
    }

    static void main(List<String> blacklistedTags) {
        editBlacklistedTags dialog = new editBlacklistedTags(blacklistedTags);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void onOK() {
        dispose();
    }

    private void createUIComponents() {
        blacklistedTagsListModel = new DefaultListModel();
        for (String tag : blacklistedTags) {
            blacklistedTagsListModel.addElement(tag);
        }
        list1 = new JList(blacklistedTagsListModel);
        scrollPane1 = new JScrollPane(list1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        setBlacklistRemoveButton = new JButton(new ImageIcon(getClass().getResource("/images/remove_icon.png")));
        setBlacklistRemoveButton.setBorder(BorderFactory.createEmptyBorder());
        setBlacklistRemoveButton.setContentAreaFilled(false);
    }
}
