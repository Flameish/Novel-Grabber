package grabber.sources;

import grabber.novel.NovelMetadata;
import org.jsoup.nodes.Element;
import search.SearchResult;


public interface Source {
    String getName();
    String getUrl();
    boolean canLogin();
    boolean canSearch();
    boolean isEnabled();
    void setEnabled(boolean enabled);
    String[] getBlacklistedTags();
    NovelMetadata fetchNovelMetadata(String url) throws SourceException;
    Element fetchChapterBody(String url) throws SourceException;
    SearchResult[] search(String name) throws SourceException;

}
