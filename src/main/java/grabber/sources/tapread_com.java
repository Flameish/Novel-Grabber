package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class tapread_com implements Source {
    private final Novel novel;
    private Document toc;

    public tapread_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            String bookID = novel.novelLink.substring(novel.novelLink.lastIndexOf("/") + 1);
            Map<String, String> chapterMap = null;
            try {
                String json = Jsoup.connect("https://www.tapread.com/ajax/book/contents")
                        .ignoreContentType(true)
                        .data("bookId", bookID)
                        .method(Connection.Method.POST)
                        .execute().body();
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
                JSONObject results = (JSONObject) jsonObject.get("result");
                JSONArray chapterObjects = (JSONArray) results.get("chapterList");
                chapterMap = new LinkedHashMap<>();
                for (Object o : chapterObjects) {
                    JSONObject slide = (JSONObject) o;
                    String chapterId = String.valueOf(slide.get("chapterId"));
                    String chapterName = String.valueOf(slide.get("chapterName"));
                    String chapterLocked = String.valueOf(slide.get("lock"));
                    if (chapterLocked.equals("0")) chapterMap.put(chapterId, chapterName);
                }
            } catch (IOException | org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            }
            for (String chapterId : chapterMap.keySet()) {
                chapterList.add(new Chapter(chapterMap.get(chapterId), "https://tapread.com/book/index/" + bookID + "/" + chapterId));
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
            String chapterID = chapter.chapterURL.substring(chapter.chapterURL.lastIndexOf("/")+1);
            String bookID = novel.novelLink.substring(novel.novelLink.lastIndexOf("/") + 1);
            String json = Jsoup.connect("https://www.tapread.com/ajax/book/chapter")
                    .data("bookId", bookID)
                    .data("chapterId", chapterID)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .execute()
                    .body();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
            JSONObject results = (JSONObject) jsonObject.get("result");
            String content = (String) results.get("content");

            chapterBody = Jsoup.parse(content);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not connect to webpage. (" + e.getMessage() + ")");
            }
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if(toc != null) {
            metadata.setTitle(toc.select(".book-name").first().text());
            metadata.setAuthor(toc.select(".person-info .author .name").first().text());
            metadata.setDescription(toc.select(".desc").first().text());
            metadata.setBufferedCover(toc.select(".book-img img").attr("abs:src"));

            Elements tags = toc.select(".book-catalog .txt");
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
