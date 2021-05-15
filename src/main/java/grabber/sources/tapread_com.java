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

public class tapread_com implements Source {
    private final String name = "TapRead";
    private final String url = "https://tapread.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public tapread_com(Novel novel) {
        this.novel = novel;
    }

    public tapread_com() {
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
            String bookID = novel.novelLink.substring(novel.novelLink.lastIndexOf("/") + 1);
            Map<String, String> chapterMap = null;
            try {
                String json = Jsoup.connect("https://www.tapread.com/ajax/book/contents")
                        .cookies(novel.cookies)
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
                GrabberUtils.err(e.getMessage(), e);
            }
            for (String chapterId : chapterMap.keySet()) {
                chapterList.add(new Chapter(chapterMap.get(chapterId), "https://tapread.com/book/index/" + bookID + "/" + chapterId));
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
            String chapterID = chapter.chapterURL.substring(chapter.chapterURL.lastIndexOf("/") + 1);
            String bookID = novel.novelLink.substring(novel.novelLink.lastIndexOf("/") + 1);
            String json = Jsoup.connect("https://www.tapread.com/ajax/book/chapter")
                    .data("bookId", bookID)
                    .data("chapterId", chapterID)
                    .cookies(novel.cookies)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .execute()
                    .body();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
            JSONObject results = (JSONObject) jsonObject.get("result");
            String content = (String) results.get("content");

            chapterBody = Jsoup.parse(content);
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, "JSON parse error", e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            metadata.setTitle(toc.select(".book-name").first().text());
            metadata.setAuthor(toc.select(".person-info .author .name").first().text());
            metadata.setDescription(toc.select(".desc").first().text());
            metadata.setBufferedCover(toc.select(".book-img img").attr("abs:src"));

            Elements tags = toc.select(".book-catalog .txt");
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

}
