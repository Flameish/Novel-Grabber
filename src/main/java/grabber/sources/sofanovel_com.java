package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class sofanovel_com implements Source {
    private final Novel novel;
    private JSONObject node;

    public sofanovel_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        String title = novel.novelLink.substring(novel.novelLink.indexOf("/book/")+6);
        System.out.println(title);
        try {
            // Book details
            String response = Jsoup.connect("https://srv.sofanovel.com/bookinfo/book?nType=3&nNeedThrdTypeData=0&szNameKey="+title)
                    .ignoreContentType(true)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(Connection.Method.GET)
                    .execute()
                    .body();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(response);
            JSONArray anyData = (JSONArray) jsonObject.get("anyData");
            for (Object o : anyData) {
                node = (JSONObject) o;
            }
            String bookId = String.valueOf(node.get("szBookId"));
            String trdBookId = (String) ((JSONObject) node.get("objExtend")).get("szTrdBookId");
            // Chapters
            response = Jsoup.connect("https://srv.sofanovel.com/bookinfo/chapter?nType=2&szBookID="+bookId+"&nOffset=0&nLimit=9999&nSort=1&nIsSubscribe=1")
                    .ignoreContentType(true)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(Connection.Method.GET)
                    .execute()
                    .body();
            System.out.println(response);
            jsonObject = (JSONObject) new JSONParser().parse(response);
            JSONObject chapterAnyData = (JSONObject) jsonObject.get("anyData");
            JSONArray chapterArr = (JSONArray) chapterAnyData.get("aryChapter");
            for (Object chapterObj : chapterArr) {
                JSONObject chapter = (JSONObject) chapterObj;
                String chapterName = String.valueOf(chapter.get("szChapterName"));
                String chapterId = String.valueOf(chapter.get("szTrdChpId2"));
                String chapterLink = "https://sofa-novel-private.sofanovel.com/book%2F1%2F"+trdBookId+"%2F0%2F"+chapterId+"%2F54.txt";
                chapterList.add(new Chapter(chapterName, chapterLink));
            }

        } catch (IOException | ParseException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            chapterBody = doc.selectFirst("");
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (node != null) {
            metadata.setTitle(String.valueOf(node.get("szBookName")));
            metadata.setAuthor(String.valueOf(node.get("szAuthor")));
            metadata.setDescription(String.valueOf(node.get("szDesc")));
            metadata.setBufferedCover(String.valueOf(node.get("szCover")));

            List<String> subjects = new ArrayList<>();
            JSONArray aryAllTypeData = (JSONArray) node.get("aryAllTypeData");
            for (Object o : aryAllTypeData) {
                JSONObject genre = (JSONObject) o;
                subjects.add(String.valueOf(genre.get("szThrdTypeName")));
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
