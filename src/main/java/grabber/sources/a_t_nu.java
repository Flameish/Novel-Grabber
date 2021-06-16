package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
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

public class a_t_nu implements Source {
    private final String name = "Active Translations";
    private final String url = "https://a-t.nu/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public a_t_nu() {
    }

    public a_t_nu(Novel novel) {
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
            Connection.Response res = Jsoup.connect("https://a-t.nu/wp-admin/admin-ajax.php")
                    .data("action", "manga_get_chapters")
                    .data("manga", toc.select("a.wp-manga-action-button:has(i.fa-bell)").attr("data-post"))
                    .cookies(novel.cookies)
                    .method(Connection.Method.POST)
                    .execute();
            Elements chapterLinks = res.parse().select(".wp-manga-chapter:not(:has(i.fa-lock)) a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            Collections.reverse(chapterList);
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
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            chapterBody = doc.selectFirst(".text-left");
            // Chapter text is obfuscated by inserting parts via css.
            // Eg.:
            // .f55239af1c37bfd60de353c6394b7b8c8::before {content: 'Finally,'
            // .f55239af1c37bfd60de353c6394b7b8c8::after {content: 'city.'
            Element styleContent = chapterBody.selectFirst("style");
            String[] insertStrings = styleContent.html().split(";}");
            for (String insertString: insertStrings) {
                if (insertString.startsWith(".")) {
                    String className = insertString.substring(0, insertString.indexOf("::"));
                    String content = insertString.substring(insertString.indexOf(": '")+3, insertString.length()-1);
                    content = content.replaceAll("\\\\a0", "");
                    Element elmntInBody = chapterBody.selectFirst(className);
                    if (insertString.contains("::before")) {
                        elmntInBody.prependText(content);
                    } else {
                        elmntInBody.appendText(content);
                    }
                }
            }
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
            Element desc = toc.selectFirst("meta[property=og:description]");
            Element cover = toc.selectFirst("meta[property=og:image]");

            metadata.setTitle(title != null ? title.attr("content") : "");
            metadata.setDescription(desc != null ? desc.attr("content") : "");
            metadata.setBufferedCover(cover != null ? cover.attr("content") : "");
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

}
