package system.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import system.Config;

import java.io.*;
import java.util.Base64;

/**
 * Handles email config data.
 */
public class EmailConfig {
    private static EmailConfig emailConfig;
    private String username;
    private String password;
    private String host;
    private String receiverEmail;
    private int port;
    private int ssl;

    private EmailConfig() { }

    public static EmailConfig getInstance() {
        if(emailConfig == null) {
            emailConfig = new EmailConfig();
            emailConfig.load();
        }
        return emailConfig;
    }

    /**
     * Saves email config as JSON file.
     */
    public void save() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(Config.getInstance().emailConfig_file_path))) {
            JSONObject emailObj = new JSONObject();
            emailObj.put("username", getUsername());
            emailObj.put("password", getPassword());
            emailObj.put("receiverEmail", getReceiverEmail());
            emailObj.put("host", getHost());
            emailObj.put("port", getPort());
            emailObj.put("ssl", getSsl());
            writer.write(emailObj.toJSONString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads email config file(JSON) and creates EmailConfig object.
     */
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Config.getInstance().emailConfig_file_path))) {
            JSONObject emailObj = (JSONObject) new JSONParser().parse(reader);
            setUsername((String) emailObj.get("username"));
            setPassword((String) emailObj.get("password"));
            setReceiverEmail((String) emailObj.get("receiverEmail"));
            setHost((String) emailObj.get("host"));
            setPort(((Long) emailObj.get("port")).intValue());
            setSsl(((Long) emailObj.get("ssl")).intValue());
        } catch (IOException e) {
            System.out.println("[EMAIL]No file found.");
        } catch (ParseException e) {
            System.out.println("[EMAIL]Could not parse file.");
        }
    }

    public String getUsername() {
        if(username == null) return "";
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if(password == null) return "";
        return new String(Base64.getDecoder().decode(password));
    }

    public void setPassword(String password) {
        this.password = Base64.getEncoder().encodeToString(password.getBytes());
    }

    public String getHost() {
        if(host == null) return "";
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSsl() {
        return ssl;
    }

    public void setSsl(int ssl) {
        this.ssl = ssl;
    }
}
