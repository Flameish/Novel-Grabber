package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import gui.GUI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.data.Settings;
import system.data.accounts.Account;
import system.data.accounts.Accounts;
import system.init;

public class wattpad_com implements Source {
    private final Novel novel;
    private Document toc;

    public wattpad_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            if(Settings.getInstance().isWattHeadless()) {
                toc = getTocHeadless();
            } else {
                toc = getPageStatic();
            }
            Elements chapterLinks = toc.select(".table-of-contents a");
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
        novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        Document toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        novel.headlessDriver.driver.close();
        return toc;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            String wattpadChapterID = chapter.chapterURL.substring(24, chapter.chapterURL.indexOf("-"));
            String json = Jsoup.connect("https://www.wattpad.com/v4/parts/" + wattpadChapterID + "?fields=text_url")
                    .ignoreContentType(true)
                    .execute()
                    .body();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject results = (JSONObject) jsonObject.get("text_url");
            Document doc = Jsoup.connect(String.valueOf(results.get("text"))).get();
            chapterBody = doc.select("body").first();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            if(init.gui != null) {
                init.gui.appendText(novel.window,"[ERROR]"+e.getMessage());
            }
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        metadata.setTitle(toc.select(".container h1").first().text());
        metadata.setAuthor(toc.select("a.send-author-event.on-navigate:not(.avatar)").first().text());
        metadata.setDescription(toc.select("h2.description").first().text());
        metadata.setBufferedCover(toc.select(".cover.cover-lg img").attr("abs:src"));

        Elements tags = toc.select(".tag-items li div.tag-item");
        List<String> subjects = new ArrayList<>();
        for(Element tag: tags) {
            subjects.add(tag.text());
        }
        metadata.setSubjects(subjects);

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        System.out.println("[INFO] Login...");
        if(init.gui != null) {
            init.gui.appendText(novel.window,"[INFO] Login...");
        }
        try {
            Account account = Accounts.getInstance().getAccount("WattPad");
            if(!account.getUsername().isEmpty()) {
                Connection.Response res = Jsoup.connect("https://www.wattpad.com/")
                        .method(Connection.Method.GET)
                        .execute();
                res = Jsoup.connect("https://www.wattpad.com/login")
                        .data("username", account.getUsername())
                        .data("password", account.getPassword())
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
