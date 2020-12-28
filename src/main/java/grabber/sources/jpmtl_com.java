package grabber.sources;

import grabber.Chapter;
import grabber.Novel;
import grabber.NovelMetadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class jpmtl_com implements Source {
    private final Novel novel;
    private Document toc;
    private String bookID;

    public jpmtl_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();

            bookID = novel.novelLink.substring(novel.novelLink.lastIndexOf("/") + 1);
            try {
                String jsonString = Jsoup.connect("https://jpmtl.com/v2/chapter/"+bookID+"/list?state=published&structured=true&direction=false")
                        .ignoreContentType(true)
                        .method(Connection.Method.GET)
                        .execute().body();
                Object jsonObject = new JSONParser().parse(jsonString);
                JSONArray volArray = (JSONArray) jsonObject;
                for(Object volume : volArray) {
                    JSONArray chapters = (JSONArray) ((JSONObject) volume).get("chapters");
                    for(Object chapter : chapters) {
                        long chapterID = (long) ((JSONObject) chapter).get("id");
                        String chapterTitle = (String) ((JSONObject) chapter).get("title");
                        chapterList.add(new Chapter(chapterTitle, "https://jpmtl.com/books/"+bookID+"/"+chapterID));
                    }
                }

            } catch (IOException | ParseException e) {
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
            chapterBody = doc.select(".chapter-content__content").first();
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
            metadata.setTitle(toc.selectFirst(".book-sidebar__title").text());
            metadata.setDescription(toc.selectFirst("meta[property=og:description]").attr("content"));
            metadata.setBufferedCover(toc.select("meta[property=og:image]").attr("content"));

            try {
                List<String> subjects = new ArrayList<>();
                String jsonString = Jsoup.connect("https://jpmtl.com/v2/book/"+bookID+"/category")
                        .ignoreContentType(true)
                        .method(Connection.Method.GET)
                        .execute().body();
                Object obj = new JSONParser().parse(jsonString);
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray genres = (JSONArray) jsonObject.get("genres");
                for(Object tag : genres) {
                    subjects.add((String) ((JSONObject) tag).get("name"));
                }
                metadata.setSubjects(subjects);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        blacklistedTags.add(".adswrapper");
        blacklistedTags.add(".cp-content:contains(This novel has been translated by JPMTL.com and if you are reading this somewhere, they have stolen our translation.)");
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
