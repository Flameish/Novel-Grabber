package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class manSetBlacklistedTags extends JDialog {
    DefaultListModel blacklistedTagsListModel;
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField manBlacklistField;
    private JButton addButton;
    private JButton setBlacklistRemoveButton;
    private JList list1;
    private JScrollPane scrollPane1;

    private manSetBlacklistedTags() {
        setContentPane(contentPane);
        setModal(true);
        setTitle("Add blacklisted tags");
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(manBlacklistField.getText() == null || manBlacklistField.getText().isEmpty())) {
                    GUI.blacklistedTags.addAll(Arrays.asList(manBlacklistField.getText().split(",")));
                }
                blacklistedTagsListModel.clear();
                for (String tag : GUI.blacklistedTags) {
                    blacklistedTagsListModel.addElement(tag);
                }
                System.out.println(GUI.blacklistedTags.toString());
            }
        });
        setBlacklistRemoveButton.addActionListener(arg1 -> {
            if (!blacklistedTagsListModel.isEmpty()) {
                int[] indices = list1.getSelectedIndices();
                for (int i = indices.length - 1; i >= 0; i--) {
                    blacklistedTagsListModel.removeElementAt(indices[i]);
                    GUI.blacklistedTags.remove(indices[i]);
                }
            }
        });
    }

    static void main() {
        manSetBlacklistedTags dialog = new manSetBlacklistedTags();
        dialog.pack();
        dialog.setVisible(true);
    }

    private void onOK() {
        dispose();
    }

    private void createUIComponents() {
        blacklistedTagsListModel = new DefaultListModel();
        for (String tag : GUI.blacklistedTags) {
            blacklistedTagsListModel.addElement(tag);
        }
        list1 = new JList(blacklistedTagsListModel);
        scrollPane1 = new JScrollPane(list1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        setBlacklistRemoveButton = new JButton(new ImageIcon(getClass().getResource("/images/remove_icon.png")));
        setBlacklistRemoveButton.setBorder(BorderFactory.createEmptyBorder());
        setBlacklistRemoveButton.setContentAreaFilled(false);
    }
}
