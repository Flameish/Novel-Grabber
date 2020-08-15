package system.data.library;

import grabber.Novel;
import org.json.simple.JSONObject;
import system.Config;
import system.data.Settings;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Followed novel in system.library.
 */
public class LibraryNovel {
    private String novelUrl;
    private String title;
    private String cover;
    private boolean autoDownloadEnabled;
    private boolean sendEmailNotification;
    private boolean sendDesktopNotification;
    private boolean sendAttachmentEnabled;
    private boolean updateLast;
    private String cliString;
    private int lastChapter;
    private int newestChapter;
    private int threshold;

    /**
     * Creates a new system.library novel.
     * Writes the cover to file.
     * @param novel
     */
    public LibraryNovel(Novel novel) {
        novelUrl = novel.novelLink;
        title = novel.bookTitle;
        cover = novel.bookCover;
        lastChapter = novel.chapterList.size();

        // Build cli command
        String tempCli = "-link "+novel.novelLink +" -path \""+ Config.getInstance().home_path+ "/"
                + Config.getInstance().home_folder + "/"+novel.bookTitle+"/\"";
        if(novel.useHeadless) {
            tempCli = tempCli+ " -headless " + Settings.getInstance().getBrowser();
        }
        if(novel.useAccount) {
            tempCli = tempCli+ " -login";
        }
        cliString = tempCli;

        saveCover(novel);
    }


    /**
     * Creates a library novel from a JSON object.
     * Doesn't write cover to file.
     * This method is called for already existing novels.
     * @param libNovel
     */
    public LibraryNovel(JSONObject libNovel) {
        novelUrl = (String) libNovel.get("novelUrl");
        title = (String) libNovel.get("title");
        cover = (String) libNovel.get("cover");
        cliString = (String) libNovel.get("cliString");
        autoDownloadEnabled = (boolean) libNovel.get("autoDownloadEnabled");
        sendEmailNotification = (boolean) libNovel.get("sendEmailNotification");
        sendDesktopNotification = (boolean) libNovel.get("sendDesktopNotification");
        sendAttachmentEnabled = (boolean) libNovel.get("sendAttachmentEnabled");
        updateLast = (boolean) libNovel.get("updateLast");
        lastChapter = (((Long) libNovel.get("lastChapter")).intValue());
        newestChapter = (((Long) libNovel.get("newestChapter")).intValue());
        threshold = (((Long) libNovel.get("threshold")).intValue());
    }

    /**
     *Returns this system.library novel as a JSON Object.
     */
    public JSONObject getAsJSONObject() {
        JSONObject libraryNovel = new JSONObject();
        libraryNovel.put("novelUrl", getNovelUrl());
        libraryNovel.put("title", getTitle());
        libraryNovel.put("cover", getCover());
        libraryNovel.put("cliString", getCliString());
        libraryNovel.put("autoDownloadEnabled", isAutoDownloadEnabled());
        libraryNovel.put("sendEmailNotification", isSendEmailNotification());
        libraryNovel.put("sendDesktopNotification", isSendDesktopNotification());
        libraryNovel.put("sendAttachmentEnabled", isSendAttachmentEnabled());
        libraryNovel.put("updateLast", isUpdateLast());
        libraryNovel.put("lastChapter", getLastChapter());
        libraryNovel.put("newestChapter", getNewestChapter());
        libraryNovel.put("threshold", getThreshold());
        return libraryNovel;
    }

    /**
     * Writes BufferedCover to file.
     * @param novel
     */
    private void saveCover(Novel novel) {
        // Save cover
        File outputfile = new File(Config.getInstance().home_path+ "/" + Config.getInstance().home_folder
                + "/"+ novel.bookTitle+"/"+ novel.bookCover);
        outputfile.mkdirs();
        try {
            System.out.println(novel.bookCover);
            // cover name + file extension
            ImageIO.write(novel.bufferedCover, novel.bookCover.substring(novel.bookCover.lastIndexOf(".")+1), outputfile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ERROR]Could not save cover.");
        }
    }

    public String getNovelUrl() {
        return novelUrl;
    }

    public void setNovelUrl(String novelUrl) {
        this.novelUrl = novelUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isAutoDownloadEnabled() {
        return autoDownloadEnabled;
    }

    public void setAutoDownloadEnabled(boolean autoDownloadEnabled) {
        this.autoDownloadEnabled = autoDownloadEnabled;
    }

    public String getCliString() {
        return cliString;
    }

    public void setCliString(String cliString) {
        this.cliString = cliString;
    }

    public int getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(int lastChapter) {
        this.lastChapter = lastChapter;
    }

    public int getNewestChapter() {
        return newestChapter;
    }

    public void setNewestChapter(int newestChapter) {
        this.newestChapter = newestChapter;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isSendDesktopNotification() {
        return sendDesktopNotification;
    }

    public void setSendDesktopNotification(boolean sendDesktopNotification) {
        this.sendDesktopNotification = sendDesktopNotification;
    }

    public boolean isSendEmailNotification() {
        return sendEmailNotification;
    }

    public void setSendEmailNotification(boolean sendEmailNotification) {
        this.sendEmailNotification = sendEmailNotification;
    }

    public boolean isSendAttachmentEnabled() {
        return sendAttachmentEnabled;
    }

    public void setSendAttachmentEnabled(boolean sendAttachmentEnabled) {
        this.sendAttachmentEnabled = sendAttachmentEnabled;
    }

    public boolean isUpdateLast() {
        return updateLast;
    }

    public void setUpdateLast(boolean updateLast) {
        this.updateLast = updateLast;
    }

    public boolean notificationsEnabled() {
        return (isSendDesktopNotification() || isSendEmailNotification());
    }
}
