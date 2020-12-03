package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import system.data.accounts.Account;
import system.data.accounts.Accounts;
import system.init;

public class lnmtl_com implements Source {
    private final Novel novel;
    private Document toc;

    public lnmtl_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            // Get volume ids
            String lnmtlScript = toc.toString();
            int volArrStartIndex = lnmtlScript.indexOf("lnmtl.volumes = [{");
            int volArrEndIndex = lnmtlScript.indexOf("}];", volArrStartIndex)+3;
            String volArray = lnmtlScript.substring(volArrStartIndex, volArrEndIndex);
            Pattern pattern = Pattern.compile("\"id\":(.*?),", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(volArray);
            // Loop through volumes
            while (matcher.find()) {
                // Loop through pages inside volumes
                int page = 1;
                while(true) {
                    String json = Jsoup.connect("https://lnmtl.com/chapter?page=" + (page++) + "&volumeId=" + matcher.group(1))
                            .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                            .ignoreContentType(true)
                            .method(Connection.Method.GET)
                            .execute().body();
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
                    JSONArray chapterArray = (JSONArray) jsonObject.get("data");
                    for (Object chapterObj: chapterArray) {
                        JSONObject chapter = (JSONObject) chapterObj;
                        chapterList.add(new Chapter((String) chapter.get("title"), (String) chapter.get("site_url")));
                    }
                    if (jsonObject.get("next_page_url") == null) break;
                }
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
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR] Could not parse chapterlist!");
            }
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc;
            if(novel.cookies != null) {
                doc = Jsoup.connect(chapter.chapterURL)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .cookies(novel.cookies)
                        .get();
            } else {
                doc = Jsoup.connect(chapter.chapterURL)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
            }
            Elements unfixedSentences = doc.select(".translated");
            StringBuilder fixedChapter = new StringBuilder();
            for(Element sentence: unfixedSentences) {
                fixedChapter.append("<p>" + sentence.text() + "</p>");
            }
            chapterBody = Jsoup.parseBodyFragment(fixedChapter.toString());
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

        if(toc != null) {
            metadata.setTitle(toc.select("meta[property=og:title]").attr("content"));
            metadata.setAuthor(toc.select(".panel-body:contains(Authors) span").first().text());
            metadata.setDescription(toc.select(".description").first().text());
            metadata.setBufferedCover(toc.select("meta[property=og:image:url]").attr("content"));

            Elements tags = toc.select("div.panel:nth-child(4) > div:nth-child(2) > ul:nth-child(1) a");
            List<String> subjects = new ArrayList<>();
            for(Element tag: tags) {
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

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        System.out.println("[INFO] Login...");
        if(init.gui != null) {
            init.gui.appendText(novel.window,"[INFO] Login...");
        }
        try {
            Account account = Accounts.getInstance().getAccount("LNMTL");
            if(!account.getUsername().isEmpty()) {
                Connection.Response res = Jsoup.connect("https://lnmtl.com/auth/login")
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .method(Connection.Method.GET)
                        .execute();
                String token = res.parse().select("input[name=_token]").attr("value");
                res = Jsoup.connect("https://lnmtl.com/auth/login")
                        .data("email", account.getUsername())
                        .data("password", account.getPassword())
                        .data("_token", token)
                        .cookies(res.cookies())
                        .method(Connection.Method.POST)
                        .execute();
                return res.cookies();
            } else {
                System.out.println("[ERROR] No account found.");
                if(init.gui != null) {
                    init.gui.appendText(novel.window,"[ERROR] No account found.");
                }
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException();
    }

}
