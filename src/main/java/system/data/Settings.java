package system.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import system.Config;

import java.io.*;

/**
 * Handles general setting data.
 */
public class Settings {
    private static Settings settings;

    private String browser;
    private String saveLocation;
    private boolean autoGetImages;
    private boolean removeStyling;
    private boolean useStandardLocation;
    private int filenameFormat;

    private Settings() { }

    public static Settings getInstance() {
        if(settings == null) {
            settings = new Settings();
            settings.load();
        }
        return settings;
    }

    /**
     * Reads settings file(JSON) and creates Settings object.
     */
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Config.getInstance().settings_file_path))) {
            JSONObject settingsObj = (JSONObject) new JSONParser().parse(reader);
            setBrowser((String) settingsObj.get("browser"));
            setFilenameFormat(((Long) settingsObj.get("filenameFormat")).intValue());
            setAutoGetImages((boolean) settingsObj.get("autoGetImages"));
            setSaveLocation((String) settingsObj.get("saveLocation"));
            setRemoveStyling((boolean) settingsObj.get("removeStyling"));
            setUseStandardLocation((boolean) settingsObj.get("useStandardLocation"));
        } catch (IOException e) {
            System.out.println("[SETTINGS]No file found.");
        } catch (ParseException e) {
            System.out.println("[SETTINGS]Could not parse file.");
        }
    }

    /**
     * Saves settings as JSON file.
     */
    public void save() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(Config.getInstance().settings_file_path))) {
            JSONObject settingsObj = new JSONObject();
            settingsObj.put("browser", getBrowser());
            settingsObj.put("filenameFormat", getFilenameFormat());
            settingsObj.put("autoGetImages", isAutoGetImages());
            settingsObj.put("saveLocation", getSaveLocation());
            settingsObj.put("removeStyling", isRemoveStyling());
            settingsObj.put("useStandardLocation", isUseStandardLocation());
            writer.write(settingsObj.toJSONString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public int getFilenameFormat() {
        return filenameFormat;
    }

    public void setFilenameFormat(int filenameFormat) {
        this.filenameFormat = filenameFormat;
    }

    public boolean isAutoGetImages() {
        return autoGetImages;
    }

    public void setAutoGetImages(boolean autoGetImages) {
        this.autoGetImages = autoGetImages;
    }

    public String getSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;
    }

    public boolean isRemoveStyling() {
        return removeStyling;
    }

    public void setRemoveStyling(boolean removeStyling) {
        this.removeStyling = removeStyling;
    }

    public boolean isUseStandardLocation() {
        return useStandardLocation;
    }

    public void setUseStandardLocation(boolean useStandardLocation) {
        this.useStandardLocation = useStandardLocation;
    }
}
