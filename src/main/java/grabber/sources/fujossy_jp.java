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
import system.init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class fujossy_jp implements Source {
    private final Novel novel;
    private JSONObject bookObj;

    public fujossy_jp(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            String storyID = novel.novelLink.substring(GrabberUtils.ordinalIndexOf(novel.novelLink,"/",4)+1);
            String json = Jsoup.connect("https://fujossy.jp/api/books/"+storyID+".json")
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute().body();
            JSONObject jsonObj = (JSONObject) new JSONParser().parse(json);
            bookObj = (JSONObject) jsonObj.get("book");
            JSONArray chapterObjects = (JSONArray) bookObj.get("stories");
            for (Object o : chapterObjects) {
                JSONObject chapterObj = (JSONObject) o;
                chapterList.add(new Chapter(String.valueOf(chapterObj.get("title")),
                        novel.novelLink + "/stories/" + chapterObj.get("id")));
            }
        } catch (HttpStatusException httpEr) {
            String errorMsg;
            int errorCode = httpEr.getStatusCode();
            switch(errorCode) {
                case 403:
                    errorMsg = "[ERROR] Forbidden! (403)";
                    break;
                case 404:
                    errorMsg = "[ERROR] Page not found! (404)";
                    break;
                case 500:
                    errorMsg = "[ERROR] Server error! (500)";
                    break;
                case 503:
                    errorMsg = "[ERROR] Service Unavailable! (503)";
                    break;
                case 504:
                    errorMsg = "[ERROR] Gateway Timeout! (504)";
                    break;
                default:
                    errorMsg = "[ERROR] Could not connect to webpage!";
            }
            System.err.println(errorMsg);
            if (init.gui != null) {
                init.gui.appendText(novel.window, errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR] Could not connect to webpage!");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            doc.outputSettings().prettyPrint(false);
            chapterBody = doc.selectFirst(".story__body");
        } catch (HttpStatusException httpEr) {
            String errorMsg;
            int errorCode = httpEr.getStatusCode();
            switch(errorCode) {
                case 403:
                    errorMsg = "[ERROR] Forbidden! (403)";
                    break;
                case 404:
                    errorMsg = "[ERROR] Page not found! (404)";
                    break;
                case 500:
                    errorMsg = "[ERROR] Server error! (500)";
                    break;
                case 503:
                    errorMsg = "[ERROR] Service Unavailable! (503)";
                    break;
                case 504:
                    errorMsg = "[ERROR] Gateway Timeout! (504)";
                    break;
                default:
                    errorMsg = "[ERROR] Could not connect to webpage!";
            }
            System.err.println(errorMsg);
            if (init.gui != null) {
                init.gui.appendText(novel.window, errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR] Could not connect to webpage!");
            }
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if(bookObj != null) {

            metadata.setTitle(String.valueOf(bookObj.get("title")));
            JSONObject userObj = (JSONObject) bookObj.get("user");
            metadata.setAuthor(String.valueOf(userObj.get("display_name")));
            metadata.setDescription(String.valueOf(bookObj.get("description")));
            JSONObject coverObj = (JSONObject) bookObj.get("cover");
            metadata.setBufferedCover(String.valueOf(coverObj.get("url")));

            List<String> subjects = new ArrayList<>();
            JSONArray chapterObjects = (JSONArray) bookObj.get("tag_names");
            for (Object o : chapterObjects) {
                JSONObject tagObj = (JSONObject) o;
                subjects.add(String.valueOf(tagObj.get("name")));
            }
            metadata.setSubjects(subjects);
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
