package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
            if(novel.cookies != null) {
                toc = Jsoup.connect(novel.novelLink).cookies(novel.cookies).get();
            } else {
                toc = Jsoup.connect(novel.novelLink).get();
            }
            Elements chapterLinks = toc.select("#accordion .chapter-item a");
            for(Element chapterLink: chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not connect to webpage. (" + e.getMessage() + ")");
            }
        }
        return chapterList;
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
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not connect to webpage. (" + e.getMessage() + ")");
            }
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

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
