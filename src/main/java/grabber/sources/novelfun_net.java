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

public class novelfun_net implements Source {
    private final String name = "NovelFun";
    private final String url = "https://novelfun.net/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public novelfun_net(Novel novel) {
        this.novel = novel;
    }

    public novelfun_net() {
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
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            String title = toc.selectFirst("link[rel=amphtml]").attr("abs:href");
            title = title.replace("https://novelfun.net/novel/", "").replace("/amp/", "");
            String jsonQuery = "{\"query\":\"query ChapterChaptersListQuery(\\n  $bookSlug: ID!\\n  $offset: Int = 0\\n) {\\n  ...ChapterChaptersList_items\\n}\\n\\nfragment ChapterChaptersList_items on Query {\\n  allChapters(bookSlug: $bookSlug, first: 9999, latestFirst: false, offset: $offset) {\\n    totalCount\\n    edges {\\n      node {\\n        title\\n        chapNum\\n        url\\n        refId\\n        id\\n      }\\n    }\\n  }\\n}\\n\",\"variables\":{\"bookSlug\":\"" + title + "\",\"offset\":0}}";
            try {
                String response = Jsoup.connect("https://novelfun.net/graphql")
                        .timeout(60000)
                        .ignoreContentType(true)
                        .cookies(novel.cookies)
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .method(Connection.Method.POST)
                        .requestBody(jsonQuery)
                        .execute()
                        .body();
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(response);
                JSONObject data = (JSONObject) jsonObject.get("data");
                JSONObject allChapters = (JSONObject) data.get("allChapters");
                JSONArray edges = (JSONArray) allChapters.get("edges");
                for (Object o : edges) {
                    JSONObject node = (JSONObject) ((JSONObject) o).get("node");
                    String chapterName = String.valueOf(node.get("title"));
                    String chapterLink = String.valueOf(node.get("url"));
                    chapterList.add(new Chapter(chapterName, "https://novelfun.net" + chapterLink));
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
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            chapterBody = doc.selectFirst("div[class*=content]");
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
            Element title = toc.selectFirst("meta[property=og:title]");
            Element author = toc.selectFirst("tbody tr:contains(Author) a");
            Element desc = toc.selectFirst("article div");

            metadata.setTitle(title != null ? title.attr("content") : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(toc.selectFirst("meta[property=og:image]").attr("abs:content"));

            Elements tags = toc.select("meta[property=book:tag]");
            List<String> subjects = new ArrayList<>();
            for (Element tag : tags) {
                subjects.add(tag.attr("content"));
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
