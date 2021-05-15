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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class jpmtl_com implements Source {
    private final String name = "JPMTL";
    private final String url = "https://jpmtl.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;
    private String bookID;

    public jpmtl_com(Novel novel) {
        this.novel = novel;
    }

    public jpmtl_com() {
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
        try {
            toc = Jsoup.connect(novel.novelLink).cookies(novel.cookies).get();

            bookID = novel.novelLink.substring(novel.novelLink.lastIndexOf("/") + 1);
            try {
                String jsonString = Jsoup.connect("https://jpmtl.com/v2/chapter/" + bookID + "/list?state=published&structured=true&direction=false")
                        .cookies(novel.cookies)
                        .ignoreContentType(true)
                        .method(Connection.Method.GET)
                        .execute().body();
                Object jsonObject = new JSONParser().parse(jsonString);
                JSONArray volArray = (JSONArray) jsonObject;
                for (Object volume : volArray) {
                    JSONArray chapters = (JSONArray) ((JSONObject) volume).get("chapters");
                    for (Object chapter : chapters) {
                        long chapterID = (long) ((JSONObject) chapter).get("id");
                        String chapterTitle = (String) ((JSONObject) chapter).get("title");
                        chapterList.add(new Chapter(chapterTitle, "https://jpmtl.com/books/" + bookID + "/" + chapterID));
                    }
                }

            } catch (IOException | ParseException e) {
                GrabberUtils.err(e.getMessage(), e);
            }
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).cookies(novel.cookies).get();
            chapterBody = doc.select(".chapter-content__content").first();
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
            metadata.setTitle(toc.selectFirst(".book-sidebar__title").text());
            metadata.setDescription(toc.selectFirst("meta[property=og:description]").attr("content"));
            metadata.setBufferedCover(toc.select("meta[property=og:image]").attr("content"));

            try {
                List<String> subjects = new ArrayList<>();
                String jsonString = Jsoup.connect("https://jpmtl.com/v2/book/" + bookID + "/category")
                        .cookies(novel.cookies)
                        .ignoreContentType(true)
                        .method(Connection.Method.GET)
                        .execute().body();
                Object obj = new JSONParser().parse(jsonString);
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray genres = (JSONArray) jsonObject.get("genres");
                for (Object tag : genres) {
                    subjects.add((String) ((JSONObject) tag).get("name"));
                }
                metadata.setSubjects(subjects);
            } catch (IOException | ParseException e) {
                GrabberUtils.err(e.getMessage(), e);
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

}
