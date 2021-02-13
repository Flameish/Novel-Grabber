package system.data;

import grabber.GrabberUtils;
import system.init;

import java.io.*;
import java.util.*;

/**
 * Handles persistent setting data.
 */
public class Settings {
    private final static String settingsFile = GrabberUtils.getCurrentPath() + "/settings.ini";
    private static Settings settings;
    private String browser = "";
    private String saveLocation = "";
    private String username = "";
    private String password = "";
    private String host = "";
    private String receiverEmail = "";
    private String ssl = "SMTP";
    private String telegramApiToken = "";
    private boolean autoGetImages = false;
    private boolean removeStyling = false;
    private boolean useStandardLocation = false;
    private boolean pollingEnabled = true;
    private List<String> headlessList = new ArrayList<>();
    private int filenameFormat = 0;
    private int outputFormat = 0;
    private int port = 25;
    private int frequency = 20;

    private Settings() { }

    public static Settings getInstance() {
        if(settings == null) {
            settings = new Settings();
            settings.load();
        }
        return settings;
    }

    /**
     * Reads settings from file.
     */
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {
            Properties prop = new Properties();
            prop.load(reader);

            setUsername(prop.getProperty("username"));
            setPassword(prop.getProperty("password"));
            setReceiverEmail(prop.getProperty("receiverEmail"));
            setHost(prop.getProperty("host"));
            setSsl(prop.getProperty("ssl"));
            setPort(Integer.parseInt(prop.getProperty("port")));
            setBrowser(prop.getProperty("browser"));
            setFilenameFormat(Integer.parseInt(prop.getProperty("filenameFormat")));
            setOutputFormat(Integer.parseInt(prop.getProperty("outputFormat")));
            setAutoGetImages(Boolean.parseBoolean(prop.getProperty("autoGetImages")));
            setSaveLocation(prop.getProperty("saveLocation"));
            setRemoveStyling(Boolean.parseBoolean(prop.getProperty("removeStyling")));
            setUseStandardLocation(Boolean.parseBoolean(prop.getProperty("useStandardLocation")));
            setFrequency(Integer.parseInt(prop.getProperty("frequency")));
            setPollingEnabled(Boolean.parseBoolean(prop.getProperty("pollingEnabled")));
            setTelegramApiToken(prop.getProperty("telegramApiToken"));
            setSaveLocation(prop.getProperty("saveLocation"));
            setHeadlessList(new ArrayList<>(Arrays.asList(prop.getProperty("headlessList").split(","))));
        } catch (IOException e) {
            GrabberUtils.err("No settings file found.");
        }
    }

    /**
     * Saves settings to file.
     */
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile))) {
            Properties prop = new Properties();

            prop.setProperty("username", getUsername());
            prop.setProperty("password", getPassword());
            prop.setProperty("receiverEmail", getReceiverEmail());
            prop.setProperty("host", getHost());
            prop.setProperty("ssl", getSsl());
            prop.setProperty("port", String.valueOf(getPort()));
            prop.setProperty("browser", getBrowser());
            prop.setProperty("filenameFormat", String.valueOf(getFilenameFormat()));
            prop.setProperty("outputFormat", String.valueOf(getOutputFormat()));
            prop.setProperty("autoGetImages", String.valueOf(isAutoGetImages()));
            prop.setProperty("saveLocation", getSaveLocation());
            prop.setProperty("removeStyling", String.valueOf(isRemoveStyling()));
            prop.setProperty("useStandardLocation", String.valueOf(isUseStandardLocation()));
            prop.setProperty("frequency", String.valueOf(getFrequency()));
            prop.setProperty("pollingEnabled", String.valueOf(isPollingEnabled()));
            prop.setProperty("headlessList", String.join(",", headlessList));
            prop.store(writer, "Novel-Grabber version: " + init.versionNumber);
        } catch (IOException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
    }
    // Getter
    public int getFrequency() {
        return frequency;
    }
    public int getPort() {
        return port;
    }
    public int getFilenameFormat() {
        return filenameFormat;
    }
    public int getOutputFormat() {
        return outputFormat;
    }
    public boolean isPollingEnabled() {
        return pollingEnabled;
    }
    public boolean isAutoGetImages() {
        return autoGetImages;
    }
    public boolean isRemoveStyling() {
        return removeStyling;
    }
    public boolean isUseStandardLocation() {
        return useStandardLocation;
    }
    public String getBrowser() {
        return browser;
    }
    public String getSsl() {
        return ssl;
    }
    public String getTelegramApiToken() {
        return telegramApiToken;
    }
    public String getSaveLocation() {
        return saveLocation;
    }
    public String getHost() {
        return host;
    }
    public String getPassword() {
        return new String(Base64.getDecoder().decode(password));
    }
    public String getUsername() {
        return username;
    }
    public String getReceiverEmail() {
        return receiverEmail;
    }
    public List<String> getHeadlessList() {
        return headlessList;
    }
    // Setter
    public void setPort(int port) {
        this.port = port;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public void setOutputFormat(int outputFormat) {
        this.outputFormat = outputFormat;
    }
    public void setFilenameFormat(int filenameFormat) {
        this.filenameFormat = filenameFormat;
    }
    public void setPollingEnabled(boolean pollingEnabled) {
        this.pollingEnabled = pollingEnabled;
    }
    public void setAutoGetImages(boolean autoGetImages) {
        this.autoGetImages = autoGetImages;
    }
    public void setRemoveStyling(boolean removeStyling) {
        this.removeStyling = removeStyling;
    }
    public void setUseStandardLocation(boolean useStandardLocation) {
        this.useStandardLocation = useStandardLocation;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = Base64.getEncoder().encodeToString(password.getBytes());
    }
    public void setHost(String host) {
        this.host = host;
    }
    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }
    public void setSsl(String ssl) {
        this.ssl = ssl;
    }
    public void setBrowser(String browser) {
        this.browser = browser;
    }
    public void setTelegramApiToken(String token) {
        this.telegramApiToken = token;
    }
    public void setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;
    }
    public void setHeadlessList(List<String> headlessList) {
        this.headlessList = headlessList;
    }
}
