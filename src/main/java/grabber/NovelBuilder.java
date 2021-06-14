package grabber;

import bots.telegram.DownloadTask;
import grabber.sources.Source;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


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
     */
    public NovelBuilder fromCLI(Map<String, List<String>> params) throws IOException, ClassNotFoundException {
        novel.novelLink = params.get("link").get(0);
        novel.source = setSource(novel.novelLink).novel.source; //It's ugly, I know
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
                case "headless":
                    novel.browser = "Headless";
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
        if(params.containsKey("noDesc")) {
            novel.noDescription =  true;
        }
        if(params.containsKey("login")) {
            novel.useAccount = true;
        }
        return this;
    }

    public Novel build() {
        return novel;
    }
    public NovelBuilder novelLink(String novelLink) {
        novel.novelLink = novelLink;
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
    public NovelBuilder getImages(boolean getImages) {
        novel.getImages = getImages;
        return this;
    }
    public NovelBuilder displayChapterTitle(boolean displayChapterTitle) {
        novel.displayChapterTitle = displayChapterTitle;
        return this;
    }
    public NovelBuilder useHeadless(boolean useHeadless) {
        novel.useHeadless = useHeadless;
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
    public NovelBuilder downloadTask(DownloadTask downloadTask) {
        novel.downloadTask = downloadTask;
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

    public NovelBuilder setSource() throws ClassNotFoundException, IOException {
        Source source;
        try {
            String sourcesFolder = GrabberUtils.getCurrentPath() + "/sources";
            File dir = new File(sourcesFolder);
            URL loadPath = dir.toURI().toURL();
            URL[] urls = new URL[]{loadPath};
            URLClassLoader classLoader = new URLClassLoader(urls);

            source = (Source) classLoader.loadClass("grabber.sources.manualSource")
                    .getConstructor(Novel.class)
                    .newInstance(novel);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Manual source not found.", e);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                MalformedURLException | IllegalAccessException e) {
            throw new IOException("Could not access or load source file(s)", e);
        }
        novel.source = source;
        return this;
    }

    public NovelBuilder setSource(String hostUrl) throws ClassNotFoundException, IOException {
        Source source;
        try {
            String sourcesFolder = GrabberUtils.getCurrentPath() + "/sources";
            File dir = new File(sourcesFolder);
            URL loadPath = dir.toURI().toURL();
            URL[] urls = new URL[]{loadPath};
            URLClassLoader classLoader = new URLClassLoader(urls);

            String domain = GrabberUtils.getDomainName(hostUrl)
                    .replaceAll("[^A-Za-z0-9]", "_");
            // Supported sources have their domain name as their class name and java does not allow class names
            // to start with digits, which is possible for domain names, we need to add a 'n' for number in front.
            if (domain.substring(0, 1).matches("\\d")) domain = "n" + domain;
            source = (Source) classLoader.loadClass("grabber.sources." + domain)
                    .getConstructor(Novel.class)
                    .newInstance(novel);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Host not supported: " + GrabberUtils.getDomainName(novel.novelLink), e);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                MalformedURLException | IllegalAccessException e) {
            throw new IOException("Could not access or load source file(s)", e);
        }
        novel.source = source;
        return this;
    }
}
