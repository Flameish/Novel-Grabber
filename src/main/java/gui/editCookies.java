package gui;

import system.data.accounts.Account;
import system.data.accounts.Accounts;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class editCookies extends JDialog {
    private JPanel contentPane;
    private JLabel chapterNamesAreaLbl;
    private JLabel chapterLinksAreaLbl;
    private JButton buttonSave;
    private JButton buttonClose;
    private JTextArea chapterNamesArea;
    private JTextArea chapterLinksArea;
    private Account account;
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
            chapterNamesArea.setText(chapterNamesArea.getText().replaceAll("(?m)^\\s+$", ""));
            chapterLinksArea.setText(chapterLinksArea.getText().replaceAll("(?m)^\\s+$", ""));
            List<String> chapterNames = new ArrayList<>(Arrays.asList(chapterNamesArea.getText().split("\\n")));
            List<String> chapterLinks = new ArrayList<>(Arrays.asList(chapterLinksArea.getText().split("\\n")));
            if(chapterNames.size() == chapterLinks.size()) {
                Map<String, String> cookies = new HashMap<>();
                for(int i = 0; i < chapterNames.size(); i++) {
                    cookies.put(chapterNames.get(i), chapterLinks.get(i));
                }
                Accounts.getInstance().addAccount(new Account(domain, cookies));
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
        chapterNamesArea = new JTextArea();
        chapterLinksArea = new JTextArea();
        account = Accounts.getInstance().getAccount(domain);
        for (Map.Entry<String, String> cookie : account.getCookies().entrySet()) {
            chapterNamesArea.append(cookie.getKey() + "\n");
            chapterLinksArea.append(cookie.getValue() + "\n");
        }
    }
}
