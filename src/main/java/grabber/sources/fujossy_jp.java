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

public class fujossy_jp implements Source {
    private final String name = "fujossy";
    private final String url = "https://fujossy.jp/";
    private final boolean canHeadless = false;
    private Novel novel;
    private JSONObject bookObj;

    public fujossy_jp(Novel novel) {
        this.novel = novel;
    }

    public fujossy_jp() {
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
            String storyID = novel.novelLink.substring(GrabberUtils.ordinalIndexOf(novel.novelLink, "/", 4) + 1);
            String json = Jsoup.connect("https://fujossy.jp/api/books/" + storyID + ".json")
                    .cookies(novel.cookies)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute().body();
            JSONObject jsonObj = (JSONObject) new JSONParser().parse(json);
            bookObj = (JSONObject) jsonObj.get("book");
            JSONArray chapterObjects = (JSONArray) bookObj.get("stories");
            for (Object o : chapterObjects) {
                JSONObject chapterObj = (JSONObject) o;
                chapterList.add(new Chapter(String.valueOf(chapterObj.get("title")),
                        novel.novelLink + "/stories/" + chapterObj.get("id")));
            }
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(e.getMessage(), e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            doc.outputSettings().prettyPrint(false);
            chapterBody = Jsoup.parse(doc.selectFirst(".story__body").html().replaceAll("\n", "<br \\>"));
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (bookObj != null) {

            metadata.setTitle(String.valueOf(bookObj.get("title")));
            JSONObject userObj = (JSONObject) bookObj.get("user");
            metadata.setAuthor(String.valueOf(userObj.get("display_name")));
            metadata.setDescription(String.valueOf(bookObj.get("description")));
            JSONObject coverObj = (JSONObject) bookObj.get("cover");
            metadata.setBufferedCover(String.valueOf(coverObj.get("url")));

            List<String> subjects = new ArrayList<>();
            JSONArray chapterObjects = (JSONArray) bookObj.get("tag_names");
            for (Object o : chapterObjects) {
                JSONObject tagObj = (JSONObject) o;
                subjects.add(String.valueOf(tagObj.get("name")));
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
