package system.persistent;

import org.json.simple.JSONObject;
import system.Config;

public class Settings {
    public static String getBrowser() {
        JSONObject headless = (JSONObject) Config.data.get("headless");
        if(headless == null || !headless.containsKey("browser")) return "Chrome";
        return (String) headless.get("browser");
    }

    public static boolean getBrowserShowGUI() {
        JSONObject headless = (JSONObject) Config.data.get("headless");
        if(headless == null || !headless.containsKey("showBrowserGUI")) return false;
        return (boolean) headless.get("showBrowserGUI");
    }

    public static int getEPUBOutputFormat() {
        JSONObject generalSettings = (JSONObject) Config.data.get("generalSettings");
        if(generalSettings == null || !generalSettings.containsKey("filenameFormat")) return 0;
        return Integer.parseInt(String.valueOf(generalSettings.get("filenameFormat")));
    }

    public static boolean getImages() {
        JSONObject generalSettings = (JSONObject) Config.data.get("generalSettings");
        if(generalSettings == null || !generalSettings.containsKey("autoGetImages")) return false;
        return (boolean) generalSettings.get("autoGetImages");
    }

    public static String getSavelocation() {
        JSONObject generalSettings = (JSONObject) Config.data.get("generalSettings");
        if(generalSettings == null || !generalSettings.containsKey("saveLocation")) return "";
        return (String) generalSettings.get("saveLocation");
    }

    public static boolean getRemoveStyling() {
        JSONObject generalSettings = (JSONObject) Config.data.get("generalSettings");
        if(generalSettings == null || !generalSettings.containsKey("removeStyling")) return false;
        return (boolean) generalSettings.get("removeStyling");
    }

    public static boolean getUseStandardLocation() {
        JSONObject generalSettings = (JSONObject) Config.data.get("generalSettings");
        if(generalSettings == null || !generalSettings.containsKey("useStandardLocation")) return false;
        return (boolean) generalSettings.get("useStandardLocation");
    }

    public static void setHeadlessSettings(String newBrowser, boolean showGUI) {
        JSONObject headlessDetails = new JSONObject();
        headlessDetails.put("browser", newBrowser);
        headlessDetails.put("showBrowserGUI", showGUI);
        Config.data.put("headless", headlessDetails);
        Config.saveConfig();
    }

    public static void setGeneralSettings(boolean getImages, boolean removeStyling, int formatNumber, String saveLocation, boolean useStandardLocation) {
        JSONObject generalSettings = new JSONObject();
        generalSettings.put("autoGetImages", getImages);
        generalSettings.put("removeStyling", removeStyling);
        generalSettings.put("filenameFormat", formatNumber);
        generalSettings.put("saveLocation", saveLocation);
        generalSettings.put("useStandardLocation", useStandardLocation);
        Config.data.put("generalSettings", generalSettings);
        Config.saveConfig();
    }
}
