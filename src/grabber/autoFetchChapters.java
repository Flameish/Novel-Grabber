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

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Chapter download handling of the automatic tab.
 */
public class autoFetchChapters {
    public static boolean killTask = false;
    public static WebDriver driver;
    public static WebDriverWait wait;
    static void getChapters(Download currGrab) {
        try {
            // Needs to be reset in case of stopped grabbing
            currGrab.chapterLinks.clear();
            currGrab.chaptersNames.clear();
            currGrab.bookDesc.add(0, "");
            currGrab.wordCount = 0;
            // Connect to webpage
            if (currGrab.useHeaderlessBrowser) {
                currGrab.gui.appendText(currGrab.window, "[INFO]Starting headerless browser...");
                driverSetup(currGrab);
                wait = new WebDriverWait(driver, 30);
                // Open website
                driver.navigate().to(currGrab.gui.chapterListURL.getText());
                // Get HTML source for metadata later on
                currGrab.tocDoc = Jsoup.parse(driver.getPageSource());
                // Potential click to load table of contents
                String cssQuery;
                switch (currGrab.currHostSettings.host) {
                    case "https://royalroad.com/":
                        Select chapterShow = new Select(driver.findElement(By.name("chapters_length")));
                        chapterShow.selectByVisibleText("All");
                        break;
                    case "https://creativenovels.com/":
                        cssQuery = "#tab-45344-27";
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssQuery)));
                        driver.findElement(By.cssSelector(cssQuery)).click();
                        break;
                    case "https://flying-lines.com/":
                        cssQuery = ".chapter-tables > span:nth-child(2)";
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssQuery)));
                        driver.findElement(By.cssSelector(cssQuery)).click();
                        break;
                    case "https://tapread.com/":
                        cssQuery = ".tab-content";
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssQuery)));
                        driver.findElement(By.cssSelector(cssQuery)).click();
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chapter-list a")));
                        break;
                    case "https://wordexcerpt.com/":
                        cssQuery = "li.nav-item:nth-child(2) > a:nth-child(1)";
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssQuery)));
                        driver.findElement(By.cssSelector(cssQuery)).click();
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(currGrab.currHostSettings.chapterLinkSelecter)));
                        break;
                    case "https://webnovel.com/":
                        cssQuery = "/html/body/div[1]/div/div/div/div[2]/div/div/div/div[1]/div/div[1]/div/ul/li[2]/a";
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(cssQuery)));
                        driver.findElement(By.xpath(cssQuery)).click();
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".j_catalog_list a")));
                        break;
                    case "https://boxnovel.com/":
                        cssQuery = ".chapter-readmore";
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssQuery)));
                        driver.findElement(By.cssSelector(cssQuery)).click();
                        break;
                    case "https://ficfun.com/":
                        cssQuery = ".button-round-red";
                        driver.findElement(By.cssSelector(cssQuery)).click();
                        break;
                }

                // Get chapter links and names
                String linkSelect = currGrab.currHostSettings.chapterLinkSelecter;

                String baseUrl = driver.getCurrentUrl().substring(0, Shared.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
                Document doc = Jsoup.parse(driver.getPageSource(), baseUrl);
                for (Element chapterLink : doc.select(linkSelect)) {
                    currGrab.chapterLinks.add(chapterLink.attr("abs:href"));
                    currGrab.chaptersNames.add(chapterLink.text());
                }
                /*
                List<WebElement> chapterLinks = driver.findElements(By.cssSelector(linkSelect));
                int counter = 0;
                for (WebElement chapterLink : chapterLinks) {
                    counter++;
                    if(!chapterLink.getAttribute("textContent").isEmpty()) {
                        currGrab.chaptersNames.add(chapterLink.getAttribute("textContent").trim());
                    } else {
                        currGrab.chaptersNames.add("Chapter: " + counter);
                    }
                    currGrab.chapterLinks.add(chapterLink.getAttribute("href"));
                } */
                driver.close();
            } else {
                Document doc = Jsoup.connect(currGrab.currHostSettings.url).timeout(30 * 1000).get();
                currGrab.tocDoc = doc;
                // Get chapter links and names.
                Elements chapterItems;
                Elements links;
                if (!currGrab.autoChapterToChapter) {
                    switch (currGrab.currHostSettings.host) {
                        // Custom chapter selection
                        case "https://fanfiction.net/":
                            chapterItems = doc.select(currGrab.currHostSettings.chapterLinkSelecter);
                            String fullLink = doc.select("link[rel=canonical]").attr("abs:href");
                            String baseLinkStart = fullLink.substring(0, Shared.ordinalIndexOf(fullLink, "/", 5) + 1);
                            String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);

                            links = chapterItems.select("option[value]");
                            for (Element chapterLink : links) {
                                if (!currGrab.chapterLinks.contains(baseLinkStart + chapterLink.attr("value") + baseLinkEnd)) {
                                    currGrab.chapterLinks.add(baseLinkStart + chapterLink.attr("value") + baseLinkEnd);
                                    currGrab.chaptersNames.add(chapterLink.text());
                                }
                            }
                            break;
                        case "https://fanfiktion.de/":
                            chapterItems = doc.select(currGrab.currHostSettings.chapterLinkSelecter);
                            String fullLink1 = doc.select("link[rel=canonical]").attr("abs:href");
                            String baseLinkStart1 = fullLink1.substring(0, Shared.ordinalIndexOf(fullLink1, "/", 5) + 1);
                            String baseLinkEnd1 = fullLink1.substring(baseLinkStart1.length() + 1);
                            links = chapterItems.select("option[value]");
                            for (Element chapterLink : links) {
                                if (!currGrab.chapterLinks.contains(baseLinkStart1 + chapterLink.attr("value") + baseLinkEnd1)) {
                                    currGrab.chapterLinks.add(baseLinkStart1 + chapterLink.attr("value") + baseLinkEnd1);
                                    currGrab.chaptersNames.add(chapterLink.text());
                                }
                            }
                            break;
                        case "https://flying-lines.com/":
                            chapterItems = doc.select(currGrab.currHostSettings.chapterLinkSelecter);
                            links = chapterItems.select("a[href]");
                            String chapterName;
                            int chapterNumber;
                            String chapterURL;
                            for (Element chapterLink : links) {
                                chapterNumber = Integer.parseInt(chapterLink.text().substring(0, chapterLink.text().indexOf(".") - 1));
                                chapterName = chapterLink.text().substring(chapterLink.text().indexOf(".") + 1);
                                currGrab.chaptersNames.add("Chapter " + chapterNumber + ": " + chapterName);
                                chapterURL = chapterLink.attr("abs:href").replace("/novel", "/h5/novel/"
                                        + currGrab.gui.chapterListURL.getText().substring(currGrab.gui.chapterListURL.getText().lastIndexOf("/") + 1)
                                        + "/" + chapterNumber);
                                currGrab.chapterLinks.add(chapterURL.substring(0, chapterURL.lastIndexOf("/")));
                            }
                            break;
                        case "https://tapread.com/":
                            String novelURL = currGrab.gui.chapterListURL.getText();
                            int tapReadNovelId = Integer.valueOf(novelURL.substring(novelURL.lastIndexOf("/") + 1));
                            currGrab.xhrBookId = tapReadNovelId;
                            Map<String, String> chapters = xhrRequest.tapReadGetChapterList(tapReadNovelId);
                            for (String chapterId : chapters.keySet()) {
                                currGrab.chaptersNames.add(chapters.get(chapterId));
                                currGrab.xhrChapterIds.add(chapterId);
                                currGrab.chapterLinks.add("https://.tapread.com/book/index/" + tapReadNovelId + "/" + chapterId);
                            }
                            break;
                        case "https://webnovel.com/":
                            String csrfToken = "null";
                            String bookId = currGrab.gui.chapterListURL.getText();
                            String bookTitle = doc.select(currGrab.currHostSettings.bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
                            bookId = bookId.substring(Shared.ordinalIndexOf(bookId, "/", 4) + 1, Shared.ordinalIndexOf(bookId, "/", 5));

                            String otherParameter = "";
                            CookieManager cookieManager = new CookieManager();
                            CookieHandler.setDefault(cookieManager);

                            URL url = new URL(currGrab.currHostSettings.url);
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
                                currGrab.chaptersNames.add("Chapter " + webnovelChapterNumber + ": " + webnovelChapters.get(chapterId));
                                currGrab.xhrChapterIds.add(chapterId);
                                currGrab.chapterLinks.add(
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
                                currGrab.chapterLinks.add(chapterUrl);
                                currGrab.chaptersNames.add("Chapter: " + i);
                                i++;
                            }
                            break;
                        default:
                            chapterItems = doc.select(currGrab.currHostSettings.chapterLinkSelecter);
                            for (Element chapterLink : chapterItems) {
                                currGrab.chapterLinks.add(chapterLink.attr("abs:href"));
                                currGrab.chaptersNames.add(chapterLink.text());
                            }
                            break;
                    }
                }
            }
        } catch (IllegalArgumentException | IOException e) {
            currGrab.gui.appendText(currGrab.window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
    }

    static void downloadChapters(Download currGrab) {
        currGrab.successfulFilenames.clear();
        currGrab.successfulChapterNames.clear();
        currGrab.successfulExtraPagesFilenames.clear();
        currGrab.successfulExtraPagesNames.clear();
        currGrab.failedChapters.clear();
        currGrab.imageLinks.clear();
        currGrab.imageNames.clear();
        currGrab.gui.appendText(currGrab.window, "[INFO]Connecting...");
        // Reverse link order if selected.
        if (currGrab.invertOrder) {
            Collections.reverse(currGrab.chapterLinks);
            Collections.reverse(currGrab.chaptersNames);
        }

        // To latest chapter.
        if (currGrab.gui.toLastChapter.isSelected()) {
            currGrab.lastChapter = currGrab.chapterLinks.size();
        }
        // Grab all chapters.
        if (currGrab.allChapters) {
            processAllChapters(currGrab);
            // Grab chapters from specific range.
        } else {
            if (currGrab.lastChapter > currGrab.chapterLinks.size()) {
                currGrab.gui.appendText(currGrab.window, "[ERROR] Novel does not have that many chapters. " +
                        "(" + currGrab.chapterLinks.size() + " detected.)");
                return;
            }
            processSpecificChapters(currGrab);
        }
        if (currGrab.gui.checkInvertOrder.isSelected()) {
            Collections.reverse(currGrab.chapterLinks);
            Collections.reverse(currGrab.chaptersNames);
        }
    }

    static void getMetadata(Download currGrab) {
        try {
            // Reset
            currGrab.gui.autoBookTitle.setText("");
            currGrab.gui.autoAuthor.setText("");
            currGrab.gui.autoChapterAmount.setText("");
            currGrab.gui.setBufferedCover(null);
            currGrab.gui.autoBookSubjects.setText("");
            Document doc = currGrab.tocDoc;
            // Title
            if (!currGrab.currHostSettings.bookTitleSelector.isEmpty()) {
                if (doc.select(currGrab.currHostSettings.bookTitleSelector) != null && !doc.select(currGrab.currHostSettings.bookTitleSelector).isEmpty()) {
                    currGrab.bookTitle = doc.select(currGrab.currHostSettings.bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
                    currGrab.gui.autoBookTitle.setText(currGrab.bookTitle);
                } else {
                    currGrab.bookTitle = "Unknown";
                    currGrab.gui.autoBookTitle.setText("Unknown");
                }
            } else {
                currGrab.bookTitle = "Unknown";
                currGrab.gui.autoBookTitle.setText("Unknown");
            }
            // Description
            if (!currGrab.currHostSettings.bookDescSelector.equals("false")) {
                if (doc.select(currGrab.currHostSettings.bookDescSelector) != null && !doc.select(currGrab.currHostSettings.bookDescSelector).isEmpty()) {
                    currGrab.bookDesc.set(0, doc.select(currGrab.currHostSettings.bookDescSelector).first().text());
                } else {
                    currGrab.bookDesc.set(0, "");
                }
            } else {
                currGrab.bookDesc.set(0, "");
            }
            // Author
            if (!currGrab.currHostSettings.bookAuthorSelector.isEmpty()) {
                if (doc.select(currGrab.currHostSettings.bookAuthorSelector) != null && !doc.select(currGrab.currHostSettings.bookAuthorSelector).isEmpty()) {
                    if (currGrab.currHostSettings.host.equals("https://volarenovels.com/")) {
                        currGrab.bookAuthor = doc.select(currGrab.currHostSettings.bookAuthorSelector).first().text().replace("Translated by: ", "");
                        currGrab.gui.autoAuthor.setText(currGrab.bookAuthor);
                    } else {
                        currGrab.bookAuthor = doc.select(currGrab.currHostSettings.bookAuthorSelector).first().text();
                        currGrab.gui.autoAuthor.setText(currGrab.bookAuthor);
                    }
                } else {
                    currGrab.bookAuthor = "Unknown";
                    currGrab.gui.autoAuthor.setText("Unknown");
                }
            } else {
                currGrab.bookAuthor = "Unknown";
                currGrab.gui.autoAuthor.setText("Unknown");
            }
            if (!currGrab.chapterLinks.isEmpty()) {
                currGrab.gui.autoChapterAmount.setText(String.valueOf(currGrab.chapterLinks.size()));
                currGrab.gui.autoGetNumberButton.setEnabled(true);
            }
            // Tags
            if (!currGrab.currHostSettings.bookSubjectSelector.isEmpty()) {
                if (doc.select(currGrab.currHostSettings.bookSubjectSelector) != null && !doc.select(currGrab.currHostSettings.bookSubjectSelector).isEmpty()) {
                    Elements tags = doc.select(currGrab.currHostSettings.bookSubjectSelector);
                    if (currGrab.currHostSettings.host.equals("http://gravitytales.com/")) {
                        String allTags = doc.select(currGrab.currHostSettings.bookSubjectSelector).first().text();
                        allTags = allTags.replace("Genres:", "");
                        currGrab.bookSubjects = Arrays.asList(allTags.split(", "));
                        for (String eachTag : currGrab.bookSubjects) {
                            currGrab.gui.autoBookSubjects.setText(currGrab.gui.autoBookSubjects.getText() + eachTag + ",");
                            if (!currGrab.gui.autoBookSubjects.getText().isEmpty()) {
                                currGrab.gui.autoBookSubjects.setText(
                                        currGrab.gui.autoBookSubjects.getText().substring(0,
                                                currGrab.gui.autoBookSubjects.getText().lastIndexOf(",")));
                            }
                        }
                    } else {
                        for (Element tag : tags) {
                            currGrab.bookSubjects.add(tag.text());
                        }

                        // Display book subjects on GUI
                        int maxNumberOfSubjects = 0;
                        currGrab.gui.autoBookSubjects.setText("<html>");
                        for (String eachTag : currGrab.bookSubjects) {
                            currGrab.gui.autoBookSubjects.setText(currGrab.gui.autoBookSubjects.getText() + eachTag + ", ");
                            maxNumberOfSubjects++;
                            if (maxNumberOfSubjects == 4) {
                                maxNumberOfSubjects = 0;
                                currGrab.gui.autoBookSubjects.setText(currGrab.gui.autoBookSubjects.getText() + "<br>");
                            }
                        }
                        if (!currGrab.gui.autoBookSubjects.getText().isEmpty()) {
                            currGrab.gui.autoBookSubjects.setText(
                                    currGrab.gui.autoBookSubjects.getText().substring(0,
                                            currGrab.gui.autoBookSubjects.getText().lastIndexOf(",")));
                        }
                    }
                } else {
                    currGrab.bookSubjects.add("Unknown");
                    currGrab.gui.autoBookSubjects.setText("Unknown");
                }
            } else {
                currGrab.bookSubjects.add("Unknown");
                currGrab.gui.autoBookSubjects.setText("Unknown");
            }
            // Chapter number
            if (currGrab.autoChapterToChapter) {
                currGrab.gui.autoChapterAmount.setText("Unknown");
                currGrab.gui.autoGetNumberButton.setEnabled(false);
            }
            if (!currGrab.chapterLinks.isEmpty()) {
                currGrab.gui.autoChapterAmount.setText(String.valueOf(currGrab.chapterLinks.size()));
                currGrab.gui.autoGetNumberButton.setEnabled(true);
            }
            // Cover
            if (!currGrab.currHostSettings.bookCoverSelector.isEmpty()) {
                if (doc.select(currGrab.currHostSettings.bookCoverSelector) != null && !doc.select(currGrab.currHostSettings.bookCoverSelector).isEmpty()) {
                    Element coverSelect = doc.select(currGrab.currHostSettings.bookCoverSelector).first();
                    if (coverSelect != null) {
                        String coverLink;
                        if (currGrab.currHostSettings.host.equals("https://wordexcerpt.com/"))
                            coverLink = coverSelect.attr("data-src");
                        else coverLink = coverSelect.attr("src");
                        if (currGrab.currHostSettings.host.equals("https://webnovel.com/")) {
                            coverLink = coverLink.replace("/300/300", "/600/600");
                        }
                        currGrab.bufferedCover = Shared.getBufferedCover(coverLink, currGrab);
                        currGrab.gui.setBufferedCover(currGrab.bufferedCover);
                        currGrab.bookCover = currGrab.imageNames.get(0);
                /* downloadImage() adds every image to Lists and this interferes with
                   the cover image when adding images from these Lists to the epub */
                        currGrab.imageNames.clear();
                        currGrab.imageLinks.clear();
                    }
                }
            }
        } catch (Exception e) {
            currGrab.gui.appendText(currGrab.window, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processSpecificChapters(Download currGrab) {
        currGrab.tocFileName = "Table of Contents " + currGrab.firstChapter + "-" + currGrab.lastChapter;
        currGrab.gui.setMaxProgress(currGrab.window, (currGrab.lastChapter - currGrab.firstChapter) + 1);
        currGrab.gui.progressBar.setStringPainted(true);
        if (currGrab.useHeaderlessBrowser) {
            driverSetup(currGrab);
            wait = new WebDriverWait(driver, 30);
        }
        for (int i = currGrab.firstChapter; i <= currGrab.lastChapter; i++) {
            if (currGrab.useHeaderlessBrowser) {
                driver.navigate().to(currGrab.chapterLinks.get(i - 1));
                String chapterContainer = currGrab.currHostSettings.chapterContainer;
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(chapterContainer)));
                WebElement chapter = driver.findElement(By.cssSelector(chapterContainer));
                Shared.saveChapterFromString(chapter.getAttribute("innerHTML"), i, currGrab.chaptersNames.get(i - 1),
                        currGrab.currHostSettings.chapterContainer, currGrab);
            } else {
                // Custom chapter selection
                switch (currGrab.currHostSettings.host) {
                    case "https://tapread.com/":
                        String chapterContentString = xhrRequest.tapReadGetChapterContent("bookId=" + currGrab.xhrBookId + "&chapterId=" + currGrab.xhrChapterIds.get(i - 1));
                        Shared.saveChapterFromString(chapterContentString, i, currGrab.chaptersNames.get(i - 1),
                                currGrab.currHostSettings.chapterContainer, currGrab);
                        break;
                    default:
                        Shared.saveChapterWithHTML(currGrab.chapterLinks.get(i - 1), i, currGrab.chaptersNames.get(i - 1),
                                currGrab.currHostSettings.chapterContainer, currGrab);
                        break;
                }
            }
            // If grabbing was stopped midway
            if (killTask) {
                currGrab.gui.appendText(currGrab.window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
                Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) Shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) Shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    currGrab.gui.appendText(currGrab.window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            Shared.sleep(currGrab.waitTime);
        }
        if (currGrab.useHeaderlessBrowser) {
            driver.close();
        }
    }

    private static void processAllChapters(Download currGrab) {
        currGrab.tocFileName = currGrab.bookTitle;
        currGrab.gui.setMaxProgress(currGrab.window, currGrab.chapterLinks.size());
        currGrab.gui.progressBar.setStringPainted(true);
        if (currGrab.useHeaderlessBrowser) {
            driverSetup(currGrab);
            wait = new WebDriverWait(driver, 30);
        }
        for (int i = 1; i <= currGrab.chapterLinks.size(); i++) {
            if (currGrab.useHeaderlessBrowser) {
                driver.navigate().to(currGrab.chapterLinks.get(i - 1));
                String chapterContainer = currGrab.chapterContainer;
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(chapterContainer)));
                WebElement chapter = driver.findElement(By.cssSelector(chapterContainer));
                Shared.saveChapterFromString(chapter.getAttribute("innerHTML"), i, currGrab.chaptersNames.get(i - 1),
                        currGrab.currHostSettings.chapterContainer, currGrab);
            } else {
                switch (currGrab.currHostSettings.host) {
                    case "https://tapread.com/":
                        String chapterContentString = xhrRequest.tapReadGetChapterContent("bookId=" + currGrab.xhrBookId + "&chapterId=" + currGrab.xhrChapterIds.get(i - 1));
                        Shared.saveChapterFromString(chapterContentString, i, currGrab.chaptersNames.get(i - 1),
                                currGrab.currHostSettings.chapterContainer, currGrab);
                        break;
                    default:
                        Shared.saveChapterWithHTML(currGrab.chapterLinks.get(i - 1), i, currGrab.chaptersNames.get(i - 1),
                                currGrab.currHostSettings.chapterContainer, currGrab);
                        break;
                }
            }
            if (killTask) {
                currGrab.gui.appendText(currGrab.window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
                Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) Shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) Shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    currGrab.gui.appendText(currGrab.window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            Shared.sleep(currGrab.waitTime);
        }
        if (currGrab.useHeaderlessBrowser) {
            driver.close();
        }
    }

    /**
     * Handles downloading chapter to chapter.
     */
    static void processChaptersToChapters(String[] args, Download currGrab) {
        currGrab.successfulFilenames.clear();
        currGrab.successfulChapterNames.clear();
        currGrab.successfulExtraPagesFilenames.clear();
        currGrab.successfulExtraPagesNames.clear();
        currGrab.failedChapters.clear();
        currGrab.imageLinks.clear();
        currGrab.imageNames.clear();
        currGrab.gui.appendText(currGrab.window, "[INFO]Connecting...");
        currGrab.gui.setMaxProgress(currGrab.window, 9001);
        String nextChapter = args[0];
        String lastChapter = args[1];
        currGrab.nextChapterBtn = args[2];
        int chapterNumber = currGrab.chapterToChapterNumber;
        while (true) {
            Shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, currGrab.chapterContainer, currGrab);
            nextChapter = currGrab.nextChapterURL;
            if (nextChapter.equals(lastChapter) || (nextChapter + "/").equals(lastChapter)) {
                chapterNumber++;
                Shared.sleep(currGrab.waitTime);
                currGrab.nextChapterBtn = "NOT_SET";
                Shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, currGrab.chapterContainer, currGrab);
                break;
            }
            if (killTask) {
                currGrab.gui.appendText(currGrab.window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
                Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) Shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) Shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    currGrab.gui.appendText(currGrab.window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            chapterNumber++;
            Shared.sleep(currGrab.waitTime);
        }
    }

    /**
     * Displays chapter name and chapter number.
     */
    public static String[] getChapterNumber(GUI gui, String chapterURL, Download currGrab) {
        try {
            int chapterNumber = currGrab.chapterLinks.indexOf(chapterURL);
            if (chapterNumber == -1)
                chapterNumber = currGrab.chapterLinks.indexOf(chapterURL.substring(0, chapterURL.lastIndexOf("/")));
            if (chapterNumber == -1)
                chapterNumber = currGrab.chapterLinks.indexOf(chapterURL.replace("https:", "http:"));
            if (chapterNumber == -1) gui.showPopup("Could not find chapter number.", "error");
            else {
                return new String[]{currGrab.chaptersNames.get(chapterNumber), String.valueOf(chapterNumber + 1)};
            }
        } catch (IllegalArgumentException e) {
            gui.appendText("auto", "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
        return new String[]{null, null};
    }

    private static void driverSetup(Download currGrab) {
        switch (currGrab.gui.autoBrowserCombobox.getSelectedItem().toString()) {
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
