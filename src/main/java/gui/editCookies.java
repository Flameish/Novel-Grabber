package gui;

import grabber.Accounts;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

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
            if(chapterNames.size() == chapterLinks.size()) {
                Map<String, String> cookies = new HashMap<>();
                for(int i = 0; i < chapterNames.size(); i++) {
                    // Cookie name (key) can't be empty
                    if(chapterNames.get(i).trim().isEmpty()) continue;
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
}
