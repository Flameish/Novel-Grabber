package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
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

public class tapas_io implements Source {
    private final String name = "Tapas";
    private final String url = "https://tapas.io";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public tapas_io(Novel novel) {
        this.novel = novel;
    }

    public tapas_io() {
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
            toc = Jsoup.connect(novel.novelLink).cookies(novel.cookies).timeout(30 * 1000).get();
            String seriesId = toc.select("meta[property=al:android:url]").attr("content");
            seriesId = seriesId.substring(seriesId.indexOf("eries/") + 6, seriesId.indexOf("/info"));
            try {
                String json = Jsoup.connect("https://tapas.io/series/" + seriesId + "/episodes?page=1&sort=OLDEST&max_limit=9999")
                        .cookies(novel.cookies)
                        .ignoreContentType(true)
                        .method(Connection.Method.GET)
                        .execute().body();
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
                JSONObject data = (JSONObject) jsonObject.get("data");
                String body = (String) data.get("body");
                for (Element chapterLink : Jsoup.parse(body).select("li")) {
                    if (chapterLink.select(".ico--lock").isEmpty()) {
                        chapterList.add(new Chapter(chapterLink.select("a.info__title").text(), "https://tapas.io" + chapterLink.attr("data-href")));
                    }

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
            Document doc = Jsoup.connect(chapter.chapterURL).cookies(novel.cookies).get();
            chapterBody = doc.select("article").first();
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
            metadata.setTitle(toc.select("a.title").first().text());
            metadata.setAuthor(toc.select(".creator").first().text());
            metadata.setDescription(toc.select(".description").first().text());
            metadata.setBufferedCover(toc.select(".thumb img").attr("abs:src"));

            Elements tags = toc.select(".info-detail__row a");
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
