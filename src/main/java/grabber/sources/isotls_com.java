package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

public class isotls_com implements Source {
    private final Novel novel;
    private Document toc;

    public isotls_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Elements chapterLinks = toc.select(".chapter-item");
            for(Element chapterLink: chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
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
            chapterBody = doc.select("article").first();
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
            metadata.setTitle(toc.select("meta[name=twitter:title]").attr("content"));
            metadata.setAuthor(toc.select("meta[name=twitter:data1]").attr("content"));
            metadata.setDescription(toc.select(".project-information div").first().text());
            metadata.setBufferedCover(toc.select("meta[name=twitter:image]").attr("content"));
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        blacklistedTags.add("nav");
        blacklistedTags.add("footer");
        blacklistedTags.add("header");
        blacklistedTags.add(".ezoic-adpicker-ad");
        blacklistedTags.add(".adtester-container");
        blacklistedTags.add(".ezoic-ad");
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
