package system.data.accounts;

import org.json.simple.JSONObject;

import java.util.Base64;
import java.util.Objects;

public class Account {
    private String domain;
    private String username;
    private String password;

    public Account(String domain, String username, String password) {
        this.domain = domain;
        this.username = username;
        this.password = Base64.getEncoder().encodeToString(password.getBytes());
    }

    /**
     * Creates an account from a JSON object loaded from file.
     * @param account
     */
    public Account(JSONObject account) {
        domain = (String) account.get("domain");
        username = (String) account.get("username");
        password = (String) account.get("password");
    }

    /**
     * Returns account as JSON object.
     */
    public JSONObject getAsJSONObject() {
        JSONObject accountObj = new JSONObject();
        accountObj.put("domain", getDomain());
        accountObj.put("username", getUsername());
        accountObj.put("password", password); // getPassword decodes password
        return accountObj;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return new String(Base64.getDecoder().decode(password));
    }

    public void setPassword(String password) {
        this.password = Base64.getEncoder().encodeToString(password.getBytes());
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
