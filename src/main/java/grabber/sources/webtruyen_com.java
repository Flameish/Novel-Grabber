package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class webtruyen_com implements Source {
    private final String name = "WebTruyen";
    private final String url = "https://webtruyen.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public webtruyen_com(Novel novel) {
        this.novel = novel;
    }

    public webtruyen_com() {
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
            toc = Jsoup.connect(novel.novelLink)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            String storyID = toc.select("#storyID").attr("value");
            try {
                String json = Jsoup.connect("https://webtruyen.com/ajax/chapters?storyID=" + storyID)
                        .cookies(novel.cookies)
                        .ignoreContentType(true)
                        .method(Connection.Method.GET)
                        .execute().body();
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
                JSONArray chapterObjects = (JSONArray) jsonObject.get("chapters");
                for (Object o : chapterObjects) {
                    JSONObject chapterObj = (JSONObject) o;
                    String chapterNr = String.valueOf(chapterObj.get("no"));
                    String chapterLink = String.valueOf(chapterObj.get("url"));
                    chapterList.add(new Chapter(chapterNr, novel.novelLink + "/" + chapterLink));
                }
            } catch (IOException | org.json.simple.parser.ParseException e) {
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
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            chapterBody = doc.select("#chapter-content").first();
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
            Element title = toc.selectFirst("#story-detail .title");
            Element author = toc.selectFirst("#story-detail a[itemprop=author]");
            Element desc = toc.selectFirst("#story-detail .description");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(toc.selectFirst("img.cover").attr("abs:src"));

            Elements tags = toc.select("#story-detail a[itemprop=genre]");
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
