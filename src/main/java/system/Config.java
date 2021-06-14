package system;

import grabber.GrabberUtils;

import java.io.*;
import java.util.*;

/**
 * Configuration class of persistent variables
 */
public class Config {
    private static Config config;
    private final static String configFile = GrabberUtils.getCurrentPath() + "/config.ini";
    private List<String> headlessList = new ArrayList<>();
    private List<String> telegramAdminIds = new ArrayList<>();
    private String browser = "";
    private String saveLocation = "";
    private String username = "";
    private String password = "";
    private String host = "";
    private String receiverEmail = "";
    private String ssl = "SMTP";
    private String telegramApiToken = "";
    private String fontName = "DejaVu Sans";
    private String chapterTitleTemplate = "%s";
    private int telegramWait = 0;
    private int telegramNovelMaxChapter = -1;
    private int telegramMaxChapterPerDay = -1;
    private int filenameFormat = 0;
    private int outputFormat = 0;
    private int port = 25;
    private int frequency = 20;
    private int telegramDownloadLimit = 1;
    private int guiTheme = 0;
    private int chapterTitleFormat = 0;
    private boolean autoGetImages = false;
    private boolean useStandardLocation = false;
    private boolean pollingEnabled = true;
    private boolean separateChapters = false;
    private boolean libraryShowOnlyUpdatable = false;
    private boolean libraryNoCovers = false;
    private boolean showNovelFinishedNotification = false;
    private boolean telegramImagesAllowed = true;

    private Config() { }

    public static Config getInstance() {
        if(config == null) {
            config = new Config();
            config.load();
        }
        return config;
    }

