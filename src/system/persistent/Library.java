package system.persistent;

import grabber.Novel;
import org.json.simple.JSONObject;
import system.Config;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Library {
    public static int getLastChapter(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if (library == null || !library.containsKey(novelUrl)) return 0;
        return Integer.parseInt(String.valueOf(((JSONObject) library.get(novelUrl)).get("lastChapter")));
    }

    public static int getNewestChapter(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if (library == null || !library.containsKey(novelUrl)) return 0;
        if(((JSONObject) library.get(novelUrl)).get("newestChapter") == null) return getLastChapter(novelUrl);
        return Integer.parseInt(String.valueOf(((JSONObject) library.get(novelUrl)).get("newestChapter")));
    }

    public static int getThreshold(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if (library == null || !library.containsKey(novelUrl)) return 0;
        if(((JSONObject) library.get(novelUrl)).get("threshold") == null) return 0;
        return Integer.parseInt(String.valueOf(((JSONObject) library.get(novelUrl)).get("threshold")));
    }

    public static String getNovelTitle(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if (library == null || !library.containsKey(novelUrl)) return "";
        return (String) ((JSONObject) library.get(novelUrl)).get("title");

    }

    public static String getHost(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if (library == null || !library.containsKey(novelUrl)) return "";
        return (String) ((JSONObject) library.get(novelUrl)).get("host");
    }

    public static boolean useHeadless(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if(library == null || ((JSONObject) library.get(novelUrl)).get("headless") == null) return false;
        return (boolean) ((JSONObject) library.get(novelUrl)).get("headless");
    }
    public static boolean useAccount(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if(library == null || ((JSONObject) library.get(novelUrl)).get("useAccount") == null) return false;
        return (boolean) ((JSONObject) library.get(novelUrl)).get("useAccount");
    }

    public static String getBookCover(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if (library == null || !library.containsKey(novelUrl)) return "";
        return (String) ((JSONObject) library.get(novelUrl)).get("cover");
    }

    public static String getSavelocation(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if (library == null || !library.containsKey(novelUrl)) return "";
        return (String) ((JSONObject) library.get(novelUrl)).get("saveLocation");
    }

    public static String getCLICommand(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if (library == null || !library.containsKey(novelUrl)) return "";
        return (String) ((JSONObject) library.get(novelUrl)).get("cliCommand");
    }

    public static boolean isStarred(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if(library == null || !library.containsKey(novelUrl)) return false;
        return true;
    }

    public static boolean getAutoDownload(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if(library == null || ((JSONObject) library.get(novelUrl)).get("autoDownload") == null) return false;
        return (boolean) ((JSONObject) library.get(novelUrl)).get("autoDownload");
    }


    public static boolean getPolling() {
        JSONObject librarySettings = (JSONObject) Config.data.get("librarySettings");
        if(librarySettings == null || librarySettings.get("pollingEnabled") == null) return true;
        return (boolean) librarySettings.get("pollingEnabled");
    }

    public static boolean getUpdateLast() {
        JSONObject librarySettings = (JSONObject) Config.data.get("librarySettings");
        if(librarySettings == null || librarySettings.get("updateLast") == null) return false;
        return (boolean) librarySettings.get("updateLast");
    }

    public static int getFrequency() {
        JSONObject librarySettings = (JSONObject) Config.data.get("librarySettings");
        if(librarySettings == null || librarySettings.get("frequency") == null) return 20;
        return Integer.parseInt(String.valueOf(librarySettings.get("frequency")));
    }

    public static List<String> getLibrary() {
        JSONObject library = (JSONObject) Config.data.get("library");
        if(library == null || library.isEmpty()) return new ArrayList<>();
        Set<String> keys = library.keySet();
        return new ArrayList<>(keys);
    }

    public static void removeStarred(String novelUrl) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if(library == null) library = new JSONObject();
        library.remove(novelUrl);
        Config.data.put("library", library);
        Config.saveConfig();
    }


    public static void setNewestChapter(String novelUrl, int chapterNumber) {
        JSONObject library = (JSONObject) Config.data.get("library");
        JSONObject novel = (JSONObject) library.get(novelUrl);
        novel.put("newestChapter", chapterNumber);
        library.put(novelUrl, novel);
        Config.data.put("library", library);
        Config.saveConfig();
    }

    public static void setLastChapter(String novelUrl, int chapterNumber) {
        JSONObject library = (JSONObject) Config.data.get("library");
        JSONObject novel = (JSONObject) library.get(novelUrl);
        novel.put("lastChapter", chapterNumber);
        library.put(novelUrl, novel);
        Config.data.put("library", library);
        Config.saveConfig();
    }

    public static void setThreshold(String novelUrl, int amountChapters) {
        JSONObject library = (JSONObject) Config.data.get("library");
        JSONObject novel = (JSONObject) library.get(novelUrl);
        novel.put("threshold", amountChapters);
        library.put(novelUrl, novel);
        Config.data.put("library", library);
        Config.saveConfig();
    }

    public static void setAutoDownload(String novelUrl, boolean useAuto) {
        JSONObject library = (JSONObject) Config.data.get("library");
        JSONObject novel = (JSONObject) library.get(novelUrl);
        novel.put("autoDownload", useAuto);
        library.put(novelUrl, novel);
        Config.data.put("library", library);
        Config.saveConfig();
    }

    public static void setPolling(boolean pollingEnabled) {
        JSONObject librarySettings = (JSONObject) Config.data.get("librarySettings");
        if(librarySettings == null) librarySettings = new JSONObject();
        librarySettings.put("pollingEnabled", pollingEnabled);
        Config.data.put("librarySettings", librarySettings);
        Config.saveConfig();
    }

    public static void setUpdateLast(boolean updateLast) {
        JSONObject librarySettings = (JSONObject) Config.data.get("librarySettings");
        if(librarySettings == null) librarySettings = new JSONObject();
        librarySettings.put("updateLast", updateLast);
        Config.data.put("librarySettings", librarySettings);
        Config.saveConfig();
    }

    public static void setFrequency(int frequency) {
        JSONObject librarySettings = (JSONObject) Config.data.get("librarySettings");
        if(librarySettings == null) librarySettings = new JSONObject();
        librarySettings.put("frequency", frequency);
        Config.data.put("librarySettings", librarySettings);
        Config.saveConfig();
    }

    public static void setCLICommand(String novelUrl, String cliCommand) {
        JSONObject library = (JSONObject) Config.data.get("library");
        JSONObject novel = (JSONObject) library.get(novelUrl);
        novel.put("cliCommand", cliCommand);
        library.put(novelUrl, novel);
        Config.data.put("library", library);
        Config.saveConfig();
    }


    public static void setStarred(Novel novel) {
        JSONObject library = (JSONObject) Config.data.get("library");
        if(library == null) library = new JSONObject();
        JSONObject newNovel = (JSONObject) library.get(novel.novelLink);
        if(newNovel == null) newNovel = new JSONObject();
        newNovel.put("title", novel.metadata.bookTitle);
        newNovel.put("author", novel.metadata.bookAuthor);
        newNovel.put("cover", novel.metadata.bookCover);
        newNovel.put("lastChapter", novel.chapters.size());
        newNovel.put("headless", novel.options.headless);
        newNovel.put("host", novel.options.hostname);
        newNovel.put("useAccount", novel.options.useAccount);
        newNovel.put("saveLocation", Config.home_path+ "/" + Config.home_folder + "/"+novel.metadata.bookTitle+"/");
        String cliCommand = "-link "+novel.novelLink +" -path "+(Config.home_path+ "/" + Config.home_folder + "/"+novel.metadata.bookTitle+"/").replaceAll(" ","-");
        if(novel.options.headless) {
            cliCommand = cliCommand+ " -headless " +Settings.getBrowser();
        }
        if(novel.options.useAccount) {
            cliCommand = cliCommand+ " -login -account " +Accounts.getUsername(novel.options.hostname) + " "+Accounts.getPassword(novel.options.hostname);
        }
        newNovel.put("cliCommand", cliCommand);

        library.put(novel.novelLink, newNovel);
        Config.data.put("library", library);
        File outputfile = new File((Config.home_path+ "/" + Config.home_folder + "/"+novel.metadata.bookTitle+"/").replaceAll(" ","-")+novel.metadata.bookCover);
        outputfile.mkdirs();
        try {
            ImageIO.write(novel.metadata.bufferedCover, novel.metadata.bookCover.substring(novel.metadata.bookCover.lastIndexOf(".")+1), outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Config.saveConfig();
    }

}
