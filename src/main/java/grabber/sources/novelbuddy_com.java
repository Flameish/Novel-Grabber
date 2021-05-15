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
import java.util.List;

public class novelbuddy_com implements Source {
    private final String name = "NovelBuddy";
    private final String url = "https://novelbuddy.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public novelbuddy_com() {
    }

    public novelbuddy_com(Novel novel) {
        this.novel = novel;
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
            String novelId = toc.selectFirst("#readchapterbtn").attr("href");
            novelId = novelId.substring(1, novelId.indexOf("/", 1));
            String  json = Jsoup.connect("https://novelbuddy.com/api/novels/" + novelId + "/chapters")
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .cookies(novel.cookies)
                    .method(Connection.Method.GET)
                    .execute()
                    .body();
            JSONArray jsonArray = (JSONArray) new JSONParser().parse(json);
            for (Object chapterObj: jsonArray) {
                JSONObject chapter = (JSONObject) chapterObj;
                String chapterName = (String) chapter.get("name");
                String chapterUrl = (String) chapter.get("url");
                chapterList.add(new Chapter(chapterName, "https://novelbuddy.com" + chapterUrl));
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
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            chapterBody = doc.selectFirst("#chapter__content");
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
            Element title = toc.selectFirst("h1.novel-title");
            Element author = toc.selectFirst("span[itemprop=author]");
            Element desc = toc.selectFirst("div.summary div.content");
            Element cover = toc.selectFirst(".cover img");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:data-src") : "");

            Elements tags = toc.select(".categories li a");
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
        blacklistedTags.add("#google_translate_element");
        blacklistedTags.add("input[type=hidden]");
        blacklistedTags.add(".ads-banner");
        blacklistedTags.add("h1");
        return blacklistedTags;
    }

}
