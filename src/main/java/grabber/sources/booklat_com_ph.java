package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.data.accounts.Account;
import system.data.accounts.Accounts;
import system.init;

public class booklat_com_ph implements Source {
    private final Novel novel;
    private Document toc;

    public booklat_com_ph(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            Document tempPage = Jsoup.connect(novel.novelLink+"/chapters").cookies(novel.cookies).get();
            toc = Jsoup.connect(tempPage.select("#lnkRead").attr("abs:href")).cookies(novel.cookies).get();
            Elements chaptersLinks = toc.select("#ddChapter option[value]");
            for(Element chapterLink: chaptersLinks) {
                chapterList.add(new Chapter(
                        chapterLink.text(),
                        novel.novelLink.replace("/Info/", "/Read/") + "/" + chapterLink.attr("value")));
            }
            toc = tempPage;
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
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR] Need to use login.");
            }
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).cookies(novel.cookies).get();
            chapterBody = doc.select("#chapter-content").first();
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
            metadata.setTitle(toc.select("#book-title").first().text());
            metadata.setAuthor(toc.select("#author-name").first().text());
            metadata.setDescription(toc.select("#story-info").first().text());
            metadata.setBufferedCover(toc.select("img#cover-image").attr("abs:src"));

            Elements tags = toc.select("#book-category");
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
        System.out.println("[INFO] Login...");
        if(init.gui != null) {
            init.gui.appendText(novel.window,"[INFO] Login...");
        }
        try {
            Account account = Accounts.getInstance().getAccount("Booklat");
            if(!account.getUsername().isEmpty()) {
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
