package grabber.sources.domains;

import grabber.novel.Chapter;
import grabber.novel.NovelMetadata;
import grabber.sources.Source;
import grabber.sources.SourceException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import search.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class wuxiaworld_com implements Source {
    private final String name = "Wuxiaworld.com";
    private final String url = "https://wuxiaworld.com";
    private final String[] blacklistedTags = {"a.chapter-nav"};
    private final boolean canLogin = false;
    private final boolean canSearch = false;
    private boolean enabled = true;

    public wuxiaworld_com() {}

    @Override
    public NovelMetadata fetchNovelMetadata(String url) throws SourceException {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterLinks = doc.select("#accordion .chapter-item a");

        for (Element chapterLink : chapterLinks) {
            chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }

        NovelMetadata metadata = new NovelMetadata();
        metadata.setChapterList(chapterList);

        Element title = doc.selectFirst(".novel-body h2");
        Element author = doc.selectFirst(".novel-body div:contains(Author) dd");
        Element desc = doc.selectFirst(".fr-view:not(.pt-10)");
        Element cover = doc.selectFirst(".novel-left img");
        Elements tags = doc.select(".genres a");

        metadata.setUrl(url);
        metadata.setTitle(title != null ? title.text() : "");
        metadata.setAuthor(author != null ? author.text() : "");
        metadata.setDescription(desc != null ? desc.text() : "");
        metadata.setCoverImage(cover != null ? cover.attr("abs:src") : "");

        List<String> subjects = new ArrayList<>();
        for (Element tag : tags) {
            subjects.add(tag.text());
        }
        metadata.setSubjects(subjects);

        return metadata;
    }


    @Override
    public Element fetchChapterBody(String chapterUrl) throws SourceException {
        try {
            Document doc = Jsoup.connect(chapterUrl).get();
            return doc.select(".p-15 .fr-view").first();
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    @Override
    public SearchResult[] search(String name) throws SourceException {
        return new SearchResult[0];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public boolean canLogin() {
        return canLogin;
    }

    @Override
    public boolean canSearch() {
        return canSearch;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String[] getBlacklistedTags() {
        return blacklistedTags;
    }
}
