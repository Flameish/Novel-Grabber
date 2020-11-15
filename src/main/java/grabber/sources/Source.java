package grabber.sources;

import grabber.Chapter;
import grabber.NovelMetadata;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Map;

public interface Source {
    List<Chapter> getChapterList();
    Element getChapterContent(Chapter chapter);
    NovelMetadata getMetadata();
    List<String> getBlacklistedTags();
    Map<String, String> getLoginCookies() throws UnsupportedOperationException;
}
