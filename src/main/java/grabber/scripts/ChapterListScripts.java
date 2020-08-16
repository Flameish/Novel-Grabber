package grabber.scripts;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 *  Custom scripts to fetch chapter lists from host sites.
 */
public class ChapterListScripts {
    public static void getList(Novel novel) {
        System.out.println("[GRABBER]Fetching chapterlist...");
        if(init.gui != null) {
            init.gui.appendText(novel.window, "[GRABBER]Fetching novel info...");
        }
        try {
            switch(novel.url) {
                case "https://mtlnovel.com/":
                    novel.chapterList = mtlnovels(novel);
                    break;
                case "https://boxnovel.com/":
                    novel.chapterList = boxnovel(novel);
                    break;
                case "http://novelfull.com/":
                    novel.chapterList = novelfull(novel);
                    break;
                case "https://creativenovels.com/":
                    novel.chapterList = creativenovels(novel);
                    break;
                case "https://zenithnovels.com/":
                    novel.chapterList = zenithnovels(novel);
                    break;
                case "https://translatinotaku.net/":
                    novel.chapterList = translatinotaku(novel);
                    break;
                case "https://comrademao.com/":
                    novel.chapterList = comrademao(novel);
                    break;
                case "https://fanfiction.net/":
                    novel.chapterList = fanfiction(novel);
                    break;
                case "https://fanfiktion.de/":
                    novel.chapterList = fanfiktion(novel);
                    break;
                case "https://tapread.com/":
                    novel.chapterList = tapread(novel);
                    break;
                case "https://webnovel.com/":
                    novel.chapterList = webnovel(novel);
                    break;
                case "https://booklat.com.ph/":
                    novel.chapterList = booklat(novel);
                    break;
                case "https://ficfun.com/":
                    novel.chapterList = ficfun(novel);
                    break;
                case "https://dreame.com/":
                    novel.chapterList = dreame(novel);
                    break;
                case "https://foxaholic.com/":
                    novel.chapterList = foxaholic(novel);
                    break;
                case "https://wordrain69.com/":
                    novel.chapterList = wordrain(novel);
                    break;
                case "https://scribblehub.com/":
                    novel.chapterList = scribblehub(novel);
                    break;
                case "https://novelupdates.com/":
                    novel.chapterList = novelupdates(novel);
                    break;
                default:
                    novel.chapterList = defaults(novel);
                    break;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Chapter> mtlnovels(Novel novel) throws IOException {
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Document temp = Jsoup.connect(novel.novelLink+"/chapter-list/").timeout(30 * 1000).get();
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink :temp.select(novel.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        Collections.reverse(chapters);
        return chapters;
    }

    private static List<Chapter> novelupdates(Novel novel) throws IOException {
        Connection.Response res = Jsoup.connect(novel.novelLink)
                .method(Connection.Method.GET)
                .timeout(30 * 1000)
                .execute();
        novel.tableOfContent = res.parse();
        res = Jsoup.connect("https://www.novelupdates.com/wp-admin/admin-ajax.php")
                .method(Connection.Method.POST)
                .timeout(30 * 1000)
                .cookies(res.cookies())
                .data("action", "nd_getchapters")
                .data("mypostid", novel.tableOfContent.select("#mypostid").attr("value"))
                .timeout(30 * 1000)
                .execute();
        Document doc = res.parse();
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : doc.select(novel.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        Collections.reverse(chapters);
        return chapters;
    }

    private static List<Chapter> scribblehub(Novel novel) throws IOException {
        novel.tableOfContent = Jsoup.connect(novel.novelLink).cookie("toc_show","9999").timeout(30 * 1000).get();
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        Collections.reverse(chapters);
        return chapters;
    }


    private static List<Chapter> ficfun(Novel novel) {
        List<Chapter> chapters = new ArrayList<>();
        try {
            novel.tableOfContent = Jsoup.connect(novel.novelLink).get();
            String firstChapterUrl = novel.tableOfContent.select(".js-readBook-btn").attr("abs:href");
            novel.tempPage = Jsoup.connect(firstChapterUrl).get();
            for (Element chapterLink : novel.tempPage.select(novel.chapterLinkSelector)) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapters;
    }

    private static List<Chapter> dreame(Novel novel) {
        List<Chapter> chapters = new ArrayList<>();
        try {
            novel.tableOfContent = Jsoup.connect(novel.novelLink).get();
            String firstChapterUrl = novel.tableOfContent.select(".js-readBook-btn").attr("abs:href");
            novel.tempPage = Jsoup.connect(firstChapterUrl).get();
            for (Element chapterLink : novel.tempPage.select(novel.chapterLinkSelector)) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapters;
    }

    private static List<Chapter> creativenovels(Novel novel) throws IOException {
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Connection.Response res = Jsoup.connect("https://creativenovels.com/wp-admin/admin-ajax.php")
                .method(Connection.Method.POST)
                .timeout(30 * 1000)
                .data("action", "crn_chapter_list")
                .data("view_id", novel.tableOfContent.select("#chapter_list_novel_page").attr("class"))
                .execute();
        Document doc = res.parse();
        String ajaxResp = doc.select("body").toString();
        ajaxResp = ajaxResp.replaceAll("success.define.","");
        ajaxResp = ajaxResp.replaceAll(".data.available.end_data.","");
        String[] test = ajaxResp.split(".data.");
        List<String> names = new ArrayList<>();
        List<String> links = new ArrayList<>();
        for (String line: test) {
            if(line.contains("locked.end")) break;
            if(line.contains("http")) {
                links.add(line.substring(line.indexOf("http")));
            } else {
                names.add(line);
            }
        }
        names.remove(names.size()-1);

        List<Chapter> chapters = new ArrayList<>();
        for(int i = 0; i < links.size(); i++) {
            chapters.add(new Chapter(names.get(i),links.get(i)));
        }
        return chapters;
    }

    private static List<Chapter> foxaholic(Novel novel) throws IOException {
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Connection.Response res = Jsoup.connect("https://foxaholic.com/wp-admin/admin-ajax.php")
                .method(Connection.Method.POST)
                .referrer("novel.novelLink")
                .timeout(30 * 1000)
                .data("action", "manga_get_chapters")
                .data("manga", novel.tableOfContent.select("#manga-chapters-holder").attr("data-id"))
                .execute();

        Document doc = res.parse();
        List<Chapter> chapters = new ArrayList<>();
        for(Element link: doc.select(novel.chapterLinkSelector)) {
            chapters.add(new Chapter(link.text(), link.attr("abs:href")));
        }
        Collections.reverse(chapters);
        return chapters;
    }

    private static List<Chapter> wordrain(Novel novel) throws IOException {
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Connection.Response res = Jsoup.connect("https://wordrain69.com/wp-admin/admin-ajax.php")
                .method(Connection.Method.POST)
                .referrer("novel.novelLink")
                .timeout(30 * 1000)
                .data("action", "manga_get_chapters")
                .data("manga", novel.tableOfContent.select("#manga-chapters-holder").attr("data-id"))
                .execute();

        Document doc = res.parse();
        List<Chapter> chapters = new ArrayList<>();
        for(Element link: doc.select(novel.chapterLinkSelector)) {
            chapters.add(new Chapter(link.text(), link.attr("abs:href")));
        }
        Collections.reverse(chapters);
        return chapters;
    }

    private static List<Chapter> boxnovel(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
        for(Element chapterLink: chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        String boxNovelFirstChapter = chapters.get(chapters.size()-1).chapterURL;
        String boxNovelbaseLinkStart = boxNovelFirstChapter.substring(0, GrabberUtils.ordinalIndexOf(boxNovelFirstChapter, "/", 5) + 9);
        String boxNovelChapterNumberString = boxNovelFirstChapter.substring(boxNovelbaseLinkStart.length());
        int boxNovelChapterNumber;
        if(boxNovelChapterNumberString.contains("-")) {
            boxNovelChapterNumber = Integer.valueOf(boxNovelChapterNumberString.substring(0,boxNovelChapterNumberString.indexOf("-")));
        } else {
            boxNovelChapterNumber = Integer.valueOf(boxNovelChapterNumberString);
        }
        if (boxNovelChapterNumber != 1) {
            for (int i = boxNovelChapterNumber - 1; i >= 1; i--) {
                chapters.add(new Chapter("Chapter " + i, boxNovelbaseLinkStart + i));
            }
        }
        Collections.reverse(chapters);
        return chapters;
    }

    private static List<Chapter> novelfull(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks;
        while (!novel.tableOfContent.select("li.next").hasClass("disabled")) {
            chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
            for (Element chapterLink : chapterLinks) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select("li.next a").attr("abs:href")).timeout(30 * 1000).get();
        }
        chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> zenithnovels(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks;
        while (!novel.tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href").isEmpty()) {
            chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
            for (Element chapterLink : chapterLinks) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href")).timeout(30 * 1000).get();
        }
        chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        Collections.reverse(chapters);
        return chapters;
    }

    private static List<Chapter> translatinotaku(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks;
        while (!novel.tableOfContent.select("a.page-numbers.next").attr("abs:href").isEmpty()) {
            chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
            for (Element chapterLink : chapterLinks) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select("a.page-numbers.next").attr("abs:href")).timeout(30 * 1000).get();
        }
        chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));

        }
        return chapters;
    }

    private static List<Chapter> comrademao(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks;
        while (!novel.tableOfContent.select(".pagination a.next").attr("abs:href").isEmpty()) {
            chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
            for (Element chapterLink : chapterLinks) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select(".pagination a.next").attr("abs:href")).timeout(30 * 1000).get();
        }
        chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        Collections.reverse(chapters);
        return chapters;
    }

    private static List<Chapter> fanfiction(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
        String fullLink = novel.tableOfContent.select("link[rel=canonical]").attr("abs:href");
        String baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
        String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
        chapterLinks = chapterLinks.select("option[value]");
        for(int i = 0;  i < chapterLinks.size() / 2; i++)
            chapters.add(new Chapter(chapterLinks.get(i).text(),baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
        return chapters;
    }

    private static List<Chapter> fanfiktion(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks = novel.tableOfContent.select(novel.chapterLinkSelector);
        String fullLink = novel.tableOfContent.select("link[rel=canonical]").attr("abs:href");
        String baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
        String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
        chapterLinks = chapterLinks.select("option[value]");
        for(int i = 0;  i < chapterLinks.size(); i++)
            chapters.add(new Chapter(chapterLinks.get(i).text(),baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
        return chapters;
    }

    private static List<Chapter> tapread(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        String novelURL = novel.novelLink;
        int tapReadNovelId = Integer.parseInt(novelURL.substring(novelURL.lastIndexOf("/") + 1));
        Map<String, String> chapterMap = null;
        try {
            JSONParser tapreadParser = new JSONParser();
            String json = Jsoup.connect("https://www.tapread.com/book/contents")
                    .ignoreContentType(true)
                    .data("bookId", String.valueOf(tapReadNovelId))
                    .method(Connection.Method.POST)
                    .execute().body();
            Object obj = tapreadParser.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject results = (JSONObject) jsonObject.get("result");
            JSONArray chapterObjects = (JSONArray) results.get("chapterList");
            chapterMap = new LinkedHashMap<>();
            for (Object o : chapterObjects) {
                JSONObject slide = (JSONObject) o;
                String chapterId = String.valueOf(slide.get("chapterId"));
                String chapterName = String.valueOf(slide.get("chapterName"));
                String chapterLocked = String.valueOf(slide.get("lock"));
                if (chapterLocked.equals("0")) chapterMap.put(chapterId, chapterName);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        int i = 0;
        for (String chapterId : chapterMap.keySet()) {
            chapters.add(new Chapter(chapterMap.get(chapterId), "https://tapread.com/book/index/" + tapReadNovelId + "/" + chapterId));
            chapters.get(i).xhrBookId = tapReadNovelId;
            chapters.get(i).xhrChapterId = chapterId;
            i++;
        }
        return chapters;
    }

    private static List<Chapter> webnovel(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        String csrfToken = "null";
        String bookId = novel.novelLink;
        String bookTitle = novel.tableOfContent.select(novel.bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
        bookId = novel.tableOfContent.select("a#j_read").attr("data-report-bid");

        String otherParameter = "";
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(novel.novelLink);
        URLConnection connection = url.openConnection();
        connection.getContent();

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        for (HttpCookie cookie : cookies) {
            if (cookie.toString().startsWith("_csrfToken")) {
                csrfToken = cookie.toString().substring(11);
            }
        }
        Map<String, String> webnovelChapters = null;
        String httpGetString = "https://www.webnovel.com/apiajax/chapter/GetChapterList?_csrfToken=" + csrfToken + "&bookId=" + bookId + "&_=" + otherParameter;

        //xhrRequest http = new xhrRequest();
        JSONParser parser = new JSONParser();
        try {
            Document doc = Jsoup.connect(httpGetString).get();
            String jsonString = String.valueOf(doc.select("body").text());
            Object obj = parser.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject data = (JSONObject) jsonObject.get("data");
            JSONArray volumeItems = (JSONArray) data.get("volumeItems");
            webnovelChapters = new LinkedHashMap<>();

            for (Object o : volumeItems) {
                JSONObject chapterItem = (JSONObject) o;
                JSONArray chapterItems = (JSONArray) chapterItem.get("chapterItems");
                for (Object a : chapterItems) {
                    JSONObject slide = (JSONObject) a;
                    String chapterId = String.valueOf(slide.get("id"));
                    // Crude hotfix
                    String chapterName = String.valueOf(slide.get("name")).replaceAll("â€™", "\'");
                    String isVip = String.valueOf(slide.get("isVip"));
                    if (isVip.equals("0")) {
                        webnovelChapters.put(chapterId, chapterName);
                    }
                }
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        int webnovelChapterNumber = 1;
        for (String chapterId : webnovelChapters.keySet()) {
            chapters.add(new Chapter("Chapter " + webnovelChapterNumber + ": " + webnovelChapters.get(chapterId), "https://www.webnovel.com/book/" + bookId + "/" + chapterId + "/"
                    + bookTitle.replace(" ", "-") + "/" + webnovelChapters.get(chapterId).replace(" ", "-")));
            webnovelChapterNumber++;
        }
        return chapters;
    }

    private static List<Chapter> booklat(Novel novel) throws IOException {
        novel.tempPage = Jsoup.connect(novel.novelLink+"/chapters").cookies(novel.cookies).timeout(30 * 1000).get();
        novel.tableOfContent = Jsoup.connect(novel.tempPage.select("#lnkRead").attr("abs:href")).cookies(novel.cookies).timeout(30 * 1000).get();
        Elements chaptersLinks = novel.tableOfContent.select("#ddChapter option[value]");
        List<Chapter> chapters = new ArrayList<>();
        for(Element chapterLink: chaptersLinks) {
            chapters.add(new Chapter(
                    chapterLink.text(),
                    novel.novelLink.replace("/Info/", "/Read/") + "/" + chapterLink.attr("value")));
        }
        novel.tableOfContent = novel.tempPage;
        return chapters;
    }

    private static List<Chapter> defaults(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();

        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        for (Element chapterLink : novel.tableOfContent.select(novel.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        if(novel.url.equals("https://liberspark.com/")
                || novel.url.equals("https://wuxiaworld.site/")
                || novel.url.equals("https://wuxiaworld.online/")
                || novel.url.equals("https://wordexcerpt.com/")) {
            Collections.reverse(chapters);
        }
        return chapters;
    }
}
