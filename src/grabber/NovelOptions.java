package grabber;

public class NovelOptions {
    public boolean removeStyling = false;
    public boolean getImages = false;
    public boolean displayChapterTitle = false;
    public boolean noDescription = false;
    public boolean invertOrder = false;
    public boolean headless = false;
    public boolean headlessGUI = false;
    public boolean useAccount = false;
    public int waitTime = 0;
    public int firstChapter;
    public int lastChapter;
    public String saveLocation;
    public String window;
    public String browser;
    public String hostname;
    public String novelLink;

    public NovelOptions() {
    }
}
