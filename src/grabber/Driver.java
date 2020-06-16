package grabber;

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

import java.util.ArrayList;
import java.util.List;

public class Driver {
    WebDriver driver;
    WebDriverWait wait;
    Novel novel;
    public Driver(Novel novel) {
        this.novel = novel;
        driverSetup();
        wait = new WebDriverWait(driver, 30);
    }

    private void driverSetup() {
        novel.gui.appendText(novel.options.window, "[INFO]Starting headerless browser...");
        switch (novel.options.browser) {
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
     * Fetches chapters list with a headless browser and saves the page in novel.tableOfContent
     */
    public List<Chapter> getChapterList() {
        driver.navigate().to(novel.novelLink);
        String baseUrl;
        // These websites require manual interactions to display the chapter list
        switch (novel.host.url) {
            case "https://royalroad.com/":
                Select chapterShow = new Select(driver.findElement(By.name("chapters_length")));
                chapterShow.selectByVisibleText("All");
                break;
            case "https://comrademao.com/":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.bookDescSelector)));
                // Parse html from headerless to Jsoup for faster interaction.
                baseUrl = driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
                // Save table of contents doc for metadata extraction later on
                novel.tableOfContent = Jsoup.parse(driver.getPageSource(), baseUrl);
                Elements chapterLinks;
                List<Chapter> chapters = new ArrayList<>();
                while (!novel.tableOfContent.select(".pagination a.next").attr("abs:href").isEmpty()) {
                    chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
                    for (Element chapterLink : chapterLinks) {
                        chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                    driver.navigate().to(novel.tableOfContent.select(".pagination a.next").attr("abs:href"));
                    novel.tableOfContent = Jsoup.parse(driver.getPageSource(), baseUrl);
                }
                chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                return chapters;
            case "https://creativenovels.com/":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul[role='tablist'] > li:nth-of-type(3) button")));
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
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.nav-item:nth-child(2) > a:nth-child(1)")));
                driver.findElement(By.cssSelector("li.nav-item:nth-child(2) > a:nth-child(1)")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.chapterLinkSelector)));
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
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chapter-readmore")));
                driver.findElement(By.cssSelector(".chapter-readmore")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.chapterLinkSelector)));
                break;
            case "https://ficfun.com/":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button-round-red")));
                baseUrl = driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
                // metadata and chapterlist are not on the same page
                novel.tempPage  = Jsoup.parse(driver.getPageSource(), baseUrl);
                driver.findElement(By.cssSelector(".button-round-red")).click();
                break;
            case "https://dreame.com/":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button-round-purple")));
                baseUrl = driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
                // metadata and chapterlist are not on the same page
                novel.tempPage  = Jsoup.parse(driver.getPageSource(), baseUrl);
                driver.findElement(By.cssSelector(".button-round-purple")).click();
                break;
            case "https://wuxiaworld.site/":
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.chapterLinkSelector)));
                break;
        }
        // Parse html from headerless to Jsoup for faster interaction.
        baseUrl = driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
        // Save table of contents doc for metadata extraction later on
        novel.tableOfContent = Jsoup.parse(driver.getPageSource(), baseUrl);

        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }

        switch(novel.host.url) {
            case "https://dreame.com/":
                novel.tableOfContent = novel.tempPage;
                break;
            case "https://ficfun.com/":
                novel.tableOfContent = novel.tempPage;
                break;
        }
        return chapters;
    }

    public Document getChapterContent(String chapterLink) {
        driver.navigate().to(chapterLink);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.chapterContainer)));
        WebElement chapterElement = driver.findElement(By.cssSelector("body"));
        String baseUrl = driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(driver.getCurrentUrl(), "/", 3) + 1);
        return Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
    }

    public void close() {
        driver.close();
    }
}
