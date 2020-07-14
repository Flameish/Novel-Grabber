package system.persistent;

import org.json.simple.JSONObject;
import system.Config;

import java.util.Base64;

public class EmailConfig {
    public static String getUsername() {
        JSONObject email = (JSONObject) Config.data.get("email");
        if(email == null || !email.containsKey("username")) return "";
        return (String) email.get("username");
    }

    public static String getPassword() {
        JSONObject email = (JSONObject) Config.data.get("email");
        if(email == null || !email.containsKey("password")) return "";
        String password = (String) email.get("password");
        return new String(Base64.getDecoder().decode(password));
    }

    public static String getHost() {
        JSONObject email = (JSONObject) Config.data.get("email");
        if(email == null || !email.containsKey("host")) return "";
        return (String) email.get("host");
    }

    public static int getPort() {
        JSONObject email = (JSONObject) Config.data.get("email");
        if(email == null || !email.containsKey("port")) return 587;
        return Integer.parseInt(String.valueOf(email.get("port")));
    }

    public static int getSSL() {
        JSONObject email = (JSONObject) Config.data.get("email");
        if(email == null || !email.containsKey("ssl")) return 0;
        return Integer.parseInt(String.valueOf(email.get("ssl")));
    }

    public static String getReceiverEmail() {
        JSONObject email = (JSONObject) Config.data.get("email");
        if(email == null || !email.containsKey("receiverEmail")) return "";
        return (String) email.get("receiverEmail");
    }

    public static void saveEmailSettings(String host, String username, String password, String receiverEmail, int port, int SSL) {
        JSONObject email = (JSONObject) Config.data.get("email");
        if(email == null) email = new JSONObject();
        email.put("password", Base64.getEncoder().encodeToString(password.getBytes()));
        email.put("username", username);
        email.put("receiverEmail", receiverEmail);
        email.put("host", host);
        email.put("port", port);
        email.put("ssl", SSL);
        Config.data.put("email", email);
        Config.saveConfig();
    }
}
