package system;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Base64;

public class Accounts {
    public static JSONObject accounts = new JSONObject();

    public static void readAccounts() throws ParseException {
        try(BufferedReader reader = new BufferedReader(new FileReader(Config.account_Config_Path))) {
            accounts = (JSONObject) new JSONParser().parse(reader);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAccounts() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(Config.account_Config_Path))) {
            writer.write(accounts.toJSONString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUsername(String domain) {
        return accounts.containsKey(domain) ? String.valueOf(((JSONObject) accounts.get(domain)).get("username")) : "";
    }

    public static String getPassword(String domain) {
        String password = accounts.containsKey(domain) ? String.valueOf(((JSONObject) accounts.get(domain)).get("password")) : "";
        return new String(Base64.getDecoder().decode(password));
    }

    // Can add new and edit existing objects
    public static void addAccount(String domain, String username, String password) {
        JSONObject accountDetails = new JSONObject();
        accountDetails.put("username", username);
        accountDetails.put("password", Base64.getEncoder().encodeToString(password.getBytes()));

        Accounts.accounts.put(domain, accountDetails);

        saveAccounts();
    }
}
