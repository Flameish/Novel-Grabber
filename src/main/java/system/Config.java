package system;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Stores config data.
 */
public class Config {
    private static Config config;
    public JSONObject siteSelectorsJSON;
    public String home_path = System.getProperty("user.home");
    public String home_folder = "Novel-Grabber";
    public String JSON_Link = "https://raw.githubusercontent.com/Flameish/Novel-Grabber/master/src/main/resources/siteSelector.json";
    public String settings_file_path = home_path + "/" + home_folder + "/" + "settings.json";
    public String library_file_path = home_path + "/" + home_folder + "/" + "library.json";
    public String accounts_file_path = home_path + "/" + home_folder + "/" + "accounts.json";
    public String emailConfig_file_path = home_path + "/" + home_folder + "/" + "emailConfig.json";

    private Config() {
        checkConfigFolder();
        fetchSelectors();
    }

    public static Config getInstance() {
        if(config == null) {
            config = new Config();
        }
        return config;
    }

    /**
     * Checks if config folder and file exist.
     * If not then new ones will be created.
     */
    private void checkConfigFolder()  {
        Path path = Paths.get(home_path);
        if (Files.isDirectory(path) && Files.exists(path)) {
            Path myFolder = path.resolve(home_folder);
            if (Files.notExists(myFolder)) {
                try {
                    System.out.println("[CONFIG]Creating new folders...");
                    Files.createDirectory(myFolder);
                } catch (IOException e) {
                    System.out.println("[CONFIG - ERROR]Could not create folders.");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Fetches host selectors and creates JSON object.
     */
    private void fetchSelectors() {
        try {
            System.out.println("[CONFIG]Fetching latest selector JSON...");
            Document doc = Jsoup.connect(JSON_Link).timeout(30 * 1000).get();
            String JSONString = doc.select("body").first().text();
            Object obj = new JSONParser().parse(JSONString);
            siteSelectorsJSON = (JSONObject) obj;
        } catch (ParseException | IOException e) {
            System.out.println("[CONFIG]Could not fetch selectors from: " + JSON_Link);
            e.printStackTrace();
        }
    }
}
