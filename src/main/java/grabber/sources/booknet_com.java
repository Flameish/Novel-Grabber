package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
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

public class booknet_com implements Source {
    private final String name = "BookNet";
    private final String url = "https://booknet.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public booknet_com(Novel novel) {
        this.novel = novel;
    }

    public booknet_com() {
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
            Elements chapterLinks = toc.select(".js-chapter-change option");
            for (Element chapterLink : chapterLinks) {
                if (!chapterLink.attr("value").isEmpty()) {
                    chapterList.add(new Chapter(
                            chapterLink.text(),
                            novel.novelLink.replace("/book/", "/reader/") + "?c=" + chapterLink.attr("value")
                    ));
                }
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
            Connection.Response response = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .method(Connection.Method.GET)
                    .execute();
            Document doc = response.parse();
            if(doc.selectFirst("title").text().equals("Mature")) {
                GrabberUtils.err(novel.window, "Mature story. Requires an account to access.");
                return chapterBody; // Return empty chapter body
            }
            String csrf = doc.selectFirst("meta[name=csrf-token]").attr("content");
            String chapterId = doc.selectFirst(".js-chapter-change option[selected]").attr("value");
            StringBuilder content = new StringBuilder();
            int page = 1;
            while (true) {
                GrabberUtils.sleep(500);
                String json = Jsoup.connect("https://booknet.com/reader/get-page")
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .ignoreContentType(true)
                        .data("chapterId", chapterId)
                        .data("page", String.valueOf(page++))
                        .data("_csrf", csrf)
                        .cookies(response.cookies())
                        .cookies(novel.cookies)
                        .method(Connection.Method.POST)
                        .execute().body();
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
                content.append((String) jsonObject.get("data"));
                if ((boolean) jsonObject.get("isLastPage")) break;
            }
            chapterBody = Jsoup.parse(content.toString());
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr), httpEr);
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, "Could not parse response!", e);
        }
        GrabberUtils.sleep(2000);
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            Element title = toc.selectFirst(".roboto");
            Element author = toc.selectFirst(".author");
            Element desc = toc.selectFirst("#annotation");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(toc.selectFirst(".book-view-cover img").attr("abs:src"));

            Elements tags = toc.select(".book-view-info-coll p:has(span.meta-name) a");
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
        blacklistedTags.add(".reader-pagination");
        blacklistedTags.add(".clearfix");
        return blacklistedTags;
    }

}
