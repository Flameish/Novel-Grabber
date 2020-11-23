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
import system.init;

public class creativenovels_com implements Source {
    private final Novel novel;
    private Document toc;

    public creativenovels_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();

        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Connection.Response res = Jsoup.connect("https://creativenovels.com/wp-admin/admin-ajax.php")
                    .method(Connection.Method.POST)
                    .timeout(30 * 1000)
                    .data("action", "crn_chapter_list")
                    .data("view_id", toc.select("#chapter_list_novel_page").attr("class"))
                    .execute();
            Document doc = res.parse();
            String ajaxResp = doc.select("body").toString();
            ajaxResp = ajaxResp.replaceAll("success.define.","");
            ajaxResp = ajaxResp.replaceAll(".data.available.end_data.","");
            String[] test = ajaxResp.split(".data.");
            List<String> names = new ArrayList<>();
            List<String> links = new ArrayList<>();
            for (String line: test) {
                if(line.contains("locked.end")) break;
                if(line.contains("http")) {
                    links.add(line.substring(line.indexOf("http")));
                } else {
                    names.add(line);
                }
            }
            names.remove(names.size()-1);
            for(int i = 0; i < links.size(); i++) {
                chapterList.add(new Chapter(names.get(i),links.get(i)));
            }
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
            Document doc = Jsoup.connect(chapter.chapterURL).get();
            chapterBody = doc.select(".entry-content.content").first();
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
            metadata.setTitle(toc.select(".e45344-14").first().text());
            metadata.setAuthor(toc.select(".e45344-16 > a:nth-child(1)").first().text());
            metadata.setDescription(toc.select(".novel_page_synopsis").first().text());
            metadata.setBufferedCover(toc.select("img.book_cover").attr("abs:src"));

            Elements tags = toc.select("div.genre_novel");
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
        blacklistedTags.add(".mNS");
        blacklistedTags.add(".support-placement");
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
