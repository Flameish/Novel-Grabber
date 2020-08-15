package grabber;

import system.data.accounts.Account;
import system.data.accounts.Accounts;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static grabber.GrabberUtils.getDomain;

/**
 * Novel builder and modifier.
 */
public class NovelBuilder {
    private Novel novel;

    public NovelBuilder() {
        novel = new Novel();
    }

    public NovelBuilder(Novel novel) {
        this.novel = novel;
    }

    /**
     * Set novel options from a given CLI string.
     * @param params
     * @return
     */
    public NovelBuilder fromCLI(Map<String, List<String>> params) {
        novel.novelLink = params.get("link").get(0);
        novel.hostname = getDomain(novel.novelLink.substring(0, GrabberUtils.ordinalIndexOf(novel.novelLink, "/", 3) + 1));
        novel.window = "auto";
        novel.displayChapterTitle = params.containsKey("displayTitle");
        novel.reverseOrder = params.containsKey("invertOrder");

        if(params.containsKey("headless")) {
            novel.useHeadless = true;
            switch (params.get("headless").get(0).toLowerCase()) {
                case "firefox":
                    novel.browser = "Firefox";
                    break;
                case "chrome":
                    novel.browser = "Chrome";
                    break;
                case "edge":
                    novel.browser = "Edge";
                    break;
                case "opera":
                    novel.browser = "Opera";
                    break;
                case "ie":
                    novel.browser = "IE";
                    break;
            }
            if(params.get("headless").size() > 1) {
                novel.headlessGUI = params.get("headless").get(1).toLowerCase().equals("gui");
            }
        }

        if(params.containsKey("path")) {
            novel.saveLocation = params.get("path").get(0);
        } else {
            Path currentRelativePath = Paths.get("");
            novel.saveLocation = currentRelativePath.toAbsolutePath().toString();
        }
        if(params.containsKey("wait")) {
            novel.waitTime =  Integer.parseInt(params.get("wait").get(0));
        }
        if(params.containsKey("autoGetImages")) {
            novel.getImages =  true;
        }
        if(params.containsKey("window")) {
            novel.window =  params.get("window").get(0);
        }
        if(params.containsKey("removeStyle")) {
            novel.removeStyling =  true;
        }
        if(params.containsKey("noDesc")) {
            novel.noDescription =  true;
        }
        if(params.containsKey("login")) {
            novel.useAccount = true;
        }
        if(params.containsKey("account")) {
            Account newAccount = Accounts.getInstance().getAccount(novel.hostname);
            Accounts.getInstance().addAccount(newAccount);
        }
        return this;
    }

    public Novel build() {
        novel.setHostSelectors();
        return novel;
    }
    public NovelBuilder novelLink(String novelLink) {
        novel.novelLink = novelLink;
        novel.hostname = getDomain(novelLink.substring(0, GrabberUtils.ordinalIndexOf(novelLink, "/", 3) + 1));
        return this;
    }
    public NovelBuilder window(String window) {
        novel.window = window;
        return this;
    }
    public NovelBuilder saveLocation(String saveLocation) {
        novel.saveLocation = saveLocation;
        return this;
    }
    public NovelBuilder browser(String browser) {
        novel.browser = browser;
        return this;
    }
    public NovelBuilder removeStyling(boolean removeStyling) {
        novel.removeStyling = removeStyling;
        return this;
    }
    public NovelBuilder getImages(boolean getImages) {
        novel.getImages = getImages;
        return this;
    }
    public NovelBuilder displayChapterTitle(boolean displayChapterTitle) {
        novel.displayChapterTitle = displayChapterTitle;
        return this;
    }
    public NovelBuilder reverseOrder(boolean reverseOrder) {
        novel.reverseOrder = reverseOrder;
        return this;
    }
    public NovelBuilder useHeadless(boolean useHeadless) {
        novel.useHeadless = useHeadless;
        return this;
    }
    public NovelBuilder headlessGUI(boolean headlessGUI) {
        novel.headlessGUI = headlessGUI;
        return this;
    }
    public NovelBuilder useAccount(boolean useAccount) {
        novel.useAccount = useAccount;
        return this;
    }
    public NovelBuilder autoDetectContainer(boolean autoDetectContainer) {
        novel.autoDetectContainer = autoDetectContainer;
        return this;
    }
    public NovelBuilder waitTime(int waitTime) {
        novel.waitTime = waitTime;
        return this;
    }
    public NovelBuilder firstChapter(int firstChapter) {
        novel.firstChapter = firstChapter;
        return this;
    }
    public NovelBuilder lastChapter(int lastChapter) {
        novel.lastChapter = lastChapter;
        return this;
    }
    public NovelBuilder blacklistedTags(List<String> blacklistedTags) {
        novel.blacklistedTags = blacklistedTags;
        return this;
    }
    public NovelBuilder chapterContainer(String chapterContainer) {
        novel.chapterContainer = chapterContainer;
        return this;
    }
}
