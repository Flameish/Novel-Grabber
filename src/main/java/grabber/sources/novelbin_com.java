package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import grabber.Driver;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;
import system.Config;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class novelbin_com implements Source {
    private final String name = "NovelBin";
    private final String url = "https://novelbin.com";
    private final boolean canHeadless = true;
    private Novel novel;
    private Document toc;
    private String coverUrl;
    private BufferedImage coverImage;

    public novelbin_com() {
    }

    public novelbin_com(Novel novel) {
        this.novel = novel;
    }

    public String getName() {
        return name;
    }

    public boolean canHeadless() {
        return canHeadless;
    }

    public String toString() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            // NovelBin always requires headless mode due to JavaScript-loaded chapters
            toc = getTocHeadless();
            
            // All chapters are in the panel-body div
            Elements chapterLinks = toc.select(".panel-body a");
            for (Element chapterLink : chapterLinks) {
                String chapterTitle = chapterLink.select(".chapter-title").text();
                if (chapterTitle.isEmpty()) {
                    chapterTitle = chapterLink.text();
                }
                chapterList.add(new Chapter(chapterTitle, chapterLink.attr("abs:href")));
            }
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapterList;
    }

    private Document getTocHeadless() {
        if (novel.headlessDriver == null) {
            // NovelBin requires a real browser (Firefox/Chrome) for JavaScript execution
            String browserToUse = null;
            
            // Determine which browser to use
            if (novel.browser != null && !novel.browser.isEmpty() && 
                (novel.browser.equals("Firefox") || novel.browser.equals("Chrome"))) {
                browserToUse = novel.browser;
            } else {
                // Check config for browser setting
                String configBrowser = Config.getInstance().getBrowser();
                if (configBrowser.equals("Firefox") || configBrowser.equals("Chrome")) {
                    browserToUse = configBrowser;
                } else {
                    // Default to Firefox if no suitable browser is configured
                    GrabberUtils.info(novel.window, "NovelBin requires Firefox or Chrome. Defaulting to Firefox.");
                    browserToUse = "Firefox";
                }
            }
            
            // Create headless driver with the selected browser
            novel.headlessDriver = createHeadlessDriver(novel.window, browserToUse);
        }
        // Navigate to the chapters tab specifically to trigger chapter loading
        String chaptersUrl = novel.novelLink;
        if (!chaptersUrl.contains("#tab-chapters-title")) {
            chaptersUrl = chaptersUrl + "#tab-chapters-title";
        }
        novel.headlessDriver.driver.navigate().to(chaptersUrl);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(chaptersUrl);
        
        // Wait for JavaScript to load all chapters
        try {
            // First wait for the panel-body to be visible
            novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".panel-body")));
            
            // Try to click the chapters tab to trigger loading
            try {
                novel.headlessDriver.driver.findElement(By.cssSelector("a[href='#tab-chapters-title']")).click();
                Thread.sleep(2000);
            } catch (Exception e) {
                // Tab might already be active or selector might be different
            }
            
            // Execute JavaScript to trigger chapter loading and scroll
            try {
                ((org.openqa.selenium.JavascriptExecutor) novel.headlessDriver.driver)
                    .executeScript("document.querySelector('a[href=\"#tab-chapters-title\"]').click();");
                Thread.sleep(1000);
                
                // Scroll to bottom multiple times to trigger lazy loading
                for (int scroll = 0; scroll < 5; scroll++) {
                    ((org.openqa.selenium.JavascriptExecutor) novel.headlessDriver.driver)
                        .executeScript("window.scrollTo(0, document.body.scrollHeight);");
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                // JavaScript execution might fail in some browsers
            }
            
            // Get the last chapter URL from the "Latest chapter" section
            String lastChapterUrl = null;
            try {
                org.openqa.selenium.WebElement lastChapterElement = novel.headlessDriver.driver
                    .findElement(By.cssSelector(".l-chapter .chapter-title"));
                lastChapterUrl = lastChapterElement.getAttribute("href");
                GrabberUtils.info(novel.window, "Waiting for last chapter to load: " + lastChapterUrl);
            }
            catch (Exception e) {
                GrabberUtils.info(novel.window, "Could not find last chapter indicator, using fallback method");
            }
            
            // Wait for the last chapter to appear in the chapter list
            if (lastChapterUrl != null) {
                final String finalLastChapterUrl = lastChapterUrl;
                int maxWait = 30; // Maximum 30 seconds
                for (int i = 0; i < maxWait; i++) {
                    Thread.sleep(1000);
                    boolean lastChapterFound = novel.headlessDriver.driver
                        .findElements(By.cssSelector(".panel-body a"))
                        .stream()
                        .anyMatch(element -> element.getAttribute("href").equals(finalLastChapterUrl));
                    
                    if (lastChapterFound) {
                        int totalCount = novel.headlessDriver.driver.findElements(By.cssSelector(".panel-body a")).size();
                        GrabberUtils.info(novel.window, "Loaded " + totalCount + " chapters from NovelBin");
                        break;
                    }
                }
            } else {
                // Fallback: Wait for count to stabilize
                int previousCount = 0;
                int stableCount = 0;
                int maxWait = 20;
                
                for (int i = 0; i < maxWait; i++) {
                    Thread.sleep(1000);
                    int currentCount = novel.headlessDriver.driver.findElements(By.cssSelector(".panel-body a")).size();
                    
                    if (currentCount == previousCount && currentCount > 30) {
                        stableCount++;
                        if (stableCount >= 2) {
                            GrabberUtils.info(novel.window, "Loaded " + currentCount + " chapters from NovelBin");
                            break;
                        }
                    } else {
                        stableCount = 0;
                    }
                    previousCount = currentCount;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        
        // Get cover image URL and capture it directly from browser
        try {
            WebElement coverElement = novel.headlessDriver.driver.findElement(By.cssSelector(".book img"));
            // Try data-src first (for lazy loading), then fall back to src
            coverUrl = coverElement.getAttribute("data-src");
            if (coverUrl == null || coverUrl.isEmpty()) {
                coverUrl = coverElement.getAttribute("src");
            }
            GrabberUtils.info(novel.window, "Found cover URL: " + coverUrl);
            
            // Capture the image directly from the browser using screenshot
            try {
                GrabberUtils.info(novel.window, "Attempting to screenshot cover element...");
                byte[] screenshotBytes = coverElement.getScreenshotAs(OutputType.BYTES);
                GrabberUtils.info(novel.window, "Screenshot bytes captured: " + screenshotBytes.length + " bytes");
                
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(screenshotBytes);
                this.coverImage = ImageIO.read(bis);
                bis.close();
                
                if (this.coverImage != null) {
                    GrabberUtils.info(novel.window, "Cover image captured successfully: " + this.coverImage.getWidth() + "x" + this.coverImage.getHeight());
                } else {
                    GrabberUtils.err(novel.window, "Screenshot captured but ImageIO.read returned null, trying direct download...");
                    // Fallback: try downloading directly
                    this.coverImage = downloadCoverImage(coverUrl);
                }
            } catch (Exception screenshotEx) {
                GrabberUtils.err(novel.window, "Failed to capture cover image via screenshot: " + screenshotEx.getMessage());
                GrabberUtils.info(novel.window, "Attempting direct download as fallback...");
                // Fallback: try downloading directly
                this.coverImage = downloadCoverImage(coverUrl);
            }
        } catch (Exception e) {
            GrabberUtils.err(novel.window, "Could not find cover image element in headless browser.", e);
        }

        // Get cookies from the headless session
        for (Cookie cookie : novel.headlessDriver.driver.manage().getCookies()) {
            novel.cookies.put(cookie.getName(), cookie.getValue());
        }

        // Parse with UTF-8 charset to handle foreign characters properly
        Document toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        toc.charset(java.nio.charset.StandardCharsets.UTF_8);
        
        novel.headlessDriver.driver.close();
        novel.headlessDriver = null;
        return toc;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .timeout(30000)
                    .get();
            doc.charset(java.nio.charset.StandardCharsets.UTF_8);
            chapterBody = doc.selectFirst("#chr-content");
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            Element title = toc.selectFirst("h3.title");
            Element author = toc.selectFirst("li:has(h3:contains(Author:)) a");
            Element desc = toc.selectFirst(".desc-text[itemprop=description]");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");

            if (coverImage != null) {
                GrabberUtils.info(novel.window, "Setting cover image in metadata");
                // Screenshots are PNG format, so use png
                metadata.setBufferedCover(coverImage, "png");
            } else {
                GrabberUtils.err(novel.window, "No cover image available");
            }

            // Add genres/tags if they exist on the page
            Elements tags = toc.select(".categories a, .genres a");
            List<String> subjects = new ArrayList<>();
            for (Element tag : tags) {
                subjects.add(tag.text());
            }
            metadata.setSubjects(subjects);
        }

        return metadata;
    }



    private Driver createHeadlessDriver(String window, String browser) {
        GrabberUtils.info(window, "Starting headless browser...");
        
        // Temporarily set the browser in config so Driver can initialize
        String originalBrowser = Config.getInstance().getBrowser();
        Config.getInstance().setBrowser(browser);
        
        // Create driver through normal constructor
        Driver driver = new Driver(window);
        
        // Close the non-headless driver
        driver.close();
        
        // Manually create the WebDriver with headless options
        if (browser.equals("Firefox")) {
            io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver().setup();
            org.openqa.selenium.firefox.FirefoxOptions options = new org.openqa.selenium.firefox.FirefoxOptions();
            options.addArguments("--headless");
            options.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            driver.driver = new org.openqa.selenium.firefox.FirefoxDriver(options);
        } else if (browser.equals("Chrome")) {
            io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
            org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            driver.driver = new org.openqa.selenium.chrome.ChromeDriver(options);
        }
        
        driver.wait = new org.openqa.selenium.support.ui.WebDriverWait(driver.driver, java.time.Duration.ofSeconds(30));
        
        // Restore original browser setting
        Config.getInstance().setBrowser(originalBrowser);
        
        return driver;
    }

    public List<String> getBlacklistedTags() {
        List<String> blacklistedTags = new ArrayList<>();
        // Add common ad/unwanted element selectors
        blacklistedTags.add(".adbox");
        blacklistedTags.add(".code-block");
        blacklistedTags.add("script");
        blacklistedTags.add("style");
        // Remove Discord/donation links
        blacklistedTags.add("p:contains(Discord:)");
        blacklistedTags.add("p:contains(Link to donations)");
        return blacklistedTags;
    }

    private BufferedImage downloadCoverImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            GrabberUtils.err(novel.window, "Cannot download cover: URL is null or empty");
            return null;
        }
        
        try {
            GrabberUtils.info(novel.window, "Downloading cover from: " + imageUrl);
            
            // Try with cookies first
            java.io.InputStream imageStream = Jsoup.connect(imageUrl)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .referrer(novel.novelLink)
                    .ignoreContentType(true)
                    .timeout(30000)
                    .execute()
                    .bodyStream();
            
            BufferedImage image = ImageIO.read(imageStream);
            imageStream.close();
            
            if (image != null) {
                GrabberUtils.info(novel.window, "Cover downloaded successfully: " + image.getWidth() + "x" + image.getHeight());
            } else {
                GrabberUtils.err(novel.window, "Downloaded cover but ImageIO.read returned null");
            }
            
            return image;
        } catch (Exception e) {
            GrabberUtils.err(novel.window, "Failed to download cover image: " + e.getMessage(), e);
            return null;
        }
    }

}
