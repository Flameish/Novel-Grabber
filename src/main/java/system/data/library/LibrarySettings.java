package system.data.library;
import grabber.Novel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import system.init;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class LibrarySettings {
    private static String libraryFile;
    public static String libraryFolder;
    private static LibrarySettings librarySettings;
    private List<LibraryNovel> starredNovels = new ArrayList<>();

    private LibrarySettings() { }

    public static LibrarySettings getInstance() {
        if(librarySettings == null) {
            librarySettings = new LibrarySettings();
            try {
                libraryFile = new File(init.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + "/../library.json";
                libraryFolder = new File(init.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + "/../Novels";
            } catch (URISyntaxException e) {
                libraryFile = "library.json";
                libraryFolder = "Novels";
                e.printStackTrace();
            }
            librarySettings.load();
        }
        return librarySettings;
    }

    /**
     * Reads system.library file(JSON) and creates Library object.
     */
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(libraryFile))) {
            JSONObject libraryObj = (JSONObject) new JSONParser().parse(reader);
            // Create starred novels from json objects
            JSONArray libraryNovels = (JSONArray) libraryObj.get("starredNovels");
            for (Object loadedNovel: libraryNovels) {
                starredNovels.add(new LibraryNovel((JSONObject) loadedNovel));
            }
        } catch (IOException e) {
            System.out.println("[LIBRARY]No file found.");
        } catch (ParseException e) {
            System.out.println("[LIBRARY]Could not parse file.");
        }
    }

    /**
     * Saves system.library as JSON file.
     */
    public void save() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(libraryFile))) {
            JSONObject libraryObj = new JSONObject();
            // Create JSON array from starred novels
            JSONArray libraryNovels = new JSONArray();
            for(LibraryNovel libraryNovel: starredNovels) {
                libraryNovels.add(libraryNovel.getAsJSONObject());
            }
            libraryObj.put("starredNovels", libraryNovels);

            writer.write(libraryObj.toJSONString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public List<LibraryNovel> getStarredNovels() {
        return starredNovels;
    }

    public LibraryNovel getNovel(String novelUrl) {
        for(LibraryNovel currNovel: starredNovels) {
            if (currNovel.getNovelUrl().equals(novelUrl)) return currNovel;
        }
        return null;
    }

    public boolean isStarred(String novelUrl) {
        for(LibraryNovel currNovel: starredNovels) {
            if (currNovel.getNovelUrl().equals(novelUrl)) return true;
        }
        return false;
    }

    public void removeStarred(String novelUrl) {
        starredNovels.removeIf(currNovel -> currNovel.getNovelUrl().equals(novelUrl));
    }

    public void setStarred(Novel novel) {
        starredNovels.add(new LibraryNovel(novel));
    }
}
