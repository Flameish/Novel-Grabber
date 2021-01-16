package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.data.accounts.Account;
import system.data.accounts.Accounts;
import system.init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class booklat_com_ph implements Source {
    private final Novel novel;
    private Document toc;

    public booklat_com_ph(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            Document tempPage = Jsoup.connect(novel.novelLink + "/chapters").cookies(novel.cookies).get();
            toc = Jsoup.connect(tempPage.select("#lnkRead").attr("abs:href")).cookies(novel.cookies).get();
            Elements chaptersLinks = toc.select("#ddChapter option[value]");
            for (Element chapterLink : chaptersLinks) {
                chapterList.add(new Chapter(
                        chapterLink.text(),
                        novel.novelLink.replace("/Info/", "/Read/") + "/" + chapterLink.attr("value")));
            }
            toc = tempPage;
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (IllegalArgumentException e) {
            GrabberUtils.err(novel.window, "Need to use login.", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).cookies(novel.cookies).get();
            chapterBody = doc.select("#chapter-content").first();
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
            metadata.setTitle(toc.select("#book-title").first().text());
            metadata.setAuthor(toc.select("#author-name").first().text());
            metadata.setDescription(toc.select("#story-info").first().text());
            metadata.setBufferedCover(toc.select("img#cover-image").attr("abs:src"));

            Elements tags = toc.select("#book-category");
            List<String> subjects = new ArrayList<>();
            for (Element tag : tags) {
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
        GrabberUtils.info(novel.window, "Login...");
        try {
            Account account = Accounts.getInstance().getAccount("Booklat");
            if (!account.getUsername().isEmpty()) {
                Connection.Response res = Jsoup.connect("https://www.booklat.com.ph/Account/Login")
                        .method(Connection.Method.GET)
                        .execute();
                String token = res.parse().select("input[name=__RequestVerificationToken]").attr("value");
                res = Jsoup.connect("https://www.booklat.com.ph/Account/Login")
                        .data("Email", account.getUsername())
                        .data("Password", account.getPassword())
                        .data("__RequestVerificationToken", token)
                        .data("RememberMe", "false")
                        .cookies(res.cookies())
                        .method(Connection.Method.POST)
                        .timeout(30 * 1000)
                        .execute();
                return res.cookies();
            } else {
                GrabberUtils.err(novel.window, "No account found");
                return null;
            }

        } catch (IOException e) {
            GrabberUtils.err(novel.window, e.getMessage(), e);
        }
        throw new UnsupportedOperationException();

    }

}
