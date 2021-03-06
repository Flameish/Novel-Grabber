package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import grabber.Accounts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class editCookies extends JDialog {
    private JLabel cookieNamesAreaLbl;
    private JLabel cookieValuesAreaLbl;
    private JPanel contentPane;
    private JButton buttonSave;
    private JButton buttonClose;
    private JTextArea cookieNamesArea;
    private JTextArea cookieValuesArea;
    private String domain;

    public editCookies(String domain) {
        this.domain = domain;
        $$$setupUI$$$();
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonSave);
        setModal(true);

        buttonSave.addActionListener(actionEvent -> {
            // Remove empty lines from textAreas
            cookieNamesArea.setText(cookieNamesArea.getText().replaceAll("(?m)^\\s+$", ""));
            cookieValuesArea.setText(cookieValuesArea.getText().replaceAll("(?m)^\\s+$", ""));
            List<String> chapterNames = new ArrayList<>(Arrays.asList(cookieNamesArea.getText().split("\\n")));
            List<String> chapterLinks = new ArrayList<>(Arrays.asList(cookieValuesArea.getText().split("\\n")));
            if (chapterNames.size() == chapterLinks.size()) {
                Map<String, String> cookies = new HashMap<>();
                for (int i = 0; i < chapterNames.size(); i++) {
                    // Cookie name (key) can't be empty
                    if (chapterNames.get(i).trim().isEmpty()) continue;
                    cookies.put(chapterNames.get(i), chapterLinks.get(i));
                }
                Accounts.getInstance().addAccount(domain, cookies);
                dispose();
            } else {
                JOptionPane.showMessageDialog(contentPane, "Lists are not the same length.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonClose.addActionListener(actionEvent -> onCancel());

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

    private void onCancel() {
        dispose();
    }

    public static void main(String domain) {
        editCookies dialog = new editCookies(domain);
        dialog.setTitle(domain);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void createUIComponents() {
        cookieNamesArea = new JTextArea();
        cookieValuesArea = new JTextArea();
        Map<String, String> cookies = Accounts.getInstance().getCookiesForDomain(domain);
        cookies.forEach((name, value) -> {
            cookieNamesArea.append(name + "\n");
            cookieValuesArea.append(value + "\n");
        });
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
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setPreferredSize(new Dimension(500, 500));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setVerifyInputWhenFocusTarget(false);
        contentPane.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setVerifyInputWhenFocusTarget(true);
        scrollPane1.setViewportView(panel1);
        cookieNamesAreaLbl = new JLabel();
        cookieNamesAreaLbl.setText("Cookie name");
        panel1.add(cookieNamesAreaLbl, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cookieValuesAreaLbl = new JLabel();
        cookieValuesAreaLbl.setText("Cookie value");
        panel1.add(cookieValuesAreaLbl, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setVerticalScrollBarPolicy(20);
        panel1.add(scrollPane2, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cookieNamesArea.setMargin(new Insets(0, 0, 0, 0));
        scrollPane2.setViewportView(cookieNamesArea);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel1.add(scrollPane3, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cookieValuesArea.setMargin(new Insets(0, 0, 0, 0));
        scrollPane3.setViewportView(cookieValuesArea);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonSave = new JButton();
        buttonSave.setText("Save");
        panel2.add(buttonSave, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonClose = new JButton();
        buttonClose.setText("Close");
        panel2.add(buttonClose, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
