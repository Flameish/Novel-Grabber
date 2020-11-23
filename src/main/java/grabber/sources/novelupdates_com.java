package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;
import net.dankito.readability4j.extended.Readability4JExtended;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import system.data.Settings;
import system.init;

public class novelupdates_com implements Source {
    private final Novel novel;
    private Document toc;

    public novelupdates_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            Connection.Response res = Jsoup.connect(novel.novelLink)
                    .method(Connection.Method.GET)
                    .execute();
            toc = res.parse();
            res = Jsoup.connect("https://www.novelupdates.com/wp-admin/admin-ajax.php")
                    .method(Connection.Method.POST)
                    .cookies(res.cookies())
                    .data("action", "nd_getchapters")
                    .data("mypostid", toc.select("#mypostid").attr("value"))
                    .execute();
            Document doc = res.parse();
            for (Element chapterLink : doc.select("li a:not([title])")) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            Collections.reverse(chapterList);
        } catch (HttpStatusException httpEr) {
            String errorMsg;
            int errorCode = httpEr.getStatusCode();
            switch(errorCode) {
                case 403:
                    errorMsg = "[ERROR] Forbidden! (403)";
                    break;
                case 404:
                    errorMsg = "[ERROR] Page not found! (404)";
                    break;
                case 500:
                    errorMsg = "[ERROR] Server error! (500)";
                    break;
                case 503:
                    errorMsg = "[ERROR] Service Unavailable! (503)";
                    break;
                case 504:
                    errorMsg = "[ERROR] Gateway Timeout! (504)";
                    break;
                default:
                    errorMsg = "[ERROR] Could not connect to webpage!";
            }
            System.err.println(errorMsg);
            if (init.gui != null) {
                init.gui.appendText(novel.window, errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR] Could not connect to webpage!");
            }
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc;
            if(Settings.getInstance().isNuHeadless()) {
                doc = getPageHeadless(chapter);
            } else {
                doc = Jsoup.connect(chapter.chapterURL)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
            }
            String extractedContentHtml = findChapter(doc, chapter.chapterURL);
            chapterBody = Jsoup.parse(extractedContentHtml);
        } catch (HttpStatusException httpEr) {
            String errorMsg;
            int errorCode = httpEr.getStatusCode();
            switch(errorCode) {
                case 403:
                    errorMsg = "[ERROR] Forbidden! (403)";
                    break;
                case 404:
                    errorMsg = "[ERROR] Page not found! (404)";
                    break;
                case 500:
                    errorMsg = "[ERROR] Server error! (500)";
                    break;
                case 503:
                    errorMsg = "[ERROR] Service Unavailable! (503)";
                    break;
                case 504:
                    errorMsg = "[ERROR] Gateway Timeout! (504)";
                    break;
                default:
                    errorMsg = "[ERROR] Could not connect to webpage!";
            }
            System.err.println(errorMsg);
            if (init.gui != null) {
                init.gui.appendText(novel.window, errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR] Could not connect to webpage!");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            if(init.gui != null) {
                init.gui.appendText(novel.window,"[GRABBER-ERROR]Could not detect chapter on: "
                        + chapter.chapterURL+"("+e.getMessage()+")");
            }
        }
        return chapterBody;
    }


    private Document getPageHeadless(Chapter chapter) {
        if(novel.headlessDriver == null) {
            novel.headlessDriver = new Driver(novel.window, novel.browser);
        }
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        WebElement chapterElement = novel.headlessDriver.driver.findElement(By.cssSelector("body"));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        return Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
    }

    private String findChapter(Document doc, String URL) {
        Readability4J readability4J = new Readability4JExtended(URL, doc.html());
        Article article = readability4J.parse();
        return article.getContent();
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if(toc != null) {
            metadata.setTitle(toc.select(".seriestitlenu").first().text());
            metadata.setAuthor(toc.select("#authtag").first().text());
            metadata.setDescription(toc.select("#editdescription").first().text());
            metadata.setBufferedCover(toc.select(".seriesimg img").attr("abs:src"));

            Elements tags = toc.select("#seriesgenre a");
            List<String> subjects = new ArrayList<>();
            for(Element tag: tags) {
                subjects.add(tag.text());
            }
            metadata.setSubjects(subjects);
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
