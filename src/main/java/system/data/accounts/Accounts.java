package system.data.accounts;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import system.init;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Accounts {
    private static String accountsFile;
    private static Accounts accounts;
    private List<Account> accountList = new ArrayList<>();

    private Accounts() { }

    public static Accounts getInstance() {
        if(accounts == null) {
            accounts = new Accounts();
            try {
                accountsFile = new File(init.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + "/../accounts.ini";
            } catch (URISyntaxException e) {
                accountsFile = "accounts.ini";
                e.printStackTrace();
            }
            accounts.load();
        }
        return accounts;
    }

    /**
     * Reads accounts file(JSON) and creates Accounts object.
     */
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFile))) {
            JSONArray accounts = (JSONArray) new JSONParser().parse(reader);
            for (Object loadedAccount: accounts) {
                accountList.add(new Account((JSONObject) loadedAccount));
            }
        } catch (IOException e) {
            System.out.println("[ACCOUNTS]No file found.");
        } catch (ParseException e) {
            System.out.println("[ACCOUNTS]Could not parse file.");
        }
    }

    /**
     * Saves accounts as JSON file.
     */
    public void save() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(accountsFile))) {
            // Create JSON array from starred novels
            JSONArray accountArray = new JSONArray();
            for(Account account: accountList) {
                accountArray.add(account.getAsJSONObject());
            }
            writer.write(accountArray.toJSONString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void addAccount(Account newAccount) {
        if(!accountList.contains(newAccount)) {
            accountList.add(newAccount);
        } else {
            getAccount(newAccount.getDomain()).setUsername(newAccount.getUsername());
            getAccount(newAccount.getDomain()).setPassword(newAccount.getPassword());
        }
        save();
    }

    /**
     * Get account for domain if it exists.
     * @param domain Account domain
     * @return Found account or empty account
     */
    public Account getAccount(String domain) {
        for(Account account: accountList) {
            if(account.getDomain().equals(domain)) return account;
        }
        return new Account(domain, "", "");
    }
}
