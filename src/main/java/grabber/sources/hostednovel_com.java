package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class hostednovel_com implements Source {
    private final String name = "Hosted Novel";
    private final String url = "https://hostednovel.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public hostednovel_com() {
    }

    public hostednovel_com(Novel novel) {
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
            String canonLink = toc.selectFirst("a.nav-link:contains(About)").attr("abs:href");
            // Get chapter group ids to build toc links for chapter intervals (201 - 300 etc)
            Document chapterListPage;
            Elements chapterGroups = toc.select(".chaptergroup");
            for(Element chapterGroup: chapterGroups) {
                Set<String> cssClassNames = chapterGroup.classNames();
                // Visit chapter list for each entry
                for(String name : cssClassNames) {
                    if (!name.contains("chaptergroup-")) continue;
                    String groupId = name.substring(name.indexOf("-")+1);
                    chapterListPage = Jsoup.connect(canonLink + "/chapters/" + groupId)
                            .cookies(novel.cookies)
                            .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                            .get();
                    Elements chapterLinks = chapterListPage.select("div#chapterlist-" + groupId + " a");
                    for (Element chapterLink : chapterLinks) {
                        chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
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
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            chapterBody = doc.selectFirst("#chapter");
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
            Element title = toc.selectFirst("h1");
            Element author = toc.selectFirst("p:contains(Written By)");
            Element desc = toc.selectFirst(".card-body div");
            Element cover = toc.selectFirst(".cover-image");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text().replace("Written By:", "") : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:src") : "");
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

}
