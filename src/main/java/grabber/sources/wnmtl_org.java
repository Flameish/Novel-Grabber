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
import java.util.Collections;
import java.util.List;

public class wnmtl_org implements Source {
    private final String name = "WNMTL";
    private final String url = "https://www.wnmtl.org/";
    private final boolean canHeadless = false;
    private Novel novel;
    private String infoJson;

    public wnmtl_org(Novel novel) {
        this.novel = novel;
    }

    public wnmtl_org() {
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
            int a = novel.novelLink.indexOf("/book/");
            int b = novel.novelLink.indexOf("-");
            String bookId = novel.novelLink.substring(a+6, b);
            infoJson = Jsoup.connect("https://api.mystorywave.com/story-wave-backend/api/v1/content/books/" + bookId)
                    .header("site-domain", "wnmtl.org")
                    .ignoreContentType(true)
                    .cookies(novel.cookies)
                    .execute()
                    .body();

            String chapterListJson = Jsoup.connect("https://api.mystorywave.com/story-wave-backend/api/v1/content/chapters/page?sortDirection=ASC&bookId="+ bookId + "&pageNumber=1&pageSize=10000")
                    .header("site-domain", "wnmtl.org")
                    .ignoreContentType(true)
                    .cookies(novel.cookies)
                    .execute()
                    .body();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(chapterListJson);
            JSONObject data = (JSONObject) jsonObject.get("data");
            JSONArray jsonArray = (JSONArray) data.get("list");
            for (Object obj : jsonArray) {
                JSONObject chapterObj = (JSONObject) obj;
                String name = (String) chapterObj.get("title");
                String id = String.valueOf((long) chapterObj.get("id"));
                String url = "https://api.mystorywave.com/story-wave-backend/api/v1/content/chapters/" + id;
                chapterList.add(new Chapter(name, url));
            }
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, "JSON parse error", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            String chapterJson = Jsoup.connect(chapter.chapterURL)
                    .header("site-domain", "wnmtl.org")
                    .ignoreContentType(true)
                    .cookies(novel.cookies)
                    .execute()
                    .body();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(chapterJson);
            JSONObject data = (JSONObject) jsonObject.get("data");
            String chapterText = (String) data.get("content");
            chapterText = chapterText.replaceAll("\\n\\n", "<br><br>");
            chapterBody = Jsoup.parse(chapterText);
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (ParseException e) {
            GrabberUtils.err(novel.window, "JSON parse error", e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (infoJson != null) {
            try {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(infoJson);
                JSONObject data = (JSONObject) jsonObject.get("data");
                String title = (String) data.get("title");
                String author = (String) data.get("authorPseudonym");
                String desc = (String) data.get("synopsis");
                String cover = (String) data.get("coverImgUrl");

                metadata.setTitle(title != null ? title : "");
                metadata.setAuthor(author != null ? author : "");
                metadata.setDescription(desc != null ? desc : "");
                metadata.setBufferedCover(cover != null ? cover : "");

                String genre = (String) data.get("genreName");

                List<String> subjects = new ArrayList<>();
                subjects.add(genre != null ? genre : "");
                metadata.setSubjects(subjects);
            } catch (ParseException e) {
                GrabberUtils.err(novel.window, "JSON parse error", e);
            }

        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        blacklistedTags.add("div.code-block");
        blacklistedTags.add(".adbox");
        return blacklistedTags;
    }

}
