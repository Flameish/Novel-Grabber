package grabber;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import system.Config;

import java.util.ArrayList;
import java.util.List;

public class HostSettings {
    public String url;
    public String chapterLinkSelector;
    public String chapterContainer;
    public String bookTitleSelector;
    public String bookDescSelector;
    public String bookAuthorSelector;
    public String bookSubjectSelector;
    public String bookCoverSelector;
    public List<String> blacklistedTags = new ArrayList<>();

    public HostSettings(String domain) {
        JSONObject currentSite = (JSONObject) Config.siteSelectorsJSON.get(domain);
        url = String.valueOf(currentSite.get("url"));
        chapterLinkSelector = String.valueOf(currentSite.get("chapterLinkSelector"));
        chapterContainer = String.valueOf(currentSite.get("chapterContainer"));
        for(Object tagObject: (JSONArray) currentSite.get("blacklistedTags")) {
            blacklistedTags.add(tagObject.toString());
        }
        bookTitleSelector = String.valueOf(currentSite.get("bookTitleSelector"));
        bookDescSelector = String.valueOf(currentSite.get("bookDescSelector"));
        bookCoverSelector = String.valueOf(currentSite.get("bookCoverSelector"));
        bookAuthorSelector = String.valueOf(currentSite.get("bookAuthorSelector"));
        bookSubjectSelector = String.valueOf(currentSite.get("bookSubjectSelector"));
    }
}
