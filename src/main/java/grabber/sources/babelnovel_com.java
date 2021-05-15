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

import javax.swing.text.html.parser.Parser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class babelnovel_com implements Source {
    private final String name = "BabelNovel";
    private final String url = "https://babelnovel.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;
    private String bookId;
    private String token;

    public babelnovel_com() {
    }

    public babelnovel_com(Novel novel) {
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
            bookId = toc.selectFirst("a[data-bca-book-id]").attr("data-bca-book-id");
            String apiUrl = "https://api.babelnovel.com/v1/books/"+ bookId;
            String json;
            // With login, they put the token as a header
            if (!novel.cookies.isEmpty()) {
                token = novel.cookies.get("_bc_novel_token");
                json = Jsoup.connect(apiUrl + "/chapters?bookId=" + bookId + "&pageSize=9999&page=0&fields=id,name,canonicalName,isBought,isFree,isLimitFree&orderBy=asc")
                        .ignoreContentType(true)
                        .header("token", token)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .cookies(novel.cookies)
                        .method(Connection.Method.GET)
                        .execute()
                        .body();
            } else {
                json = Jsoup.connect(apiUrl + "/chapters?bookId=" + bookId + "&pageSize=9999&page=0&fields=id,name,canonicalName,isBought,isFree,isLimitFree&orderBy=asc")
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .cookies(novel.cookies)
                        .method(Connection.Method.GET)
                        .execute()
                        .body();
            }
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
            JSONArray data = (JSONArray) jsonObject.get("data");
            for (Object chapterObj: data) {
                JSONObject chapter = (JSONObject) chapterObj;
                boolean isFree = (boolean) chapter.get("isFree");
                boolean isLimitFree = (boolean) chapter.get("isLimitFree");
                boolean isBought = (boolean) chapter.get("isBought");
                // Only add if available
                if (isFree || isLimitFree || isBought) {
                    String chapterName = (String) chapter.get("name");
                    String chapterId = (String) chapter.get("id");
                    chapterList.add(new Chapter(chapterName, apiUrl + "/chapters/" + chapterId + "/content"));
                }
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
        Element chapterBody = new Element("div");
        try {
            String json;
            if (token != null) {
                json = Jsoup.connect(chapter.chapterURL)
                        .header("token", token)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .cookies(novel.cookies)
                        .method(Connection.Method.GET)
                        .execute()
                        .body();
            } else {
                json = Jsoup.connect(chapter.chapterURL)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .cookies(novel.cookies)
                        .method(Connection.Method.GET)
                        .execute()
                        .body();
            }
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
            JSONObject data = (JSONObject) jsonObject.get("data");
            String content = (String) data.get("content");
            String[] sentences = content.split("\\n\\n");
            for (String sentence: sentences) {
                Element paragraph = new Element("p");
                paragraph.appendText(sentence);
                chapterBody.appendChild(paragraph);
            }
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, "JSON parse error!", e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            Element title = toc.selectFirst("ol > li:last-child");
            Element desc = toc.selectFirst("meta[property=og:description]");
            Element cover = toc.selectFirst("meta[property=og:image]");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setDescription(desc != null ? desc.attr("content") : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:content")
                    .replace(" ", "%20") : "");

            Elements tags = toc.select("div[class^=tags_group] a");
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
