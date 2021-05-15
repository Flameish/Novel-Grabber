package grabber.sources;

import grabber.*;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class activetranslations_xyz implements Source {
    private final String name = "Active Translations";
    private final String url = "https://activetranslations.xyz/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public activetranslations_xyz() {
    }

    public activetranslations_xyz(Novel novel) {
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
            toc = Jsoup.connect(novel.novelLink)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            Elements chapterLinks = toc.select(".container .pt-cv-content-item");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.select(".panel-title").text(),
                        chapterLink.select(".pt-cv-readmore").attr("abs:href")));
            }
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = new Element("div");
        if (novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window);
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.headlessDriver.driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        List<WebElement> paragraphs = novel.headlessDriver.driver.findElements(By.cssSelector(".entry-content .row p"));
        for (WebElement element: paragraphs) {
            Element para = new Element("p");
            List<WebElement> spans = element.findElements(By.cssSelector("span"));

            if (spans.isEmpty()) {
                para.appendText(element.getText());
            } else {
                for (WebElement span: spans) {
                    Element ele = new Element("span");
                    String before = ((JavascriptExecutor) novel.headlessDriver.driver)
                            .executeScript("return window.getComputedStyle(arguments[0], ':before').getPropertyValue('content');", span)
                            .toString();

                    String after = ((JavascriptExecutor) novel.headlessDriver.driver)
                            .executeScript("return window.getComputedStyle(arguments[0], ':after').getPropertyValue('content');", span)
                            .toString();
                    after = after.substring(1, after.length()-1);
                    if (before.equals("none")) before = "";
                    if (after.equals("none")) after = "";
                    if (before.length() > 2) before = before.substring(1, before.length() - 1);
                    ele.appendText(before + " " + span.getText() + " " + after);
                    para.appendChild(ele);
                }
            }
            chapterBody.appendChild(para);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            Element title = toc.selectFirst(".nv-page-title");

            metadata.setTitle(title != null ? title.text() : "");
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

}
