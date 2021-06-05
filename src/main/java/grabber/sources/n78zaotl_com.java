package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class n78zaotl_com implements Source {
    private final String name = "78ZaoTL";
    private final String url = "https://www.78zaotl.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public n78zaotl_com(){}
    public n78zaotl_com(Novel novel) {this.novel = novel;}

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

    @Override
    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList<>();
        try {
            toc = Jsoup.connect(novel.novelLink)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            Elements chapters = toc.select("div.row > div > p > a");
            for (Element e : chapters) {
                chapterList.add(new Chapter(e.text(), e.attr("abs:href")));
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

    @Override
    public Element getChapterContent(Chapter chapter) {
        Element body = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            body = doc.select("div.row > div").first();
        }catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return body;
    }

    @Override
    public NovelMetadata getMetadata() {
        NovelMetadata novelMetadata = new NovelMetadata();
        if (toc != null) {
                Element title = toc.selectFirst("div.container > h5");
                Elements desc = toc.select(".container > p");
                //no cover image provided
                //no author provided
                novelMetadata.setTitle(title != null ? title.text() : "");
                novelMetadata.setAuthor("");
                novelMetadata.setDescription(desc != null ? desc.text() : "");
                //Site doesn't use genre tags
            }
        return novelMetadata;
    }

    @Override
    public List<String> getBlacklistedTags() {
        List<String> tags = new ArrayList<>();
        tags.add("h6");
        return tags;
    }
}
