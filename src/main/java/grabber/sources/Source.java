package grabber.sources;

import grabber.Chapter;
import grabber.NovelMetadata;
import org.jsoup.nodes.Element;

import java.util.List;

public interface Source {
    String getName();

    String getUrl();

    boolean canHeadless();

    List<Chapter> getChapterList();

    Element getChapterContent(Chapter chapter);

    NovelMetadata getMetadata();

    List<String> getBlacklistedTags();

    String toString();
}
