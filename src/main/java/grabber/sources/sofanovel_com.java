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
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class sofanovel_com implements Source {
    private final String name = "SofaNovel";
    private final String url = "https://www.sofanovel.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private JSONObject node;

    public sofanovel_com(Novel novel) {
        this.novel = novel;
    }

    public sofanovel_com() {
    }

    public String getName() {
        return name;
    }

    public boolean canHeadless() {
        return canHeadless;
    }

    public String toString() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        String title = novel.novelLink.substring(novel.novelLink.indexOf("/book/") + 6);
        try {
            // Book details
            String response = Jsoup.connect("https://srv.sofanovel.com/bookinfo/book?nType=3&nNeedThrdTypeData=0&szNameKey=" + title)
                    .ignoreContentType(true)
                    .cookies(novel.cookies)
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
            response = Jsoup.connect("https://srv.sofanovel.com/bookinfo/chapter?nType=2&szBookID=" + bookId + "&nOffset=0&nLimit=9999&nSort=1&nIsSubscribe=1")
                    .ignoreContentType(true)
                    .cookies(novel.cookies)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(Connection.Method.GET)
                    .execute()
                    .body();
            jsonObject = (JSONObject) new JSONParser().parse(response);
            JSONObject chapterAnyData = (JSONObject) jsonObject.get("anyData");
            JSONArray chapterArr = (JSONArray) chapterAnyData.get("aryChapter");
            for (Object chapterObj : chapterArr) {
                JSONObject chapter = (JSONObject) chapterObj;
                if (String.valueOf(chapter.get("nLock")).equals("0")) {
                    String chapterName = String.valueOf(chapter.get("szChapterName"));
                    String chapterId = String.valueOf(chapter.get("szChapterID"));
                    String chapterLink = "https://srv.sofanovel.com/chapter/getAry?bookID=" + bookId + "&chapterID=" + chapterId;
                    chapterList.add(new Chapter(chapterName, chapterLink));
                }
            }

        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, "JSON parse error!", e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            // Book details
            String response = Jsoup.connect(chapter.chapterURL + "&report=[{%22szCDNName%22:%22aliCDN%22,%22nErrCount%22:0,%22nTime%22:1873,%22nCount%22:10}]")
                    .ignoreContentType(true)
                    .cookies(novel.cookies)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(Connection.Method.GET)
                    .execute()
                    .body();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(response);
            JSONObject chapterAnyData = (JSONObject) jsonObject.get("anyData");
            JSONArray chapterArr = (JSONArray) chapterAnyData.get("aryContentURL");
            JSONObject report = (JSONObject) chapterArr.get(0);
            String txtLink = String.valueOf(report.get("szContentURL"));
            StringBuilder chapterContent = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(txtLink).openStream()))) {
                chapterContent.append("<div>");
                String line = null;
                while ((line = in.readLine()) != null) {
                    if (!line.trim().isEmpty()) chapterContent.append("<p>" + line + "</p>");
                }
                chapterContent.append("</div>");
            } catch (IOException e) {
                e.printStackTrace();
            }
            chapterBody = Jsoup.parse(chapterContent.toString(), "", Parser.xmlParser());
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, "Parse error!", e);
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

}