    /**
     * Reads config from file.
     */
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            Properties prop = new Properties();
            prop.load(reader);
            //  Use default value from field if property is not found in file
            // General
            setGuiTheme(Integer.parseInt(prop.getProperty("guiTheme", String.valueOf(guiTheme))));
            setFontName(prop.getProperty("fontName", fontName));
            setChapterTitleFormat(Integer.parseInt(prop.getProperty("chapterTitleFormat", String.valueOf(chapterTitleFormat))));
            setChapterTitleTemplate(prop.getProperty("chapterTitleTemplate", chapterTitleTemplate));
            // Email
            setUsername(prop.getProperty("username", username));
            setPassword(prop.getProperty("password", password));
            setReceiverEmail(prop.getProperty("receiverEmail", receiverEmail));
            setHost(prop.getProperty("host", host));
            setSsl(prop.getProperty("ssl", ssl));
            setPort(Integer.parseInt(prop.getProperty("port", String.valueOf(port))));
            // Grabber
            setBrowser(prop.getProperty("browser", browser));
            setFilenameFormat(Integer.parseInt(prop.getProperty("filenameFormat", String.valueOf(filenameFormat))));
            setOutputFormat(Integer.parseInt(prop.getProperty("outputFormat", String.valueOf(outputFormat))));
            setAutoGetImages(Boolean.parseBoolean(prop.getProperty("autoGetImages", String.valueOf(autoGetImages))));
            setSaveLocation(prop.getProperty("saveLocation", saveLocation));
            setUseStandardLocation(Boolean.parseBoolean(prop.getProperty("useStandardLocation", String.valueOf(useStandardLocation))));
            setHeadlessList(new ArrayList<>(Arrays.asList(prop.getProperty("headlessList", "").split(","))));
            setSeparateChapters(Boolean.parseBoolean(prop.getProperty("separateChapters", String.valueOf(separateChapters))));
            setShowNovelFinishedNotification(Boolean.parseBoolean(prop.getProperty("showNovelFinishedNotification", String.valueOf(showNovelFinishedNotification))));
            // Library
            setFrequency(Integer.parseInt(prop.getProperty("frequency", String.valueOf(frequency))));
            setPollingEnabled(Boolean.parseBoolean(prop.getProperty("pollingEnabled", String.valueOf(pollingEnabled))));
            setLibraryShowOnlyUpdatable(Boolean.parseBoolean(prop.getProperty("libraryShowOnlyUpdatable", String.valueOf(libraryShowOnlyUpdatable))));
            setLibraryNoCovers(Boolean.parseBoolean(prop.getProperty("libraryNoCovers", String.valueOf(libraryNoCovers))));
            // Telegram
            setTelegramApiToken(prop.getProperty("telegramApiToken", telegramApiToken));
            setTelegramWait(Integer.parseInt(prop.getProperty("telegramWait", String.valueOf(telegramWait))));
            setTelegramNovelMaxChapter(Integer.parseInt(prop.getProperty("telegramNovelMaxChapter", String.valueOf(telegramNovelMaxChapter))));
            setTelegramMaxChapterPerDay(Integer.parseInt(prop.getProperty("telegramMaxChapterPerDay", String.valueOf(telegramMaxChapterPerDay))));
            setTelegramAdminIds(new ArrayList<>(Arrays.asList(prop.getProperty("telegramAdminIds", "").split(","))));
            setTelegramDownloadLimit(Integer.parseInt(prop.getProperty("telegramDownloadLimit", String.valueOf(telegramDownloadLimit))));
            setTelegramImagesAllowed(Boolean.parseBoolean(prop.getProperty("telegramImagesAllowed", String.valueOf(telegramImagesAllowed))));
        } catch (IOException e) {
            GrabberUtils.err("No settings file found.");
        }
    }

    /**
     * Saves config to file.
     */
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            Properties prop = new Properties();
            // General
            prop.setProperty("guiTheme", String.valueOf(getGuiTheme()));
            prop.setProperty("fontName", getFontName());
            prop.setProperty("chapterTitleFormat", String.valueOf(getChapterTitleFormat()));
            prop.setProperty("chapterTitleTemplate", getChapterTitleTemplate());
            // Email
            prop.setProperty("username", getUsername());
            prop.setProperty("password", getPassword());
            prop.setProperty("receiverEmail", getReceiverEmail());
            prop.setProperty("host", getHost());
            prop.setProperty("ssl", getSsl());
            prop.setProperty("port", String.valueOf(getPort()));
            // Grabber
            prop.setProperty("browser", getBrowser());
            prop.setProperty("filenameFormat", String.valueOf(getFilenameFormat()));
            prop.setProperty("outputFormat", String.valueOf(getOutputFormat()));
            prop.setProperty("autoGetImages", String.valueOf(isAutoGetImages()));
            prop.setProperty("saveLocation", getSaveLocation());
            prop.setProperty("useStandardLocation", String.valueOf(isUseStandardLocation()));
            prop.setProperty("headlessList", String.join(",", headlessList));
            prop.setProperty("separateChapters", String.valueOf(isSeparateChapters()));
            prop.setProperty("showNovelFinishedNotification", String.valueOf(isShowNovelFinishedNotification()));
            // Library
            prop.setProperty("frequency", String.valueOf(getFrequency()));
            prop.setProperty("pollingEnabled", String.valueOf(isPollingEnabled()));
            prop.setProperty("libraryShowOnlyUpdatable", String.valueOf(isLibraryShowOnlyUpdatable()));
            prop.setProperty("libraryNoCovers", String.valueOf(isLibraryNoCovers()));
            // Telegram
            prop.setProperty("telegramApiToken", String.valueOf(getTelegramApiToken()));
            prop.setProperty("telegramWait", String.valueOf(getTelegramWait()));
            prop.setProperty("telegramNovelMaxChapter", String.valueOf(getTelegramNovelMaxChapter()));
            prop.setProperty("telegramMaxChapterPerDay", String.valueOf(getTelegramMaxChapterPerDay()));
            prop.setProperty("telegramAdminIds", String.join(",", telegramAdminIds));
            prop.setProperty("telegramDownloadLimit", String.valueOf(getTelegramDownloadLimit()));
            prop.setProperty("telegramImagesAllowed", String.valueOf(isTelegramImagesAllowed()));

            prop.store(writer, "Novel-Grabber version: " + init.versionNumber);
        } catch (IOException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
    }

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
    public int getTelegramWait() {
        return telegramWait;
    }
    public boolean isPollingEnabled() {
        return pollingEnabled;
    }
    public boolean isAutoGetImages() {
        return autoGetImages;
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
    public int getTelegramNovelMaxChapter() {
        return telegramNovelMaxChapter;
    }
    public int getTelegramMaxChapterPerDay() {
        return telegramMaxChapterPerDay;
    }
    public List<String> getTelegramAdminIds() {
        return telegramAdminIds;
    }
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
    public void setTelegramWait(int telegramWait) {
        this.telegramWait = telegramWait;
    }
    public void setPollingEnabled(boolean pollingEnabled) {
        this.pollingEnabled = pollingEnabled;
    }
    public void setAutoGetImages(boolean autoGetImages) {
        this.autoGetImages = autoGetImages;
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
    public void setTelegramNovelMaxChapter(int telegramNovelMaxChapter) {
        this.telegramNovelMaxChapter = telegramNovelMaxChapter;
    }
    public void setTelegramMaxChapterPerDay(int telegramMaxChapterPerDay) {
        this.telegramMaxChapterPerDay = telegramMaxChapterPerDay;
    }
    public void setTelegramAdminIds(List<String> telegramAdminIds) {
        this.telegramAdminIds = telegramAdminIds;
    }

    public boolean isSeparateChapters() {
        return separateChapters;
    }

    public void setSeparateChapters(boolean separateChapters) {
        this.separateChapters = separateChapters;
    }

    public boolean isLibraryShowOnlyUpdatable() {
        return libraryShowOnlyUpdatable;
    }

    public boolean isLibraryNoCovers() {
        return libraryNoCovers;
    }

    public void setLibraryNoCovers(boolean libraryNoCovers) {
        this.libraryNoCovers = libraryNoCovers;
    }

    public void setLibraryShowOnlyUpdatable(boolean libraryShowOnlyUpdatable) {
        this.libraryShowOnlyUpdatable = libraryShowOnlyUpdatable;
    }

    public String getChapterTitleTemplate() {
        return chapterTitleTemplate;
    }

    public void setChapterTitleTemplate(String chapterTitleTemplate) {
        if (chapterTitleTemplate.isEmpty()) chapterTitleTemplate = "%s";
        if (!chapterTitleTemplate.contains("%s")) chapterTitleTemplate = "%s";
        this.chapterTitleTemplate = chapterTitleTemplate;
    }

    public int getChapterTitleFormat() {
        if (chapterTitleFormat < 0) chapterTitleFormat = 0;
        return chapterTitleFormat;
    }

    public void setChapterTitleFormat(int chapterTitleFormat) {
        this.chapterTitleFormat = chapterTitleFormat;
    }

    public boolean isShowNovelFinishedNotification() {
        return showNovelFinishedNotification;
    }

    public void setShowNovelFinishedNotification(boolean showNovelFinishedNotification) {
        this.showNovelFinishedNotification = showNovelFinishedNotification;
    }

    public int getTelegramDownloadLimit() {
        return telegramDownloadLimit;
    }

    public void setTelegramDownloadLimit(int telegramDownloadLimit) {
        this.telegramDownloadLimit = telegramDownloadLimit;
    }

    public int getGuiTheme() {
        return guiTheme;
    }

    public void setGuiTheme(int guiTheme) {
        this.guiTheme = guiTheme;
    }

    public boolean isTelegramImagesAllowed() {
        return telegramImagesAllowed;
    }

    public void setTelegramImagesAllowed(boolean telegramImagesAllowed) {
        this.telegramImagesAllowed = telegramImagesAllowed;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
