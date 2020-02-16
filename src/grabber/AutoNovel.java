package grabber;

import gui.GUI;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoNovel {
    private static WebDriver driver;
    static WebDriverWait wait;
    public List<String> chapterLinks = new ArrayList<>();
    public HostSettings currHostSettings;
    public BufferedImage bufferedCover;
    public String bufferedCoverName;
    public GUI gui;
    public boolean killTask = false;
    public boolean noDescription = false;
    public List<String> bookDesc = new ArrayList<>();
    public boolean autoChapterToChapter;
    public String bookTitle;
    public String bookAuthor;
    public List<String> bookSubjects = new ArrayList<>();
    public String bookCover;
    List<String> failedChapters = new ArrayList<>();
    List<String> successfulChapterNames = new ArrayList<>();
    List<String> successfulFilenames = new ArrayList<>();
    List<String> imageLinks = new ArrayList<>();
    List<String> imageNames = new ArrayList<>();
    List<String> blacklistedTags;
    String export;
    String window;
    String saveLocation;
    String tocFileName;
    boolean getImages;
    boolean invertOrder;
    boolean noStyling;
    List<String> successfulExtraPagesNames = new ArrayList<>();
    int waitTime;
    List<String> successfulExtraPagesFilenames = new ArrayList<>();
    String nextChapterURL;
    String nextChapterBtn = "NOT_SET";
    boolean displayChapterTitle;
    int wordCount = 0;
    private Document tocDoc;
    private List<String> chaptersNames = new ArrayList<>();
    private List<String> xhrChapterIds = new ArrayList<>();
    private boolean allChapters;
    private int xhrBookId;
    private int firstChapter;
    private int lastChapter;
    private boolean useHeaderlessBrowser;
    private int chapterToChapterNumber;

    // Empty constructor for ManNovel
    AutoNovel() {
    }

    // Is called when a novel URL is being 'checked'
    public AutoNovel(GUI myGUI) {
        gui = myGUI;
        String tocUrl = gui.chapterListURL.getText();
        String host = Objects.requireNonNull(gui.autoHostSelection.getSelectedItem()).toString().toLowerCase().replace(" ", "");
        useHeaderlessBrowser = gui.useHeaderlessBrowserCheckBox.isSelected();
        window = "auto";
        currHostSettings = new HostSettings(host, tocUrl);
        blacklistedTags = currHostSettings.blacklistedTags;
        if (HostSettings.autoChapterToChapterWebsitesList.contains(gui.autoHostSelection.getSelectedItem().toString())) {
            autoChapterToChapter = true;
        }
        getChapterList();
        getNovelMetadata();
    }

    public void startDownload() {
        saveLocation = gui.saveLocation.getText();
        export = gui.exportSelection.getSelectedItem().toString();
        waitTime = Integer.parseInt(gui.waitTime.getText());
        allChapters = gui.chapterAllCheckBox.isSelected();
        invertOrder = gui.checkInvertOrder.isSelected();
        useHeaderlessBrowser = gui.useHeaderlessBrowserCheckBox.isSelected();
        displayChapterTitle = gui.displayChapterTitleCheckBox.isSelected();
        noStyling = gui.autoNoStyling.isSelected();
        if (!gui.autoChapterToChapterNumberField.getText().equals("Number")) {
            chapterToChapterNumber = Integer.valueOf(gui.autoChapterToChapterNumberField.getText());
        } else {
            chapterToChapterNumber = 1;
        }
        if (!gui.chapterAllCheckBox.isSelected()) {
            firstChapter = (Integer) gui.firstChapter.getValue();
            if (!gui.toLastChapter.isSelected()) {
                lastChapter = (Integer) gui.lastChapter.getValue();
            }
        }
        getImages = gui.getImages.isSelected();
        // Write buffered cover to save location
        if (bufferedCover != null && bookCover != null) {
            try {
                File outputfile = new File(saveLocation + File.separator + "images" + File.separator + bufferedCoverName);
                if (!outputfile.exists()) outputfile.mkdirs();
                ImageIO.write(bufferedCover, bufferedCoverName.substring(bufferedCoverName.lastIndexOf(".") + 1), outputfile);
            } catch (IOException e) {
                gui.appendText(window, "[ERROR]Could not write cover image to file.");
            }
        }
        // Getting the chapters again if it was stopped previously
        if (killTask) getChapterList();
        killTask = false;
        // Start headerless browser
        if (useHeaderlessBrowser) {
            driverSetup();
            wait = new WebDriverWait(driver, 30);
        }
        if (autoChapterToChapter) {
            String[] chapterToChapterArgs = {
                    gui.autoFirstChapterURL.getText(),
                    gui.autoLastChapterURL.getText(),
                    currHostSettings.nextChapterBtn
            };
            processChaptersToChapters(chapterToChapterArgs);
        } else {
            downloadChapters();
        }
        if (useHeaderlessBrowser) {
            driver.close();
        }
        if (!successfulFilenames.isEmpty() && !killTask) {
            switch ((String) gui.exportSelection.getSelectedItem()) {
                case "Calibre":
                    shared.createToc(this);
                    break;
                case "EPUB":
                    shared.createCoverPage(this);
                    shared.createToc(this);
                    if (!bookDesc.get(0).isEmpty() && !noDescription) shared.createDescPage(this);
                    ToEpub epub = new ToEpub(this);
                    break;
            }
            shared.report(this);
        }
    }

    // Retrieves chapter links and names
    private void getChapterList() {
        try {
            gui.appendText(window, "[INFO]Fetching novel info...");
            // Needs to be reset in case of stopped grabbing
            chapterLinks.clear();
            chaptersNames.clear();
            bookDesc.add(0, "");
            wordCount = 0;
            if (useHeaderlessBrowser) {
                getChaptersHeaderless();
            } else {
                getChaptersJsoup();
            }
        } catch (IllegalArgumentException | IOException e) {
            gui.appendText(window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets chapters using Jsoup (static html) - faster
      */
    private void getChaptersJsoup() throws IOException {
        Document doc = Jsoup.connect(currHostSettings.url).timeout(30 * 1000).get();
        tocDoc = doc;
        Elements chapterItems;
        Elements links;
        if (!autoChapterToChapter) {
            switch (currHostSettings.host) {
                // Custom chapter selection
                case "https://boxnovel.com/":
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    for(Element link: chapterItems) {
                        chapterLinks.add(link.attr("abs:href"));
                        chaptersNames.add(link.text());
                    }
                    // Get href link of last (first in novel context) chapter
                    String boxNovelFirstChapter = chapterLinks.get(chapterLinks.size()-1);
                    System.out.println(boxNovelFirstChapter);
                    String boxNovelbaseLinkStart = boxNovelFirstChapter.substring(0, shared.ordinalIndexOf(boxNovelFirstChapter, "/", 5) + 9);
                    String boxNovelChapterNumberString = boxNovelFirstChapter.substring(boxNovelbaseLinkStart.length());
                    int boxNovelChapterNumber;
                    if(boxNovelChapterNumberString.contains("-")) {
                        boxNovelChapterNumber = Integer.valueOf(boxNovelChapterNumberString.substring(0,boxNovelChapterNumberString.indexOf("-")));
                    } else {
                        boxNovelChapterNumber = Integer.valueOf(boxNovelChapterNumberString);

                    }
                    if (boxNovelChapterNumber != 1) {
                        for (int i = boxNovelChapterNumber - 1; i >= 1; i--) {
                            chapterLinks.add(boxNovelbaseLinkStart + i);
                            chaptersNames.add("Chapter " + i);
                        }
                    }
                    break;
                case "http://novelfull.com/":
                    while (!doc.select("li.next").hasClass("disabled")) {
                        chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                        for (Element link : chapterItems) {
                            chapterLinks.add(link.attr("abs:href"));
                            chaptersNames.add(link.text());
                        }
                        doc = Jsoup.connect(doc.select("li.next a").attr("abs:href")).timeout(30 * 1000).get();
                    }
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    for (Element link : chapterItems) {
                        chapterLinks.add(link.attr("abs:href"));
                        chaptersNames.add(link.text());
                    }
                    break;
                case "https://zenithnovels.com/":
                    while (!doc.select(".lcp_paginator a.lcp_nextlink").attr("abs:href").isEmpty()) {
                        chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                        for (Element link : chapterItems) {
                            chapterLinks.add(link.attr("abs:href"));
                            chaptersNames.add(link.text());
                        }
                        doc = Jsoup.connect(doc.select(".lcp_paginator a.lcp_nextlink").attr("abs:href")).timeout(30 * 1000).get();
                    }
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    for (Element link : chapterItems) {
                        chapterLinks.add(link.attr("abs:href"));
                        chaptersNames.add(link.text());
                    }
                    break;
                case "https://translatinotaku.net/":
                    while (!doc.select("a.page-numbers.next").attr("abs:href").isEmpty()) {
                        chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                        for (Element link : chapterItems) {
                            chapterLinks.add(link.attr("abs:href"));
                            chaptersNames.add(link.text());
                        }
                        doc = Jsoup.connect(doc.select("a.page-numbers.next").attr("abs:href")).timeout(30 * 1000).get();
                    }
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    for (Element link : chapterItems) {
                        chapterLinks.add(link.attr("abs:href"));
                        chaptersNames.add(link.text());
                    }
                    break;
                case "https://comrademao.com/":
                    while (!doc.select(".column a.next").attr("abs:href").isEmpty()) {
                        chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                        for (Element link : chapterItems) {
                            chapterLinks.add(link.attr("abs:href"));
                            chaptersNames.add(link.text());
                        }
                        doc = Jsoup.connect(doc.select(".column a.next").attr("abs:href")).timeout(30 * 1000).get();

                    }
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    for (Element link : chapterItems) {
                        chapterLinks.add(link.attr("abs:href"));
                        chaptersNames.add(link.text());
                    }
                    break;
                case "https://wuxiaworld.online/":
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    for (Element link : chapterItems) {
                        chapterLinks.add(link.attr("abs:href"));
                        chaptersNames.add(link.text());
                    }
                    // Get href link of last (first in novel context) chapter
                    String wuxiaonlineFirstChapter = chapterLinks.get(chapterLinks.size() - 1);
                    System.out.println(wuxiaonlineFirstChapter);
                    String wuxiaonlinebaseLinkStart = wuxiaonlineFirstChapter.substring(0, shared.ordinalIndexOf(wuxiaonlineFirstChapter, "/", 4) + 9);
                    String wuxiaonlineChapterNumberString = wuxiaonlineFirstChapter.substring(wuxiaonlinebaseLinkStart.length());
                    int wuxiaonlineChapterNumber;
                    if(wuxiaonlineChapterNumberString.contains("-")) {
                        boxNovelChapterNumber = Integer.valueOf(wuxiaonlineChapterNumberString.substring(0,wuxiaonlineChapterNumberString.indexOf("-")));
                    } else {
                        boxNovelChapterNumber = Integer.valueOf(wuxiaonlineChapterNumberString);

                    }
                    if(boxNovelChapterNumber != 1) {
                        for(int i = boxNovelChapterNumber-1; i >= 1; i--) {
                            chapterLinks.add(wuxiaonlinebaseLinkStart+i);
                            chaptersNames.add("Chapter "+i);
                        }
                    }
                    break;
                case "https://fanfiction.net/":
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    String fullLink = doc.select("link[rel=canonical]").attr("abs:href");
                    String baseLinkStart = fullLink.substring(0, shared.ordinalIndexOf(fullLink, "/", 5) + 1);
                    String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);

                    links = chapterItems.select("option[value]");
                    for (Element chapterLink : links) {
                        if (!chapterLinks.contains(baseLinkStart + chapterLink.attr("value") + baseLinkEnd)) {
                            chapterLinks.add(baseLinkStart + chapterLink.attr("value") + baseLinkEnd);
                            chaptersNames.add(chapterLink.text());
                        }
                    }
                    break;
                case "https://fanfiktion.de/":
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    String fullLink1 = doc.select("link[rel=canonical]").attr("abs:href");
                    String baseLinkStart1 = fullLink1.substring(0, shared.ordinalIndexOf(fullLink1, "/", 5) + 1);
                    String baseLinkEnd1 = fullLink1.substring(baseLinkStart1.length() + 1);
                    links = chapterItems.select("option[value]");
                    for (Element chapterLink : links) {
                        if (!chapterLinks.contains(baseLinkStart1 + chapterLink.attr("value") + baseLinkEnd1)) {
                            chapterLinks.add(baseLinkStart1 + chapterLink.attr("value") + baseLinkEnd1);
                            chaptersNames.add(chapterLink.text());
                        }
                    }
                    break;
                case "https://flying-lines.com/":
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    links = chapterItems.select("a[href]");
                    String chapterName;
                    int chapterNumber;
                    String chapterURL;
                    for (Element chapterLink : links) {
                        chapterNumber = Integer.parseInt(chapterLink.text().substring(0, chapterLink.text().indexOf(".") - 1));
                        chapterName = chapterLink.text().substring(chapterLink.text().indexOf(".") + 1);
                        chaptersNames.add("Chapter " + chapterNumber + ": " + chapterName);
                        chapterURL = chapterLink.attr("abs:href").replace("/autoNovel", "/h5/autoNovel/"
                                + gui.chapterListURL.getText().substring(gui.chapterListURL.getText().lastIndexOf("/") + 1)
                                + "/" + chapterNumber);
                        chapterLinks.add(chapterURL.substring(0, chapterURL.lastIndexOf("/")));
                    }
                    break;
                case "https://tapread.com/":
                    String novelURL = gui.chapterListURL.getText();
                    int tapReadNovelId = Integer.valueOf(novelURL.substring(novelURL.lastIndexOf("/") + 1));
                    xhrBookId = tapReadNovelId;
                    Map<String, String> chapters = xhrRequest.tapReadGetChapterList(tapReadNovelId);
                    for (String chapterId : chapters.keySet()) {
                        chaptersNames.add(chapters.get(chapterId));
                        xhrChapterIds.add(chapterId);
                        chapterLinks.add("https://.tapread.com/book/index/" + tapReadNovelId + "/" + chapterId);
                    }
                    break;
                case "https://webnovel.com/":
                    String csrfToken = "null";
                    String bookId = gui.chapterListURL.getText();
                    String bookTitle = doc.select(currHostSettings.bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
                    bookId = bookId.substring(shared.ordinalIndexOf(bookId, "/", 4) + 1, shared.ordinalIndexOf(bookId, "/", 5));

                    String otherParameter = "";
                    CookieManager cookieManager = new CookieManager();
                    CookieHandler.setDefault(cookieManager);

                    URL url = new URL(currHostSettings.url);
                    URLConnection connection = url.openConnection();
                    connection.getContent();

                    List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                    for (HttpCookie cookie : cookies) {
                        if (cookie.toString().startsWith("_csrfToken")) {
                            csrfToken = cookie.toString().substring(11);
                        }

                    }
                    Map<String, String> webnovelChapters = xhrRequest.webnovelGetChapterList(
                            "https://www.webnovel.com/apiajax/chapter/GetChapterList?_csrfToken=" + csrfToken + "&bookId=" + bookId + "&_=" + otherParameter);
                    int webnovelChapterNumber = 1;
                    for (String chapterId : webnovelChapters.keySet()) {
                        chaptersNames.add("Chapter " + webnovelChapterNumber + ": " + webnovelChapters.get(chapterId));
                        xhrChapterIds.add(chapterId);
                        chapterLinks.add(
                                "https://www.webnovel.com/book/" + bookId + "/" + chapterId + "/"
                                        + bookTitle.replace(" ", "-") + "/" + webnovelChapters.get(chapterId).replace(" ", "-"));
                        webnovelChapterNumber++;
                    }
                    break;
                case "https://creativenovels.com/":
                    String novelId = doc.select("chapter_list_novel_page").attr("class");
                    xhrRequest http = new xhrRequest();
                    String postResponse = http.sendPost("https://creativenovels.com/wp-admin/admin-ajax.php",
                            "action=crn_chapter_list&view_id=61581");
                    List<String> containedUrls = new ArrayList<String>();
                    String urlRegex = "\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
                    Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
                    Matcher urlMatcher = pattern.matcher(postResponse);

                    while (urlMatcher.find()) {
                        containedUrls.add(postResponse.substring(urlMatcher.start(0),
                                urlMatcher.end(0)));
                    }
                    int i = 1;
                    for (String chapterUrl : containedUrls) {
                        chapterUrl = chapterUrl.substring(0, chapterUrl.lastIndexOf("/"));
                        chapterLinks.add(chapterUrl);
                        chaptersNames.add("Chapter: " + i);
                        i++;
                    }
                    break;
                default:
                    chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
                    for (Element chapterLink : chapterItems) {
                        chapterLinks.add(chapterLink.attr("abs:href"));
                        chaptersNames.add(chapterLink.text());
                    }
                    break;
            }
        }
    }

    /**
     * Gets chapters using Selenium (full browser visit of website) - slower
      */
    private void getChaptersHeaderless() {
        driverSetup();
        wait = new WebDriverWait(driver, 30);
        driver.navigate().to(gui.chapterListURL.getText());
        // Save HTML source for metadata later on
        tocDoc = Jsoup.parse(driver.getPageSource());
        // Website interactions
        switch (currHostSettings.host) {
            case "https://royalroad.com/":
                Select chapterShow = new Select(driver.findElement(By.name("chapters_length")));
                chapterShow.selectByVisibleText("All");
                break;
            case "https://creativenovels.com/":
                driver.findElement(By.cssSelector("ul[role='tablist'] > li:nth-of-type(3) button")).click();
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".post_box")));
                break;
            case "https://flying-lines.com/":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chapter-tables > span:nth-child(2)")));
                driver.findElement(By.cssSelector(".chapter-tables > span:nth-child(2)")).click();
                break;
            case "https://tapread.com/":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".tab-content")));
                driver.findElement(By.cssSelector(".tab-content")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chapter-list a")));
                break;
            case "https://wordexcerpt.com/":
                driver.findElement(By.cssSelector("li.nav-item:nth-child(2) > a:nth-child(1)")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(currHostSettings.chapterLinkSelecter)));
                break;
            case "https://webnovel.com/":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div/div/div[2]/div/div/div/div[1]/div/div[1]/div/ul/li[2]/a")));
                driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div[2]/div/div/div/div[1]/div/div[1]/div/ul/li[2]/a")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".j_catalog_list a")));
                break;
            case "https://boxnovel.com/":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chapter-readmore")));
                driver.findElement(By.cssSelector(".chapter-readmore")).click();
                break;
            case "https://wordrain69.com/":
                driver.findElement(By.cssSelector(".chapter-readmore")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(currHostSettings.chapterLinkSelecter)));
                break;
            case "https://ficfun.com/":
                driver.findElement(By.cssSelector(".button-round-red")).click();
                break;
            case "https://dreame.com/":
                driver.findElement(By.cssSelector(".button-round-purple")).click();
                break;
        }
        // Parse html from headerless to Jsoup for faster interaction.
        String baseUrl = driver.getCurrentUrl().substring(0, shared.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
        // baseUrl (eg. wuxiaworld.com) is needed to get full href links
        Document doc = Jsoup.parse(driver.getPageSource(), baseUrl);
        for (Element chapterLink : doc.select(currHostSettings.chapterLinkSelecter)) {
            chapterLinks.add(chapterLink.attr("abs:href"));
            chaptersNames.add(chapterLink.text());
        }
        driver.close();
    }

    void downloadChapters() {
        gui.appendText(window, "[INFO]Downloading chapters...");
        successfulFilenames.clear();
        successfulChapterNames.clear();
        successfulExtraPagesFilenames.clear();
        successfulExtraPagesNames.clear();
        failedChapters.clear();
        imageLinks.clear();
        imageNames.clear();
        if (invertOrder) {
            Collections.reverse(chapterLinks);
            Collections.reverse(chaptersNames);
        }
        if (gui.toLastChapter.isSelected()) {
            lastChapter = chapterLinks.size();
        }
        if (allChapters) {
            processAllChapters();
        } else {
            if (lastChapter > chapterLinks.size()) {
                gui.appendText(window, "[ERROR] Novel does not have that many chapters. " +
                        "(" + chapterLinks.size() + " detected.)");
                return;
            }
            processSpecificChapters();
        }
        // Reverse chapter list back for potential re-grab
        if (gui.checkInvertOrder.isSelected()) {
            Collections.reverse(chapterLinks);
            Collections.reverse(chaptersNames);
        }
    }

    private void getNovelMetadata() {
        try {
            // Reset autoNovel info on GUI
            gui.autoBookTitle.setText("");
            gui.autoAuthor.setText("");
            gui.autoChapterAmount.setText("");
            gui.setBufferedCover(null);
            gui.autoBookSubjects.setText("");
            Document doc = tocDoc;
            // Title
            if (!currHostSettings.bookTitleSelector.isEmpty()) {
                if (doc.select(currHostSettings.bookTitleSelector) != null && !doc.select(currHostSettings.bookTitleSelector).isEmpty()) {
                    bookTitle = doc.select(currHostSettings.bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
                    gui.autoBookTitle.setText(bookTitle);
                } else {
                    bookTitle = "Unknown";
                    gui.autoBookTitle.setText("Unknown");
                }
            } else {
                bookTitle = "Unknown";
                gui.autoBookTitle.setText("Unknown");
            }
            // Description
            if (!currHostSettings.bookDescSelector.equals("false")) {
                if (doc.select(currHostSettings.bookDescSelector) != null && !doc.select(currHostSettings.bookDescSelector).isEmpty()) {
                    bookDesc.set(0, doc.select(currHostSettings.bookDescSelector).first().text());
                } else {
                    bookDesc.set(0, "");
                }
            } else {
                bookDesc.set(0, "");
            }
            // Author
            if (!currHostSettings.bookAuthorSelector.isEmpty()) {
                if (doc.select(currHostSettings.bookAuthorSelector) != null && !doc.select(currHostSettings.bookAuthorSelector).isEmpty()) {
                    bookAuthor = doc.select(currHostSettings.bookAuthorSelector).first().text();
                    gui.autoAuthor.setText(bookAuthor);
                } else {
                    bookAuthor = "Unknown";
                    gui.autoAuthor.setText("Unknown");
                }
            } else {
                bookAuthor = "Unknown";
                gui.autoAuthor.setText("Unknown");
            }
            if (!chapterLinks.isEmpty()) {
                gui.autoChapterAmount.setText(String.valueOf(chapterLinks.size()));
                gui.autoGetNumberButton.setEnabled(true);
            }
            // Tags
            if (!currHostSettings.bookSubjectSelector.isEmpty()) {
                if (doc.select(currHostSettings.bookSubjectSelector) != null && !doc.select(currHostSettings.bookSubjectSelector).isEmpty()) {
                    Elements tags = doc.select(currHostSettings.bookSubjectSelector);
                    for (Element tag : tags) {
                        bookSubjects.add(tag.text());
                    }

                    // Display book subjects on GUI
                    int maxNumberOfSubjects = 0;
                    gui.autoBookSubjects.setText("<html>");
                    for (String eachTag : bookSubjects) {
                        gui.autoBookSubjects.setText(gui.autoBookSubjects.getText() + eachTag + ", ");
                        maxNumberOfSubjects++;
                        if (maxNumberOfSubjects == 4) {
                            maxNumberOfSubjects = 0;
                            gui.autoBookSubjects.setText(gui.autoBookSubjects.getText() + "<br>");
                        }
                    }
                    if (!gui.autoBookSubjects.getText().isEmpty()) {
                        gui.autoBookSubjects.setText(
                                gui.autoBookSubjects.getText().substring(0,
                                        gui.autoBookSubjects.getText().lastIndexOf(",")));
                    }
                } else {
                    bookSubjects.add("Unknown");
                    gui.autoBookSubjects.setText("Unknown");
                }
            } else {
                bookSubjects.add("Unknown");
                gui.autoBookSubjects.setText("Unknown");
            }
            // Chapter number
            if (autoChapterToChapter) {
                gui.autoChapterAmount.setText("Unknown");
                gui.autoGetNumberButton.setEnabled(false);
            }
            if (!chapterLinks.isEmpty()) {
                gui.autoChapterAmount.setText(String.valueOf(chapterLinks.size()));
                gui.autoGetNumberButton.setEnabled(true);
            }
            // Cover
            if (!currHostSettings.bookCoverSelector.isEmpty()) {
                if (doc.select(currHostSettings.bookCoverSelector) != null && !doc.select(currHostSettings.bookCoverSelector).isEmpty()) {
                    Element coverSelect = doc.select(currHostSettings.bookCoverSelector).first();
                    if (coverSelect != null) {
                        String coverLink = coverSelect.attr("abs:src");
                        // Custom
                        if (currHostSettings.host.equals("https://wordexcerpt.com/"))
                            coverLink = coverSelect.attr("data-src");
                        if (currHostSettings.host.equals("https://webnovel.com/")) {
                            coverLink = coverLink.replace("/300/300", "/600/600");
                        }
                        bufferedCover = shared.getBufferedCover(coverLink, this);
                        gui.setBufferedCover(bufferedCover);
                        bookCover = imageNames.get(0);
                /* downloadImage() adds every image to <Lists> and this interferes with
                   the cover image when adding images from these <Lists> to the epub */
                        imageNames.clear();
                        imageLinks.clear();
                    }
                }
            }
        } catch (Exception e) {
            gui.appendText(window, e.getMessage());
            e.printStackTrace();
        }
    }

    private void processSpecificChapters() {
        tocFileName = "Table of Contents " + firstChapter + "-" + lastChapter;
        gui.setMaxProgress(window, (lastChapter - firstChapter) + 1);
        gui.progressBar.setStringPainted(true);
        for (int i = firstChapter; i <= lastChapter; i++) {
            if (useHeaderlessBrowser) {
                driver.navigate().to(chapterLinks.get(i - 1));
                String chapterContainer = currHostSettings.chapterContainer;
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(chapterContainer)));
                WebElement chapter = driver.findElement(By.cssSelector("body"));
                shared.saveChapterFromString(chapter.getAttribute("innerHTML"), i, chaptersNames.get(i - 1),
                        currHostSettings.chapterContainer, this);
            } else {
                // Custom chapter selection
                switch (currHostSettings.host) {
                    // Custom
                    case "https://tapread.com/":
                        String chapterContentString = xhrRequest.tapReadGetChapterContent("bookId=" + xhrBookId + "&chapterId=" + xhrChapterIds.get(i - 1));
                        shared.saveChapterFromString(chapterContentString, i, chaptersNames.get(i - 1),
                                currHostSettings.chapterContainer, this);
                        break;
                    default:
                        shared.saveChapterWithHTML(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                                currHostSettings.chapterContainer, this);
                        break;
                }
            }
            // If grabbing was stopped
            if (killTask) {
                gui.appendText(window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(saveLocation + "/chapters");
                Path imagesFolder = Paths.get(saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    gui.appendText(window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            shared.sleep(waitTime);
        }
    }

    private void processAllChapters() {
        tocFileName = bookTitle;
        gui.setMaxProgress(window, chapterLinks.size());
        gui.progressBar.setStringPainted(true);
        for (int i = 1; i <= chapterLinks.size(); i++) {
            if (useHeaderlessBrowser) {
                driver.navigate().to(chapterLinks.get(i - 1));
                String chapterContainer = currHostSettings.chapterContainer;
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(chapterContainer)));
                WebElement chapter = driver.findElement(By.cssSelector("body"));
                shared.saveChapterFromString(chapter.getAttribute("innerHTML"), i, chaptersNames.get(i - 1),
                        currHostSettings.chapterContainer, this);
            } else {
                switch (currHostSettings.host) {
                    // Custom
                    case "https://tapread.com/":
                        String chapterContentString = xhrRequest.tapReadGetChapterContent("bookId=" + xhrBookId + "&chapterId=" + xhrChapterIds.get(i - 1));
                        shared.saveChapterFromString(chapterContentString, i, chaptersNames.get(i - 1),
                                currHostSettings.chapterContainer, this);
                        break;
                    default:
                        shared.saveChapterWithHTML(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                                currHostSettings.chapterContainer, this);
                        break;
                }
            }
            if (killTask) {
                gui.appendText(window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(saveLocation + "/chapters");
                Path imagesFolder = Paths.get(saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    gui.appendText(window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            shared.sleep(waitTime);
        }
    }

    /**
     * Handles downloading chapter to chapter.
     */
    void processChaptersToChapters(String[] args) {
        successfulFilenames.clear();
        successfulChapterNames.clear();
        successfulExtraPagesFilenames.clear();
        successfulExtraPagesNames.clear();
        failedChapters.clear();
        imageLinks.clear();
        imageNames.clear();
        gui.appendText(window, "[INFO]Connecting...");
        gui.setMaxProgress(window, 9001);
        String nextChapter = args[0];
        String lastChapter = args[1];
        nextChapterBtn = args[2];
        int chapterNumber = chapterToChapterNumber;
        while (true) {
            if (useHeaderlessBrowser) {
                driver.navigate().to(nextChapter);
                String chapterContainer = currHostSettings.chapterContainer;
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(chapterContainer)));
                WebElement chapter = driver.findElement(By.cssSelector("body"));
                shared.saveChapterFromString(chapter.getAttribute(
                        "innerHTML"), chapterNumber, "Chapter " + chapterNumber,
                        currHostSettings.chapterContainer, this);
            } else {
                shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, currHostSettings.chapterContainer, this);
            }
            nextChapter = nextChapterURL;
            if (nextChapter.equals(lastChapter) || (nextChapter + "/").equals(lastChapter)) {
                chapterNumber++;
                shared.sleep(waitTime);
                nextChapterBtn = "NOT_SET";
                if (useHeaderlessBrowser) {
                    driver.navigate().to(nextChapter);
                    String chapterContainer = currHostSettings.chapterContainer;
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(chapterContainer)));
                    WebElement chapter = driver.findElement(By.cssSelector("body"));
                    shared.saveChapterFromString(chapter.getAttribute(
                            "innerHTML"), chapterNumber, "Chapter " + chapterNumber,
                            currHostSettings.chapterContainer, this);
                } else {
                    shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, currHostSettings.chapterContainer, this);
                }
                break;
            }
            if (killTask) {
                gui.appendText(window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(saveLocation + "/chapters");
                Path imagesFolder = Paths.get(saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    gui.appendText(window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            chapterNumber++;
            shared.sleep(waitTime);
        }
    }

    /**
     * Displays chapter name and chapter number.
     */
    public String[] getChapterNumber(GUI gui, String chapterURL) {
        try {
            int chapterNumber = chapterLinks.indexOf(chapterURL);
            if (chapterNumber == -1)
                chapterNumber = chapterLinks.indexOf(chapterURL.substring(0, chapterURL.lastIndexOf("/")));
            if (chapterNumber == -1)
                chapterNumber = chapterLinks.indexOf(chapterURL.replace("https:", "http:"));
            if (chapterNumber == -1) gui.showPopup("Could not find chapter number.", "error");
            else {
                return new String[]{chaptersNames.get(chapterNumber), String.valueOf(chapterNumber + 1)};
            }
        } catch (IllegalArgumentException e) {
            gui.appendText("auto", "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
        return new String[]{null, null};
    }


    private void driverSetup() {
        gui.appendText(window, "[INFO]Starting headerless browser...");
        switch (gui.autoBrowserCombobox.getSelectedItem().toString()) {
            case "Chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(new ChromeOptions().setHeadless(true));
                break;
            case "Firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(new FirefoxOptions().setHeadless(true));
                break;
            case "Opera":
                WebDriverManager.operadriver().setup();
                driver = new OperaDriver();
                break;
            case "Edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            case "IE":
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver();
                break;
        }
    }
}
