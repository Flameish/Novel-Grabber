package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import system.data.Settings;
import system.data.accounts.Account;
import system.data.accounts.Accounts;
import system.init;

public class wuxiaworld_com implements Source {
    private final Novel novel;
    private Document toc;

    public wuxiaworld_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            if(Settings.getInstance().isWuxiaHeadless()) {
                toc = getTocHeadless();
            } else {
                toc = getPageStatic();
            }
            Elements chapterLinks = toc.select("#accordion .chapter-item a");
            for(Element chapterLink: chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapterList;
    }

    private Document getPageStatic() throws IOException {
        return Jsoup.connect(novel.novelLink)
                .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                .get();
    }

    private Document getTocHeadless() {
        if(novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window, novel.browser);
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#accordion .chapter-item a")));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        Document toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        novel.headlessDriver.driver.close();
        return toc;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = null;
            if(novel.cookies != null) {
                toc = Jsoup.connect(chapter.chapterURL).cookies(novel.cookies).get();
            } else {
                toc = Jsoup.connect(chapter.chapterURL).get();
            }
            chapterBody = doc.select(".p-15 .fr-view").first();
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
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if(toc != null) {
            metadata.setTitle(toc.select(".novel-body h2").first().text());
            metadata.setAuthor(toc.select(".novel-body div:contains(Author) dd").first().text());
            metadata.setDescription(toc.select(".fr-view:not(.pt-10)").first().text());
            metadata.setBufferedCover(toc.select(".novel-left img").attr("abs:src"));

            Elements tags = toc.select(".genres a");
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
        blacklistedTags.add("a.chapter-nav");
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        System.out.println("[INFO] Login...");
        if(init.gui != null) {
            init.gui.appendText(novel.window,"[INFO] Login...");
        }
        try {
            Account account = Accounts.getInstance().getAccount("Wuxiaworld");
            if(!account.getUsername().isEmpty()) {
                Connection.Response res = Jsoup.connect("https://www.wuxiaworld.com/account/login")
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .method(Connection.Method.GET)
                        .execute();
                String token = res.parse().select("input[name=__RequestVerificationToken]").attr("value");
                res = Jsoup.connect("https://www.wuxiaworld.com/account/login")
                        .data("Email", account.getUsername())
                        .data("Password", account.getPassword())
                        .data("__RequestVerificationToken", token)
                        .data("RememberMe", "false")
                        .cookies(res.cookies())
                        .method(Connection.Method.POST)
                        .execute();
                return res.cookies();
            } else {
                System.out.println("[ERROR] No account found.");
                if(init.gui != null) {
                    init.gui.appendText(novel.window,"[ERROR] No account found.");
                }
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException();
    }

}
