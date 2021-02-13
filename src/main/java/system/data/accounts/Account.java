package system.data.accounts;

import org.json.simple.JSONObject;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class Account {
    private String domain;
    private Map<String, String> cookies;

    public Account(String domain, Map<String, String> cookies) {
        this.domain = domain;
        this.cookies = cookies;
    }

    /**
     * Creates an account from a JSON object loaded from file.
     * @param account
     */
    public Account(JSONObject account) {
        domain = (String) account.get("domain");
        cookies = (Map<String, String>) account.get("cookies");
    }

    /**
     * Returns account as JSON object.
     */
    public JSONObject getAsJSONObject() {
        JSONObject accountObj = new JSONObject();
        accountObj.put("domain", getDomain());
        accountObj.put("cookies", new JSONObject(cookies));
        return accountObj;
    }

    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public Map<String, String> getCookies() {
        return cookies;
    }
    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return domain.equals(account.domain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain);
    }
}
