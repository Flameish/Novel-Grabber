package system.persistent;

import org.json.simple.JSONObject;
import system.Config;

import java.util.Base64;

public class Accounts {
    public static String getUsername(String domain) {
        JSONObject accounts = (JSONObject) Config.data.get("accounts");
        if(accounts == null || !accounts.containsKey(domain)) return "";
        return (String) ((JSONObject) accounts.get(domain)).get("username");
    }

    public static String getPassword(String domain) {
        JSONObject accounts = (JSONObject) Config.data.get("accounts");
        if(accounts == null || !accounts.containsKey(domain)) return "";
        String password = (String) ((JSONObject) accounts.get(domain)).get("password");
        return new String(Base64.getDecoder().decode(password));
    }

    public static void setAccount(String domain, String username, String password) {
        JSONObject accounts = (JSONObject) Config.data.get("accounts");
        if(accounts == null) accounts = new JSONObject();
        JSONObject accountDetails = new JSONObject();
        accountDetails.put("password", Base64.getEncoder().encodeToString(password.getBytes()));
        accountDetails.put("username", username);
        accounts.put(domain, accountDetails);
        Config.data.put("accounts", accounts);
        Config.saveConfig();
    }
}
