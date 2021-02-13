package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class wattpad_com implements Source {
    private final String name = "Wattpad";
    private final String url = "https://wattpad.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public wattpad_com(Novel novel) {
        this.novel = novel;
    }

    public wattpad_com() {
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
            Elements chapterLinks = toc.select(".table-of-contents a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (IOException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            String wattpadChapterID = chapter.chapterURL.substring(24, chapter.chapterURL.indexOf("-"));
            String json = Jsoup.connect("https://www.wattpad.com/v4/parts/" + wattpadChapterID + "?fields=text_url")
                    .ignoreContentType(true)
                    .cookies(novel.cookies)
                    .execute()
                    .body();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject results = (JSONObject) jsonObject.get("text_url");
            Document doc;
            if (novel.cookies != null) {
                doc = Jsoup.connect(String.valueOf(results.get("text")))
                        .cookies(novel.cookies)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
            } else {
                doc = Jsoup.connect(String.valueOf(results.get("text"))).cookies(novel.cookies).get();
            }
            chapterBody = doc.select("body").first();
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, "JSON Parse error", e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            metadata.setTitle(toc.select(".container h1").first().text());
            metadata.setAuthor(toc.select("a.send-author-event.on-navigate:not(.avatar)").first().text());
            metadata.setDescription(toc.select("h2.description").first().text());
            metadata.setBufferedCover(toc.select(".cover.cover-lg img").attr("abs:src"));

            Elements tags = toc.select(".tag-items li div.tag-item");
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
