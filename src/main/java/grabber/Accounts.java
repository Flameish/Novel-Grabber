package grabber;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Accounts {
    private final static String accountsFile = GrabberUtils.getCurrentPath() + "/accounts.json";
    private static Accounts accounts;
    private Map<String, Map<String, String>> domainCookies = new HashMap<>();

    private Accounts() { }

    public static Accounts getInstance() {
        if(accounts == null) {
            accounts = new Accounts();
            accounts.readAccountsFile();
        }
        return accounts;
    }

    private void readAccountsFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFile))) {
            JSONArray accounts = (JSONArray) new JSONParser().parse(reader);
            for (Object loadedAccount: accounts) {
                JSONObject accountObj = (JSONObject) loadedAccount;
                String domain = (String) accountObj.get("domain");
                HashMap cookies = (HashMap<String, String>) accountObj.get("cookies");
                domainCookies.put(domain, cookies);
            }
        } catch (IOException e) {
            GrabberUtils.err("No accounts file found.");
        } catch (ParseException e) {
            GrabberUtils.err("Could not parse accounts file.", e);
        }
    }

    public void writeAccountsFile() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(accountsFile))) {
            JSONArray accountArray = new JSONArray();
            domainCookies.forEach((domain, cookies) -> {
                JSONObject accountObj = new JSONObject();
                accountObj.put("domain", domain);
                accountObj.put("cookies", new JSONObject(cookies));
                accountArray.add(accountObj);
            });
            writer.write(accountArray.toJSONString());
        } catch(IOException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
    }

    public void addAccount(String domain, Map<String, String> cookies) {
        domainCookies.put(domain, cookies);
        writeAccountsFile();
    }

    public Map<String, String> getCookiesForDomain(String domain) {
        Map<String, String> cookies = domainCookies.get(domain);
        if (cookies != null) return cookies;
        return new HashMap<>();
    }
}
