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
import java.util.Collections;
import java.util.List;


public class scribblehub_com implements Source {
    private final String name = "Scribble Hub";
    private final String url = "https://scribblehub.com";
    private final String[] blacklistedTags = {};
    private final boolean canLogin = true;
    private final boolean canSearch = true;
    private boolean enabled = true;

    public scribblehub_com() {}

    @Override
    public NovelMetadata fetchNovelMetadata(String url) throws SourceException {
        Document doc;
        try {
            doc = Jsoup.connect(url).cookie("toc_show", "9999").get();
        } catch (IOException | IllegalArgumentException e) {
            throw new SourceException(e.getMessage(), e);
        }
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapterLinks = doc.select("a.toc_a");

        for (Element chapterLink : chapterLinks) {
            chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        Collections.reverse(chapterList);

        NovelMetadata metadata = new NovelMetadata();
        metadata.setChapterList(chapterList);

        Element title = doc.selectFirst(".fic_title");
        Element author = doc.selectFirst(".auth_name_fic");
        Element desc = doc.selectFirst(".wi_fic_desc");
        Element cover = doc.selectFirst(".fic_image img");
        Elements tags = doc.select(".wi_fic_genre a");

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
            return doc.select("#chp_raw").first();
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    @Override
    public SearchResult[] search(String name) throws SourceException {
        Document doc;
        try {
             doc = Jsoup.connect(String.format("https://www.scribblehub.com/?s=%s&post_type=fictionposts", name)).get();
        } catch (IOException e) {
            throw new SourceException(e.getMessage(), e);
        }
        Elements fictionList = doc.select(".search_main_box");
        SearchResult[] searchResults = new SearchResult[fictionList.size()];
        for (int i = 0; i < fictionList.size(); i++) {
            Element fiction = fictionList.get(i);
            SearchResult result = new SearchResult();

            Element url = fiction.selectFirst(".search_title a");
            if (url == null) continue; // a search result without novel url is useless
            Element title = fiction.selectFirst(".search_title a");
            Element cover = fiction.selectFirst(".search_img img");
            Elements tags = fiction.select(".search_genre a");

            result.setUrl(url.attr("abs:href"));
            result.setTitle(title != null ? title.text() : "");
            result.setDescription(fiction.clone().children().remove().text());
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
