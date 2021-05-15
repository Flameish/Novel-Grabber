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

public class goodnovel_com implements Source {
    private final String name = "GoodNovel";
    private final String url = "https://www.goodnovel.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;
    private String bookId;

    public goodnovel_com() {
    }

    public goodnovel_com(Novel novel) {
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
            bookId = toc.selectFirst(".read-continue-box a").attr("href")
                    .replace("/book/", "");
            String requestString = "{\"bookId\":\"" + bookId + "\" }";
            String json = Jsoup.connect("https://www.goodnovel.com/hwyc/chapter/list")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .requestBody(requestString)
                    .cookies(novel.cookies)
                    .method(Connection.Method.POST)
                    .execute()
                    .body();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
            JSONArray data = (JSONArray) jsonObject.get("data");
            JSONObject book = (JSONObject) data.get(0);
            JSONArray chapters = (JSONArray) book.get("chapters");

            for (Object chapterObj: chapters) {
                JSONObject chapter = (JSONObject) chapterObj;
                // Skip locked chapters
                if (!(boolean) chapter.get("unlock")) continue;
                String chapterName = (String) chapter.get("chapterName");
                long chapterId = (long) chapter.get("id");
                chapterList.add(new Chapter(chapterName, String.valueOf(chapterId)));
            }
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, "JSON parse error.", e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            String requestString = "{\"chapterId\":\"" + chapter.chapterURL + "\",\"bookId\":\"" + bookId + "\"}";
            String json = Jsoup.connect("https://www.goodnovel.com/hwyc/chapter/detail")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .requestBody(requestString)
                    .cookies(novel.cookies)
                    .method(Connection.Method.POST)
                    .execute()
                    .body();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
            JSONObject data = (JSONObject) jsonObject.get("data");
            chapterBody = new Element("div");
            chapterBody.append((String) data.get("content"));
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, e.getMessage(), e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            Element title = toc.selectFirst(".bib_img img");
            Element author = toc.selectFirst(".bibio_li");
            Element desc = toc.selectFirst("#bidph");
            Element cover = toc.selectFirst(".bib_img img");

            metadata.setTitle(title != null ? title.attr("alt") : "");
            metadata.setAuthor(author != null ? author.text()
                    .replace("By:", "")
                    .replace("Completed", "")
                    .replace("Ongoing", "")
                    .trim() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:src") : "");

            Elements tags = toc.select(".bid_tit a");
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
