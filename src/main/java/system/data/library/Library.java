package system.data.library;
import grabber.Novel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import system.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private static Library library;
    private List<LibraryNovel> starredNovels = new ArrayList<>();
    private int frequency = 20;
    private boolean pollingEnabled = true;

    private Library() { }

    public static Library getInstance() {
        if(library == null) {
            library = new Library();
            library.load();
        }
        return library;
    }

    /**
     * Reads system.library file(JSON) and creates Library object.
     */
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Config.getInstance().library_file_path))) {
            JSONObject libraryObj = (JSONObject) new JSONParser().parse(reader);
            setFrequency(((Long) libraryObj.get("frequency")).intValue());
            setPollingEnabled((boolean) libraryObj.get("pollingEnabled"));
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
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(Config.getInstance().library_file_path))) {
            JSONObject libraryObj = new JSONObject();
            libraryObj.put("frequency", getFrequency());
            libraryObj.put("pollingEnabled", isPollingEnabled());
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

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isPollingEnabled() {
        return pollingEnabled;
    }

    public void setPollingEnabled(boolean pollingEnabled) {
        this.pollingEnabled = pollingEnabled;
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
