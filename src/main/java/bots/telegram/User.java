//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package bots.telegram;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import system.Config;

public class User {
    private ConcurrentHashMap<UUID, DownloadTask> downloadTasks = new ConcurrentHashMap();
    private Config config = Config.getInstance();
    private int totalChaptersDownloadedTd = 0;
    private boolean isVip;

    public User(boolean isVip) {
        this.isVip = isVip;
    }

    public void startDownloadTask(DownloadTask downloadTask) throws IOException, ClassNotFoundException, InterruptedException, IllegalStateException {
        UUID uuid = UUID.randomUUID();

        try {
            this.downloadTasks.put(uuid, downloadTask);
            downloadTask.create();
            if (downloadTask.isInBudget(this.totalChaptersDownloadedTd)) {
                downloadTask.downloadNovel();
                this.totalChaptersDownloadedTd += downloadTask.getChaptersDownloaded();
                this.downloadTasks.remove(uuid);
            }

        } catch (Exception var4) {
            this.downloadTasks.remove(uuid);
            throw var4;
        }
    }

    public void cancelDownloadTask(UUID uuid) {
        this.getDownloadTask(uuid).cancel();
        this.downloadTasks.remove(uuid);
    }

    public Map<UUID, DownloadTask> getDownloadTasks() {
        return this.downloadTasks;
    }

    public DownloadTask getDownloadTask(UUID uuid) {
        return (DownloadTask)this.downloadTasks.get(uuid);
    }

    public void reset() {
        this.totalChaptersDownloadedTd = 0;
    }

    public String getLimitString() {
        String msgMaxChNovel = String.format("Max. chapters per novel: %s \n", this.config.getTelegramNovelMaxChapter() != -1 && !this.isVip ? String.valueOf(this.config.getTelegramNovelMaxChapter()) : "Unlimited");
        String msgChLeft = String.format("Chapters left today: %s \n", this.config.getTelegramMaxChapterPerDay() != -1 && !this.isVip ? String.valueOf(this.config.getTelegramMaxChapterPerDay() - this.totalChaptersDownloadedTd) : "Unlimited");
        String msgChSpeed = String.format("Wait time between chapters: %s \n", (this.isVip ? 0 : this.config.getTelegramWait()) + " milliseconds");
        String msgDwnLimit = String.format("Concurrent downloads: %s \n", this.isVip ? 999 : this.config.getTelegramDownloadLimit());
        String msgImagesAllowed = String.format("Images allowed: %s \n", !this.config.isTelegramImagesAllowed() && !this.isVip ? "No" : "Yes");
        return msgChLeft + msgMaxChNovel + msgChSpeed + msgDwnLimit + msgImagesAllowed;
    }

    public boolean isVip() {
        return this.isVip;
    }

    public void setVip(boolean vip) {
        this.isVip = vip;
    }

    public int getTasksAmount() {
        return this.downloadTasks.size();
    }
}
