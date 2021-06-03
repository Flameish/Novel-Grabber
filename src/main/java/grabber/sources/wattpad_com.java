package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.HttpStatusException;
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
        List<Chapter> chapterList = new ArrayList<>();
        try {
            toc = Jsoup.connect(novel.novelLink)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            // Only get chapters which are not locked
            Elements chapterLinks = toc.select(".table-of-contents a:not(:has(span.fa-lock))");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
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
            Document doc = Jsoup.connect(String.valueOf(results.get("text")))
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();;
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
            Element title = toc.selectFirst("title");
            Element author = toc.selectFirst(".author-info__username a");
            Element desc = toc.selectFirst(".description-text");
            Element cover = toc.selectFirst(".story-cover img");

            metadata.setTitle(title != null ? title.text().substring(0, title.text().indexOf(" - ")) : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:src") : "");

            Elements tags = toc.select(".tag-items li a");
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
