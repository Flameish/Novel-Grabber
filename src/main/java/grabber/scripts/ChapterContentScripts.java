package grabber.scripts;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;
import net.dankito.readability4j.extended.Readability4JExtended;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import system.init;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 *  Custom scripts to fetch chapter content from host sites. Mostly XHR
 */
public class ChapterContentScripts {

    public static void fetchContent(Novel novel, Chapter chapter) {
        switch(novel.url) {
            case "https://wattpad.com/":
                wattpad(novel, chapter);
                break;
            case "https://chrysanthemumgarden.com/":
                CG(novel, chapter);
                break;
            case "https://ficfun.com/":
            case "https://dreame.com/":
                dreame(novel, chapter);
                break;
            case "https://tapread.com/":
                tapread(novel, chapter);
                break;
            default:
                defaults(novel, chapter);
                break;
        }
    }

    private static void CG(Novel novel, Chapter chapter) {
        try {
            // Fetching the chapter page
            if(novel.useHeadless) { // Headless Browser
                novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
                if(novel.chapterContainer.isEmpty()) { // Wait 5 seconds for everything to finish loading
                    novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                } else { // Wait until chapter container is located
                    novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.chapterContainer)));
                }
                WebElement chapterElement = novel.headlessDriver.driver.findElement(By.cssSelector("body"));
                String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
                chapter.doc = Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
            } else { // Static JSoup
                if(novel.cookies != null) {
                    chapter.doc = Jsoup.connect(chapter.chapterURL)
                            .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0")
                            .cookies(novel.cookies)
                            .timeout(30 * 1000)
                            .get();
                } else {
                    chapter.doc = Jsoup.connect(chapter.chapterURL)
                            .timeout(30 * 1000)
                            .get();
                }
            }
            Elements encodedStrings = chapter.doc.select(".jum");

            for(Element string: encodedStrings) {
                string.text(CGShift.getInstance().decrypt(string.text()));
            }

            chapter.chapterContainer = chapter.doc.select(novel.chapterContainer).first();
        } catch (IOException e) {
            e.printStackTrace();
            if(init.gui != null && !novel.window.equals("checker")) {
                init.gui.appendText(novel.window,"[GRABBER]"+e.getMessage());
            }
            chapter.status = 2;
        }
    }

    private static void tapread(Novel novel, Chapter chapter) {
        try {
            String json;
            if(novel.cookies != null) {
                json = Jsoup.connect("https://www.tapread.com/book/chapter")
                        .data("bookId", String.valueOf(chapter.xhrBookId))
                        .data("chapterId",chapter.xhrChapterId)
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .cookies(novel.cookies)
                        .execute()
                        .body();
            } else {
                json = Jsoup.connect("https://www.tapread.com/book/chapter")
                        .data("bookId", String.valueOf(chapter.xhrBookId))
                        .data("chapterId",chapter.xhrChapterId)
                        .method(Connection.Method.POST)
                        .ignoreContentType(true)
                        .execute()
                        .body();
            }
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject results = (JSONObject) jsonObject.get("result");
            String content = (String) results.get("content");

            chapter.doc = Jsoup.parse(content);
            chapter.chapterContainer = chapter.doc;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            if(init.gui != null && !novel.window.equals("checker")) {
                init.gui.appendText(novel.window,"[ERROR]"+e.getMessage());
            }
            chapter.status = 2;
        }
    }

    private static void dreame(Novel novel, Chapter chapter) {
        try {
            String encodedChapter;
            if(novel.cookies != null) {
                encodedChapter = Jsoup.connect(chapter.chapterURL)
                        .timeout(30 * 1000)
                        .cookies(novel.cookies)
                        .get()
                        .select("#contentTpl")
                        .html();
            } else {
                encodedChapter = Jsoup.connect(chapter.chapterURL)
                        .timeout(30 * 1000)
                        .get()
                        .select("#contentTpl")
                        .html();
            }
            String decodedChapter = new String(Base64.getMimeDecoder().decode(encodedChapter), "UTF-8");
            chapter.doc = Jsoup.parse(decodedChapter);
            chapter.chapterContainer = chapter.doc;
        } catch (IOException e) {
            e.printStackTrace();
            if(init.gui != null && !novel.window.equals("checker")) {
                init.gui.appendText(novel.window,"[ERROR]"+e.getMessage());
            }
            chapter.status = 2;
        }
    }

    private static void wattpad(Novel novel, Chapter chapter) {
        try {
            String chapterId = chapter.chapterURL.substring(24, chapter.chapterURL.indexOf("-"));
            String json;
            if(novel.cookies != null) {
                json = Jsoup.connect("https://www.wattpad.com/v4/parts/" + chapterId + "?fields=text_url")
                        .ignoreContentType(true)
                        .cookies(novel.cookies)
                        .execute()
                        .body();
            } else {
                json = Jsoup.connect("https://www.wattpad.com/v4/parts/" + chapterId + "?fields=text_url")
                        .ignoreContentType(true)
                        .execute()
                        .body();
            }
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject results = (JSONObject) jsonObject.get("text_url");
            if(novel.cookies != null) {
                chapter.doc = Jsoup.connect(String.valueOf(results.get("text")))
                        .timeout(30 * 1000)
                        .cookies(novel.cookies)
                        .get();
            } else {
                chapter.doc = Jsoup.connect(String.valueOf(results.get("text")))
                        .timeout(30 * 1000)
                        .get();
            }
            chapter.chapterContainer = chapter.doc.select(novel.chapterContainer).first();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            if(init.gui != null && !novel.window.equals("checker")) {
                init.gui.appendText(novel.window,"[ERROR]"+e.getMessage());
            }
            chapter.status = 2;
        }
    }

    private static void defaults(Novel novel, Chapter chapter) {
        try {
            // Fetching the chapter page
            if(novel.useHeadless) { // Headless Browser
                novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
                if(novel.chapterContainer.isEmpty()) { // Wait 5 seconds for everything to finish loading
                    novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
                } else { // Wait until chapter container is located
                    novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.chapterContainer)));
                }
                WebElement chapterElement = novel.headlessDriver.driver.findElement(By.cssSelector("body"));
                String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
                chapter.doc = Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
            } else { // Static JSoup
                if(novel.cookies != null) {
                    chapter.doc = Jsoup.connect(chapter.chapterURL)
                            .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0")
                            .cookies(novel.cookies)
                            .timeout(30 * 1000)
                            .get();
                } else {
                    chapter.doc = Jsoup.connect(chapter.chapterURL)
                            .timeout(30 * 1000)
                            .get();
                }
            }
            if(novel.autoDetectContainer || novel.chapterContainer == null || novel.chapterContainer.isEmpty()) {
                String url = chapter.chapterURL;
                String html = chapter.doc.html();
                Readability4J readability4J = new Readability4JExtended(url, html);
                Article article = readability4J.parse();
                String extractedContentHtml = article.getContent();
                chapter.chapterContainer = Jsoup.parse(extractedContentHtml);
            } else {
                chapter.chapterContainer = chapter.doc.select(novel.chapterContainer).first();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(init.gui != null && !novel.window.equals("checker")) {
                init.gui.appendText(novel.window,"[GRABBER-ERROR]Could not connect to webpage. "+"("+e.getMessage()+")");
            }
            chapter.status = 2;
        } catch (NullPointerException e) {
            e.printStackTrace();
            if(init.gui != null && !novel.window.equals("checker")) {
                init.gui.appendText(novel.window,"[GRABBER-ERROR]Could not detect chapter on: "
                        + chapter.chapterURL+"("+e.getMessage()+")");
            }
            chapter.status = 2;
        }
    }
}
