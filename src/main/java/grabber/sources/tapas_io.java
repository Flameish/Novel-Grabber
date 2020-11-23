package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

public class tapas_io implements Source {
    private final Novel novel;
    private Document toc;

    public tapas_io(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
            String seriesId = toc.select("meta[property=al:android:url]").attr("content");
            seriesId = seriesId.substring(seriesId.indexOf("eries/")+6,seriesId.indexOf("/info"));
            try {
                String json = Jsoup.connect("https://tapas.io/series/"+seriesId+"/episodes?page=1&sort=OLDEST&max_limit=9999")
                        .ignoreContentType(true)
                        .method(Connection.Method.GET)
                        .execute().body();
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
                JSONObject data = (JSONObject) jsonObject.get("data");
                String body = (String) data.get("body");
                for (Element chapterLink : Jsoup.parse(body).select("li")) {
                    if(chapterLink.select(".ico--lock").isEmpty()) {
                        chapterList.add(new Chapter(chapterLink.select("a.info__title").text(), "https://tapas.io"+chapterLink.attr("data-href")));
                    }

                }
            } catch (IOException | org.json.simple.parser.ParseException e) {
                e.printStackTrace();
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
            metadata.setTitle(toc.select("a.title").first().text());
            metadata.setAuthor(toc.select(".creator").first().text());
            metadata.setDescription(toc.select(".description").first().text());
            metadata.setBufferedCover(toc.select(".thumb img").attr("abs:src"));

            Elements tags = toc.select(".info-detail__row a");
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
