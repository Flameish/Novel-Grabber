package library;

import grabber.Novel;
import grabber.NovelMetadata;
import org.json.simple.JSONObject;

/**
 * Followed novel in library.
 */
public class LibraryNovel extends Novel {
    private String lastChapterName;
    private String newestChapterName;
    private boolean autoDownloadEnabled;
    private boolean sendEmailNotification;
    private boolean sendDesktopNotification;
    private boolean sendAttachmentEnabled;
    private boolean updateLast;
    private boolean checkingActive = true;
    private int lastChapterNumber;
    private int newestChapterNumber;
    private int threshold;

    LibraryNovel() {

    }

    /**
     * Creates a library novel from a JSON object.
     * Doesn't write cover to file.
     * This method is called for already existing novels.
     */
    public LibraryNovel(JSONObject libNovel) {
        novelLink = (String) libNovel.get("novelUrl");
        saveLocation = (String) libNovel.get("saveLocation");
        lastChapterName = (String) libNovel.get("lastChapterName");
        newestChapterName = (String) libNovel.get("newestChapterName");
        autoDownloadEnabled = (boolean) libNovel.get("autoDownloadEnabled");
        sendEmailNotification = (boolean) libNovel.get("sendEmailNotification");
        sendDesktopNotification = (boolean) libNovel.get("sendDesktopNotification");
        sendAttachmentEnabled = (boolean) libNovel.get("sendAttachmentEnabled");
        updateLast = (boolean) libNovel.get("updateLast");
        useAccount = (boolean) libNovel.getOrDefault("useAccount", isUseAccount());
        getImages = (boolean) libNovel.getOrDefault("getImages", isGetImages());
        displayChapterTitle = (boolean) libNovel.getOrDefault("displayChapterTitle", isDisplayChapterTitle());
        checkingActive = (boolean) libNovel.getOrDefault("checkingActive", isCheckingActive());
        waitTime = (((Long) libNovel.getOrDefault("threshold", 0)).intValue());
        lastChapterNumber = (((Long) libNovel.get("lastChapter")).intValue());
        newestChapterNumber = (((Long) libNovel.get("newestChapter")).intValue());
        threshold = (((Long) libNovel.get("threshold")).intValue());

        metadata = new NovelMetadata();
        metadata.setTitle((String) libNovel.get("title"));
        metadata.setCoverFormat((String) libNovel.get("coverFormat"));
    }

    /**
     * Returns this library novel as a JSON Object.
     */
    public JSONObject getAsJSONObject() {
        JSONObject libraryNovel = new JSONObject();
        libraryNovel.put("novelUrl", getNovelUrl());
        libraryNovel.put("saveLocation", getSaveLocation());
        libraryNovel.put("title", metadata.getTitle());
        libraryNovel.put("coverFormat", metadata.getCoverFormat());
        libraryNovel.put("lastChapterName", getLastChapterName());
        libraryNovel.put("newestChapterName", getNewestChapterName());
        libraryNovel.put("autoDownloadEnabled", isAutoDownloadEnabled());
        libraryNovel.put("sendEmailNotification", isSendEmailNotification());
        libraryNovel.put("sendDesktopNotification", isSendDesktopNotification());
        libraryNovel.put("sendAttachmentEnabled", isSendAttachmentEnabled());
        libraryNovel.put("updateLast", isUpdateLast());
        libraryNovel.put("lastChapter", getLastLocalChapterNumber());
        libraryNovel.put("newestChapter", getNewestChapterNumber());
        libraryNovel.put("threshold", getThreshold());
        libraryNovel.put("checkingActive", isCheckingActive());
        libraryNovel.put("useAccount", isUseAccount());
        libraryNovel.put("getImages", isGetImages());
        libraryNovel.put("displayChapterTitle", isDisplayChapterTitle());
        libraryNovel.put("waitTime", getWaitTime());
        return libraryNovel;
    }


    public String getNovelUrl() {
        return novelLink;
    }

    public void setNovelUrl(String novelUrl) {
        this.novelLink = novelUrl;
    }

    public NovelMetadata getMetadata() {
        return metadata;
    }

    public boolean isAutoDownloadEnabled() {
        return autoDownloadEnabled;
    }

    public void setAutoDownloadEnabled(boolean autoDownloadEnabled) {
        this.autoDownloadEnabled = autoDownloadEnabled;
    }

    public int getLastLocalChapterNumber() {
        return lastChapterNumber;
    }

    public void setLastChapterNumber(int lastChapterNumber) {
        this.lastChapterNumber = lastChapterNumber;
    }

    public int getNewestChapterNumber() {
        return newestChapterNumber;
    }

    public void setNewestChapterNumber(int newestChapterNumber) {
        this.newestChapterNumber = newestChapterNumber;
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

    public String getLastChapterName() {
        if(lastChapterName == null) return String.valueOf(lastChapterNumber);
        return lastChapterName;
    }

    public void setLastChapterName(String lastChapterName) {
        this.lastChapterName = lastChapterName;
    }

    public String getNewestChapterName() {
        if(newestChapterName == null) return String.valueOf(newestChapterNumber);
        return newestChapterName;
    }

    public void setNewestChapterName(String newestChapterName) {
        this.newestChapterName = newestChapterName;
    }

    public boolean notificationsEnabled() {
        return (isSendDesktopNotification() || isSendEmailNotification());
    }

    public String getSaveLocation() {
        return saveLocation;
    }

    public boolean isUseAccount() {
        return useAccount;
    }

    public void setUseAccount(boolean useAccount) {
        this.useAccount = useAccount;
    }

    public void setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;
    }

    public boolean isCheckingActive() {
        return checkingActive;
    }

    public void setCheckingActive(boolean checkingActive) {
        this.checkingActive = checkingActive;
    }

    public boolean isDisplayChapterTitle() {
        return displayChapterTitle;
    }

    public void setDisplayChapterTitle(boolean displayChapterTitle) {
        this.displayChapterTitle = displayChapterTitle;
    }

    public boolean isGetImages() {
        return getImages;
    }

    public void setGetImages(boolean getImages) {
        this.getImages = getImages;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
}
