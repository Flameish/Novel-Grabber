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


public class royalroad_com implements Source {
    private final String name = "Royal Road";
    private final String url = "https://royalroad.com";
    private final String[] blacklistedTags = {};
    private final boolean canLogin = true;
    private final boolean canSearch = true;
    private boolean enabled = true;

    public royalroad_com() {}

    @Override
    public NovelMetadata fetchNovelMetadata(String url) throws SourceException {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException | IllegalArgumentException e) {
            throw new SourceException(e.getMessage(), e);
        }
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterLinks = doc.select("td:not([class]) a");

        for (Element chapterLink : chapterLinks) {
            chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }

        NovelMetadata metadata = new NovelMetadata();
        metadata.setChapterList(chapterList);

        Element title = doc.selectFirst("h1[property=name]");
        Element author = doc.selectFirst("h4 span[property=name] a");
        Element desc = doc.selectFirst(".description");
        Element cover = doc.selectFirst("img.thumbnail");
        Elements tags = doc.select("span.tags a");

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
            return doc.select(".chapter-content").first();
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    @Override
    public SearchResult[] search(String name) throws SourceException {
        Document doc;
        try {
             doc = Jsoup.connect("https://www.royalroad.com/fictions/search?title=" + name).get();
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
        Elements fictionList = doc.select(".fiction-list-item");
        SearchResult[] searchResults = new SearchResult[fictionList.size()];
        for (int i = 0; i < fictionList.size(); i++) {
            Element fiction = fictionList.get(i);
            SearchResult result = new SearchResult();

            Element url = fiction.selectFirst("h2.fiction-title a");
            if (url == null) continue; // a search result without novel url is useless
            Element title = fiction.selectFirst("h2.fiction-title");
            Element desc = fiction.selectFirst("div[id^=description]");
            Element cover = fiction.selectFirst("figure img");
            Elements tags = fiction.select("span.tags");

            result.setUrl(url.attr("abs:href"));
            result.setTitle(title != null ? title.text() : "");
            result.setDescription(desc != null ? desc.text() : "");
            result.setCoverImage(cover != null ? cover.attr("abs:src") : "");

            List<String> subjects = new ArrayList<>();
            for (Element tag : tags) {
                subjects.add(tag.text());
            }
            result.setSubjects(subjects);

            searchResults[i] = result;
        }

        return searchResults;
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
        return false;
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
