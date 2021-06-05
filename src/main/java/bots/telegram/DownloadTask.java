
package bots.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import grabber.CLI;
import grabber.GrabberUtils;
import grabber.Novel;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import system.Config;
import system.init;

public class DownloadTask {
    private TelegramBot bot;
    private Config config;
    private Novel novel;
    private long chatId;
    private int progressMsgId;
    private String messageTxt;
    private boolean ignoreLimits;
    private int chaptersDownloaded;

    public DownloadTask(String messageTxt, boolean ignoreLimits, long chatId) {
        this.bot = init.telegramBot.getBot();
        this.config = Config.getInstance();
        this.chaptersDownloaded = 0;
        this.messageTxt = messageTxt;
        this.ignoreLimits = ignoreLimits;
        this.chatId = chatId;
    }

    public void create() throws IOException, ClassNotFoundException, IllegalStateException {
        if (this.messageTxt.startsWith("http")) {
            this.createDefault();
        } else {
            this.createCLI();
        }

    }

    private void createDefault() throws IOException, ClassNotFoundException, IllegalStateException {
        this.novel = Novel.builder().novelLink(this.messageTxt).window("telegram").saveLocation("./telegram/requests/" + this.chatId).getImages(this.config.isTelegramImagesAllowed() || this.ignoreLimits).downloadTask(this).setSource(this.messageTxt).waitTime(this.ignoreLimits ? 0 : this.config.getTelegramWait()).build();
        this.novel.check();
        if (this.novel.chapterList.isEmpty()) {
            throw new IllegalStateException("Chapter list empty.");
        } else {
            this.novel = Novel.modifier(this.novel).firstChapter(1).lastChapter(this.novel.chapterList.size()).build();
        }
    }

    private void createCLI() throws IOException, ClassNotFoundException, IllegalStateException {
        String[] args = CLI.createArgsFromString(this.messageTxt);
        Map params = CLI.createParamsFromArgs(args);

        int waitTime;
        try {
            waitTime = Integer.parseInt((String)((List)params.get("wait")).get(0));
            if (waitTime < this.config.getTelegramWait() && !this.ignoreLimits) {
                waitTime = this.config.getTelegramWait();
            }

            if (waitTime > 10000) {
                waitTime = 10000;
            }
        } catch (NullPointerException | NumberFormatException var5) {
            waitTime = this.config.getTelegramWait();
        }

        this.novel = Novel.builder().fromCLI(params).window("telegram").useHeadless(false).useAccount(false).getImages(this.config.isTelegramImagesAllowed() && params.containsKey("getImages") || this.ignoreLimits).downloadTask(this).saveLocation("./telegram/requests/" + this.chatId).waitTime(waitTime).build();
        this.novel.check();
        if (this.novel.chapterList.isEmpty()) {
            throw new IllegalStateException("Chapter list empty.");
        } else {
            if (params.containsKey("chapters")) {
                if (((String)((List)params.get("chapters")).get(0)).equals("all")) {
                    this.novel.firstChapter = 1;
                    this.novel.lastChapter = this.novel.chapterList.size();
                } else {
                    this.novel.firstChapter = Integer.parseInt((String)((List)params.get("chapters")).get(0));
                    if (((String)((List)params.get("chapters")).get(1)).equals("last")) {
                        this.novel.lastChapter = this.novel.chapterList.size();
                    } else {
                        this.novel.lastChapter = Integer.parseInt((String)((List)params.get("chapters")).get(1));
                    }
                }
            } else {
                this.novel.firstChapter = 1;
                this.novel.lastChapter = this.novel.chapterList.size();
            }

        }
    }

    public boolean isInBudget(int chaptersDownloaded) {
        int chaptersToDownload = this.novel.lastChapter - this.novel.firstChapter + 1;
        if (chaptersToDownload > this.config.getTelegramNovelMaxChapter() && !this.ignoreLimits && this.config.getTelegramNovelMaxChapter() != -1) {
            this.bot.execute(new SendMessage(this.chatId, "Above chapter limit! (max. chapter per novel: " + this.config.getTelegramNovelMaxChapter() + ")"));
            return false;
        } else if (chaptersDownloaded + chaptersToDownload > this.config.getTelegramMaxChapterPerDay() && !this.ignoreLimits && this.config.getTelegramMaxChapterPerDay() != -1) {
            this.bot.execute(new SendMessage(this.chatId, String.format("Above chapter quota! (%d chapters left today)", this.config.getTelegramMaxChapterPerDay() - chaptersDownloaded)));
            return false;
        } else {
            return true;
        }
    }

    public void downloadNovel() throws IllegalStateException, InterruptedException {
        this.progressMsgId = ((SendResponse)this.bot.execute(new SendMessage(this.chatId, "Downloading: " + this.novel.metadata.getTitle()))).message().messageId();
        this.novel.downloadChapters();
        this.novel.output();
        if (!this.novel.failedChapters.isEmpty()) {
            this.bot.execute(new SendMessage(this.chatId, "Retrying failed chapters..."));
            this.novel.retry();
            if (!this.novel.failedChapters.isEmpty()) {
                this.bot.execute(new SendMessage(this.chatId, "Remaining failed chapters: " + this.novel.failedChapters.size()));
            }
        }

        File epub = new File(this.novel.saveLocation + "/" + this.novel.filename);
        if (epub.exists()) {
            this.bot.execute(new SendDocument(this.chatId, epub));
            GrabberUtils.info("EPUB sent: " + this.novel.filename);
            this.chaptersDownloaded = this.novel.successfulChapters.size();
        } else {
            this.bot.execute(new SendMessage(this.chatId, "Sorry. Could not download the novel."));
            GrabberUtils.err("EPUB not downloaded: " + this.messageTxt);
        }

    }

    public void updateProgress(int currChapter, int lastChapter) {
        if ((currChapter + 1) % 10 == 0) {
            this.bot.execute(new EditMessageText(this.chatId, this.progressMsgId, String.format("Downloading: %s (%d/%d)", this.novel.metadata.getTitle(), currChapter + 1, lastChapter)));
        }

        if (currChapter + 1 == lastChapter) {
            this.bot.execute(new EditMessageText(this.chatId, this.progressMsgId, String.format("Sending: %s", this.novel.metadata.getTitle())));
        }

    }

    public void cancel() {
        this.novel.killTask = true;
    }

    public long getChatId() {
        return this.chatId;
    }

    public int getProgressMsgId() {
        return this.progressMsgId;
    }

    public int getChaptersDownloaded() {
        return this.chaptersDownloaded;
    }

    public Novel getNovel() {
        return this.novel;
    }
}
