package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class archiveofourown_org implements Source{
    private final String name = "AO3";
    private final String url = "https://archiveofourown.org/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;
    private Document info;

    public archiveofourown_org(Novel novel) {
        this.novel = novel;
    }
    public archiveofourown_org(){}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public boolean canHeadless() {
        return canHeadless;
    }
    /**
        Must only be url/works/*novel id* cause AO3 is weird with navigation
     */
    @Override
    public List<Chapter> getChapterList() throws IllegalArgumentException{
        List<Chapter> chapList = new ArrayList<>();
        if (novel.novelLink.contains("chapters")){
            GrabberUtils.err(novel.window, "You included a link directly to the first chapter of a AO3 novel\n" +
                    "Remove the /chapters/*chapter id* and try again");
            throw new IllegalArgumentException();
        }
        try {
            toc = Jsoup.connect(novel.novelLink + "/navigate")
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            Elements chaps = toc.select("ol.chapter index group > li > a");
            for (Element e : chaps) {
                chapList.add(new Chapter(e.text(), e.attr("abs:href")));
            }
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapList;
    }

    @Override
    public Element getChapterContent(Chapter chapter) {
        Element body = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            info = doc;
            body = doc.selectFirst("div.userstuff module");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    @Override
    public NovelMetadata getMetadata() {
         NovelMetadata metadata = new NovelMetadata();
         if (toc != null) {
             Element author = info.selectFirst("div.preface group > h3 > a");
             Element title = info.select("div.preface group > h2.title heading").first();
             Element description = info.selectFirst("div.summary module > blockquote > p");
             //AO3 has no covers for novels
             metadata.setDescription(description.text());
             metadata.setTitle(title.text());
             metadata.setAuthor(author.text());
         }
         return metadata;
    }

    @Override
    public List<String> getBlacklistedTags() {
        List<String> tags = new ArrayList<>();
        tags.add("div.preface group");
        return tags;
    }
}
