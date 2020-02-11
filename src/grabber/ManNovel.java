package grabber;

import gui.GUI;
import gui.manSetMetadata;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManNovel extends AutoNovel {
    public static List<String> chapterLinks = new ArrayList<>();
    private static WebDriver driver;
    private String chapterContainer;

    public ManNovel(GUI myGUI, String method) {
        gui = myGUI;
        chapterContainer = gui.manChapterContainer.getText();
        saveLocation = gui.manSaveLocation.getText();
        invertOrder = gui.manInvertOrder.isSelected();
        getImages = gui.manGetImages.isSelected();
        blacklistedTags = GUI.blacklistedTags;
        waitTime = Integer.parseInt(gui.manWaitTime.getText());
        window = "manual";
        tocFileName = "Table of Contents";
        export = gui.manExportSelection.getSelectedItem().toString();
        noStyling = gui.manNoStyling.isSelected();
        displayChapterTitle = gui.manDispalyChapterTitleCheckbox.isSelected();
        bookDesc.add(0, "");
        killTask = false;
        switch (method) {
            case "chapterToChapter":
                processChaptersToChapters(GUI.chapterToChapterArgs);
                break;
            case "chaptersFromList":
                processChapersFromList();
                break;
        }
        manGetMetadata();
        if (!successfulFilenames.isEmpty() && !killTask) {
            switch ((String) myGUI.manExportSelection.getSelectedItem()) {
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

    /**
     * Stores all hyperlinks from the given URL and displays them on the gui.
     */
    public static void retrieveLinks(GUI gui)
            throws IllegalArgumentException, IOException {
        String url = gui.manNovelURL.getText();
        gui.appendText("manual", "Retrieving links from: " + url);
        Document doc;
        if (gui.manUseHeaderlessBrowser.isSelected()) {
            driverSetup(gui);
            driver.navigate().to(url);
            String baseUrl = driver.getCurrentUrl().substring(0, shared.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
            doc = Jsoup.parse(driver.getPageSource(), baseUrl);
            driver.close();
        } else {
            doc = Jsoup.connect(url).get();
        }
        Elements links = doc.select("a[href]");
        String currChapterLink;
        chapterLinks.clear();
        GUI.listModelChapterLinks.removeAllElements();
        for (Element chapterLink : links) {
            currChapterLink = chapterLink.attr("abs:href");
            if (currChapterLink.startsWith("http") && !chapterLink.text().isEmpty()) {
                chapterLinks.add(currChapterLink);
                GUI.listModelChapterLinks.addElement(chapterLink.text());
            }
        }
        if (!chapterLinks.isEmpty()) gui.appendText("manual", chapterLinks.size() + " links retrieved.");
    }

    /**
     * Checks if chapter numeration is selected and set the file name accordingly.
     */
    private static String manSetChapterName(int chapterNumber, boolean invertOrder) {
        String fileName;
        if (invertOrder)
            fileName = GUI.listModelChapterLinks.get(GUI.listModelChapterLinks.getSize() - chapterNumber);
        else fileName = GUI.listModelChapterLinks.get(chapterNumber - 1);
        return fileName;
    }

    private static void driverSetup(GUI gui) {
        gui.appendText("manual", "[INFO]Starting headerless browser...");
        switch (gui.manBrowserCombobox.getSelectedItem().toString()) {
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

    /**
     * Handles downloading chapters from provided list.
     */
    void processChapersFromList() throws IllegalArgumentException {
        String fileName;
        int chapterNumber = 0;
        gui.setMaxProgress(window, chapterLinks.size());
        if (invertOrder) Collections.reverse(chapterLinks);
        if (gui.manUseHeaderlessBrowser.isSelected()) driverSetup(gui);
        // Loop through all remaining chapter links and save them to file.
        for (String chapterLink : chapterLinks) {
            chapterNumber++;
            fileName = manSetChapterName(chapterNumber, invertOrder);
            if (gui.manUseHeaderlessBrowser.isSelected()) {
                driver.navigate().to(chapterLink);
                String baseUrl = driver.getCurrentUrl().substring(0, shared.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
                String chapterHTML = String.valueOf(Jsoup.parse(driver.getPageSource(), baseUrl));
                shared.saveChapterFromString(chapterHTML, chapterNumber, fileName, chapterContainer, this);
            } else {
                shared.saveChapterWithHTML(chapterLink, chapterNumber, fileName, chapterContainer, this);
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
        // Since chapter links are not getting cleared, they need to be re-inversed.
        if (invertOrder) Collections.reverse(chapterLinks);
        if (gui.manUseHeaderlessBrowser.isSelected()) driver.close();
    }

    /**
     * Handles downloading chapter to chapter.
     */
    void processChaptersToChapters(String[] args) {
        gui.appendText(window, "[INFO]Connecting...");
        gui.setMaxProgress(window, 9001);
        String nextChapter = args[0];
        String lastChapter = args[1];
        nextChapterBtn = args[2];
        int chapterNumber = GUI.chapterToChapterNumber;
        //if (gui.manUseHeaderlessBrowser.isSelected()) driverSetup(gui);
        while (true) {
/*            if(gui.manUseHeaderlessBrowser.isSelected()) {
                driver.navigate().to(nextChapter);
                String baseUrl = driver.getCurrentUrl().substring(0, shared.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
                String chapterHTML = String.valueOf(Jsoup.parse(driver.getPageSource(), baseUrl));
                shared.saveChapterFromString(chapterHTML, chapterNumber, "Chapter " + chapterNumber,
                        chapterContainer, this);
            } else {

            }*/
            shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, chapterContainer, this);
            nextChapter = nextChapterURL;
            System.out.println(nextChapter);
            if (nextChapter.equals(lastChapter) || (nextChapter + "/").equals(lastChapter)) {
                chapterNumber++;
                shared.sleep(waitTime);
                nextChapterBtn = "NOT_SET";
/*                if(gui.manUseHeaderlessBrowser.isSelected()) {
                    driver.navigate().to(nextChapter);
                    String baseUrl = driver.getCurrentUrl().substring(0, shared.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
                    String chapterHTML = String.valueOf(Jsoup.parse(driver.getPageSource(), baseUrl));
                    shared.saveChapterFromString(chapterHTML, chapterNumber, "Chapter " + chapterNumber,
                            chapterContainer, this);
                } else {

                }*/
                shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, chapterContainer, this);
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
        //if (gui.manUseHeaderlessBrowser.isSelected()) driver.close();
    }

    void manGetMetadata() {
        if (manSetMetadata.manMetadataInfo[0] != null && !manSetMetadata.manMetadataInfo[0].isEmpty()) {
            bookTitle = manSetMetadata.manMetadataInfo[0].replaceAll("[\\\\/:*?\"<>|]", "");
        } else bookTitle = "Unknown";
        if (manSetMetadata.manMetadataInfo[1] != null && !manSetMetadata.manMetadataInfo[1].isEmpty()) {
            bookAuthor = manSetMetadata.manMetadataInfo[1];
        } else bookAuthor = "Unknown";
        if (manSetMetadata.manMetadataInfo[2] != null && !manSetMetadata.manMetadataInfo[2].isEmpty()) {
            bookCover = manSetMetadata.manMetadataInfo[2];
        }
        if (manSetMetadata.manMetadataInfo[3] != null && !manSetMetadata.manMetadataInfo[3].isEmpty()) {
            bookDesc.add(manSetMetadata.manMetadataInfo[3]);
        } else {
            bookDesc.set(0, "");
        }
        if (manSetMetadata.manMetadataTags != null && !manSetMetadata.manMetadataTags.isEmpty()) {
            bookSubjects = manSetMetadata.manMetadataTags;
        }
        noDescription = manSetMetadata.noDescription;
    }
}
