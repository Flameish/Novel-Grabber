package grabber;

import gui.GUI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Novel {
    public GUI gui;
    public List<Chapter> chapters;
    public Metadata metadata;
    public Options options;
    public HostSettings host;
    public Driver headless;

    Document tableOfContent;
    public String novelLink;
    String nextChapterBtn;
    String nextChapterURL;
    public boolean killTask;
    private boolean reGrab = false;
    List<String> extraPages = new ArrayList<>();
    List<String> imageLinks = new ArrayList<>();
    List<String> imageNames = new ArrayList<>();

    private static final String NL = System.getProperty("line.separator");
    static final String htmlHead = "<!DOCTYPE html>" + NL +
            "<html>" + NL +
            "<head>" + NL +
            "<title></title>" + NL +
            "</head>" + NL +
            "<body>" + NL;
    static final String htmlFoot = "</body>" + NL + "</html>";

    public Novel() {}
    public Novel(GUI gui) {
        this.gui = gui;
        String hostname = gui.autoHostSelection.getSelectedItem().toString().toLowerCase().replace(" ", "");
        novelLink  = gui.chapterListURL.getText();
        host = new HostSettings(hostname);
        metadata = new Metadata(this);
        options = new Options();
        chapters = new ArrayList();
    }

    public void getChapterList() {
        gui.appendText("auto", "[INFO]Fetching novel info...");
        try {
            // Headless
            if(options.headless) {
                headless = new Driver(this);
                chapters = headless.getChapterList();
            // Static
            } else {
                // Custom chapter selection
                switch (host.url) {
                    case "https://boxnovel.com/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        Elements chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                        for(Element chapterLink: chapterLinks) {
                            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                        }
                        // Get link of last chapter (first in novel context)
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
                        break;
                    case "http://novelfull.com/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        while (!tableOfContent.select("li.next").hasClass("disabled")) {
                            chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                            for (Element chapterLink : chapterLinks) {
                                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                            }
                            tableOfContent = Jsoup.connect(tableOfContent.select("li.next a").attr("abs:href")).timeout(30 * 1000).get();
                        }
                        chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                        for (Element chapterLink : chapterLinks) {
                            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                        }
                        break;
                    case "https://zenithnovels.com/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        while (!tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href").isEmpty()) {
                            chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                            for (Element chapterLink : chapterLinks) {
                                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                            }
                            tableOfContent = Jsoup.connect(tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href")).timeout(30 * 1000).get();
                        }
                        chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                        for (Element chapterLink : chapterLinks) {
                            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                        }
                        break;
                    case "https://translatinotaku.net/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        while (!tableOfContent.select("a.page-numbers.next").attr("abs:href").isEmpty()) {
                            chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                            for (Element chapterLink : chapterLinks) {
                                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                            }
                            tableOfContent = Jsoup.connect(tableOfContent.select("a.page-numbers.next").attr("abs:href")).timeout(30 * 1000).get();
                        }
                        chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                        for (Element chapterLink : chapterLinks) {
                            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));

                        }
                        break;
                    case "https://comrademao.com/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        while (!tableOfContent.select(".pagination a.next").attr("abs:href").isEmpty()) {
                            chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                            for (Element chapterLink : chapterLinks) {
                                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                            }
                            tableOfContent = Jsoup.connect(tableOfContent.select(".pagination a.next").attr("abs:href")).timeout(30 * 1000).get();
                        }
                        chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                        for (Element chapterLink : chapterLinks) {
                            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                        }
                        break;
                    case "https://wuxiaworld.online/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                        for (Element chapterLink : chapterLinks) {
                            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                        }
                        // Get href link of last chapter (first in novel context)
                        String wuxiaonlineFirstChapter = chapters.get(chapterLinks.size() - 1).chapterURL;
                        String wuxiaonlinebaseLinkStart = wuxiaonlineFirstChapter.substring(0, GrabberUtils.ordinalIndexOf(wuxiaonlineFirstChapter, "/", 4) + 9);
                        String wuxiaonlineChapterNumberString = wuxiaonlineFirstChapter.substring(wuxiaonlinebaseLinkStart.length());
                        int wuxiaonlineChapterNumber;
                        if(wuxiaonlineChapterNumberString.contains("-"))
                            wuxiaonlineChapterNumber = Integer.valueOf(wuxiaonlineChapterNumberString.substring(0,wuxiaonlineChapterNumberString.indexOf("-")));
                        else
                            wuxiaonlineChapterNumber = Integer.valueOf(wuxiaonlineChapterNumberString);
                        if(wuxiaonlineChapterNumber != 1) {
                            for(int i = wuxiaonlineChapterNumber-1; i >= 1; i--) {
                                chapters.add(new Chapter("Chapter "+i, wuxiaonlinebaseLinkStart+i));
                            }
                        }
                        break;
                    case "https://fanfiction.net/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                        String fullLink = tableOfContent.select("link[rel=canonical]").attr("abs:href");
                        String baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
                        String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
                        chapterLinks = chapterLinks.select("option[value]");
                        for(int i = 0;  i < chapterLinks.size() / 2; i++)
                            chapters.add(new Chapter(chapterLinks.get(i).text(),baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
                        break;
                        // Is a reskin of fanction.net
                    case "https://fanfiktion.de/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        chapterLinks = tableOfContent.select(host.chapterLinkSelecter);
                        fullLink = tableOfContent.select("link[rel=canonical]").attr("abs:href");
                        baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
                        baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
                        chapterLinks = chapterLinks.select("option[value]");
                        for(int i = 0;  i < chapterLinks.size(); i++)
                            chapters.add(new Chapter(chapterLinks.get(i).text(),baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
                        break;
                    case "https://tapread.com/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        String novelURL = gui.chapterListURL.getText();
                        int tapReadNovelId = Integer.parseInt(novelURL.substring(novelURL.lastIndexOf("/") + 1));
                        Map<String, String> chapterMap = xhrRequest.tapReadGetChapterList(tapReadNovelId);
                        int i = 0;
                        for (String chapterId : chapterMap.keySet()) {
                            chapters.add(new Chapter(chapterMap.get(chapterId), "https://tapread.com/book/index/" + tapReadNovelId + "/" + chapterId));
                            chapters.get(i).xhrBookId = tapReadNovelId;
                            chapters.get(i).xhrChapterId = chapterId;
                            i++;
                        }
                        break;
                    case "https://webnovel.com/":
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        String csrfToken = "null";
                        String bookId = gui.chapterListURL.getText();
                        String bookTitle = tableOfContent.select(host.bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
                        bookId = bookId.substring(GrabberUtils.ordinalIndexOf(bookId, "/", 4) + 1, GrabberUtils.ordinalIndexOf(bookId, "/", 5));

                        String otherParameter = "";
                        CookieManager cookieManager = new CookieManager();
                        CookieHandler.setDefault(cookieManager);

                        URL url = new URL(novelLink);
                        URLConnection connection = url.openConnection();
                        connection.getContent();

                        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                        for (HttpCookie cookie : cookies) {
                            if (cookie.toString().startsWith("_csrfToken")) {
                                csrfToken = cookie.toString().substring(11);
                            }
                        }
                        Map<String, String> webnovelChapters = xhrRequest.webnovelGetChapterList(
                                "https://www.webnovel.com/apiajax/chapter/GetChapterList?_csrfToken=" + csrfToken + "&bookId=" + bookId + "&_=" + otherParameter);
                        int webnovelChapterNumber = 1;
                        for (String chapterId : webnovelChapters.keySet()) {
                            chapters.add(new Chapter("Chapter " + webnovelChapterNumber + ": " + webnovelChapters.get(chapterId), "https://www.webnovel.com/book/" + bookId + "/" + chapterId + "/"
                                    + bookTitle.replace(" ", "-") + "/" + webnovelChapters.get(chapterId).replace(" ", "-")));
                            webnovelChapterNumber++;
                        }
                        break;

                    case "https://gravitytales.com/":
                        //Chapter list at gravitytales.com/Novel/chapters
                        tableOfContent = Jsoup.connect(novelLink+"/chapters").timeout(30 * 1000).get();
                        for (Element chapterLink : tableOfContent.select(host.chapterLinkSelecter)) {
                            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                        }
                        // Fetch "table of contents" page for metadata
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        break;
                    default:
                        tableOfContent = Jsoup.connect(novelLink).timeout(30 * 1000).get();
                        for (Element chapterLink : tableOfContent.select(host.chapterLinkSelecter)) {
                            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            gui.appendText("auto", "[ERROR]"+e.getMessage());
        }
    }

    public void downloadChapters() throws Exception {
        gui.setMaxProgress(options.window, options.lastChapter-options.firstChapter+1);
        if(reGrab) {
            metadata.wordCount = 0;
            for(Chapter chapter: chapters) chapter.status = 0;
        }
        if(options.invertOrder) Collections.reverse(chapters); // Will get un-reversed for potential re-grab in report();
        // -1 since chapter numbers start at 1
        if(options.headless)    headless = new Driver(this);
        for(int i = options.firstChapter-1; i < options.lastChapter; i++) {
            if(killTask) {
                // Remove already downloaded images and chapters
                try {
                    Path chaptersFolder = Paths.get(options.saveLocation + "/chapters");
                    Path imagesFolder = Paths.get(options.saveLocation + "/images");
                    if (Files.exists(imagesFolder)) GrabberUtils.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) GrabberUtils.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    gui.appendText(options.window, e.getMessage());
                }
                throw new Exception("Grabbing stopped.");
            }
            chapters.get(i).saveChapter(this);
            gui.updateProgress(options.window);
            GrabberUtils.sleep(options.waitTime);
        }
        reGrab = true;
    }

    public void getMetadata() {
        metadata.getTitle();
        metadata.getDesc();
        metadata.getAuthor();
        metadata.getTags();
        metadata.getChapterNumber();
        metadata.getCover();
    }

    /**
     * Extra pages for EPUB
      */
    public void createCoverPage() {
        // Write buffered cover to save location
        if (metadata.bufferedCover != null && metadata.bookCover != null) {
            try {
                File outputfile = new File(options.saveLocation + File.separator + "images" + File.separator + metadata.bufferedCoverName);
                if (!outputfile.exists()) outputfile.mkdirs();
                ImageIO.write(metadata.bufferedCover, metadata.bufferedCoverName.substring(metadata.bufferedCoverName.lastIndexOf(".") + 1), outputfile);
            } catch (IOException e) {
                gui.appendText(options.window, "[ERROR]Could not write cover image to file.");
            }
        }
        String fileName = "cover_Page";
        String filePath = options.saveLocation + File.separator + "chapters" + File.separator + fileName +".html";
        String imageName = metadata.bookCover;
        imageName = GrabberUtils.getFileName(imageName);
        try (PrintStream out = new PrintStream(filePath, "UTF-8")) {
            out.print(htmlHead + "<div class=\"cover\" style=\"padding: 0pt; margin:0pt; text-align: center; padding:0pt; margin: 0pt;\">" + NL);
            out.println("<img src=\"" + imageName + "\" class=\"cover.img\" style=\"width: 600px; height: 800px;\" />");
            out.print("</div>" + NL + htmlFoot);
            extraPages.add(fileName);
        } catch (IOException e) {
            gui.appendText(options.window, e.getMessage());
            e.printStackTrace();
        }
    }

    public void createToc() {
        String fileName = "table_of_contents";
        String filePath = options.saveLocation + File.separator + "chapters" + File.separator + fileName+  ".html";
        try (PrintStream out = new PrintStream(filePath , "UTF-8")) {
            out.print(htmlHead + "<b>Table of Contents</b>" + NL + "<p style=\"text-indent:0pt\">" + NL);
            for (Chapter chapter: chapters) {
                if(chapter.status == 1)
                    out.println("<a href=\"" + chapter.fileName + ".html\">" + chapter.name + "</a><br/>");
            }
            out.print("</p>" + NL + htmlFoot);
            extraPages.add(fileName);
        } catch (IOException e) {
            gui.appendText(options.window, e.getMessage());
            e.printStackTrace();
        }
    }

    public void createDescPage() {
        String fileName = "desc_Page";
        String filePath = options.saveLocation + File.separator + "chapters" + File.separator + fileName + ".html";
        try (PrintStream out = new PrintStream(filePath, "UTF-8")) {
            out.print(htmlHead + "<div><b>Description</b>" + NL);
            out.println("<p>" + metadata.bookDesc.get(0) + "</p>");
            out.print("</div>" + NL + htmlFoot);
            extraPages.add(fileName);
        } catch (IOException e) {
            gui.appendText(options.window, e.getMessage());
            e.printStackTrace();
        }
    }

    public void createEPUB() {
        EPUB epub = new EPUB(this);
    }

    /**
     Prints potential failed chapters. Reverses the chapter list again for next grabbing
     and closes the headless driver if used.
     */
    public void report() {
        gui.appendText(options.window, "[INFO]Finished.");
        if(options.invertOrder) Collections.reverse(chapters);
        // Print failed chapters
        for(Chapter chapter: chapters) {
            if(chapter.status == 2)
                gui.appendText(options.window,"[WARN]Failed to grab: " +chapter.name);
        }
        if(options.headless) headless.close();
    }
}