//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package bots.telegram;

import bots.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import grabber.GrabberUtils;
import grabber.sources.Source;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import system.Config;

public class Bot {
    private TelegramBot bot;
    private Config config = Config.getInstance();
    private LocalDate yesterday = LocalDate.now(ZoneId.systemDefault());
    private List<String> vipList = new ArrayList();
    private ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap();
    private static final String infoFile = "info.txt";
    private static final String vipFile = "vip.txt";
    private static final String telegramDir = "./telegram";
    private static final String cliText = "Input needs to start with '-link'. All parameter are case sensitive.\n\n[-link] | {novel_URL} | URL to the novel's table of contents page. Every other parameter is optional.\n[-wait] | {miliseconds} | Time between each chapter grab.\n[-chapters] | {all}, {5 27}, {12 last} | Specify which chapters to download.\n[-noDesc] | Don't create a description page.\n[-getImages] | Grab images from chapter body as well.\n[-displayTitle] | Write the chapter title at the top of each chapter text.\n[-invertOrder] | Invert the chapter order.\n\nExample:\n -link http://novelhost.com/novel/ -chapters 5 10 -getImages";

    public Bot() throws InterruptedException {
        String apiToken = this.config.getTelegramApiToken();
        if (!apiToken.isEmpty()) {
            GrabberUtils.info("[BOT]Connecting...");
            this.bot = new TelegramBot(this.config.getTelegramApiToken());
            this.readVipFile();
        } else {
            throw new InterruptedException("API Token empty.");
        }
    }

    public void start() {
        GrabberUtils.info("[BOT]Running.");
        this.bot.setUpdatesListener((updates) -> {
            Iterator var2 = updates.iterator();

            while(var2.hasNext()) {
                Update update = (Update)var2.next();
                if (this.isNewDay()) {
                    this.resetLimits();
                }

                CallbackQuery callbackQuery = update.callbackQuery();
                if (callbackQuery != null) {
                    this.processCallback(callbackQuery);
                } else if (update.message() != null) {
                    this.processMessage(update.message());
                }
            }

            return -1;
        });
    }

    private void processCallback(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.message().chat().id();
        int msgId = callbackQuery.message().messageId();
        User user = (User)this.users.get(callbackQuery.from().id());
        UUID uuid = UUID.fromString(callbackQuery.data());
        DownloadTask downloadTask = user.getDownloadTask(uuid);
        if (downloadTask != null) {
            String novelTitle = user.getDownloadTask(uuid).getNovel().metadata.getTitle();
            user.cancelDownloadTask(uuid);
            this.bot.execute((new EditMessageText(chatId, msgId, String.format("Stopping: %s...", novelTitle))).replyMarkup(new InlineKeyboardMarkup()));
        } else {
            this.bot.execute((new EditMessageText(chatId, msgId, "Could not find download to stop")).replyMarkup(new InlineKeyboardMarkup()));
        }

    }

    public void stop() {
        this.bot.removeGetUpdatesListener();
        GrabberUtils.info("[BOT]Stopped.");
    }

    private void processMessage(Message message) {
        String messageTxt = message.text();
        if (messageTxt != null) {
            int userId = message.from().id();
            long chatId = message.chat().id();
            this.users.putIfAbsent(userId, new User(this.vipList.contains(String.valueOf(userId))));
            User user = (User)this.users.get(userId);
            GrabberUtils.info(messageTxt);
            if (!messageTxt.startsWith("/info") && !messageTxt.startsWith("/start")) {
                if (messageTxt.startsWith("/sources")) {
                    this.bot.execute((new SendMessage(chatId, this.getSourcesString())).parseMode(ParseMode.Markdown).disableWebPagePreview(true));
                } else if (messageTxt.startsWith("/cli")) {
                    this.bot.execute((new SendMessage(chatId, "Input needs to start with '-link'. All parameter are case sensitive.\n\n[-link] | {novel_URL} | URL to the novel's table of contents page. Every other parameter is optional.\n[-wait] | {miliseconds} | Time between each chapter grab.\n[-chapters] | {all}, {5 27}, {12 last} | Specify which chapters to download.\n[-noDesc] | Don't create a description page.\n[-getImages] | Grab images from chapter body as well.\n[-displayTitle] | Write the chapter title at the top of each chapter text.\n[-invertOrder] | Invert the chapter order.\n\nExample:\n -link http://novelhost.com/novel/ -chapters 5 10 -getImages")).disableWebPagePreview(true));
                } else if (messageTxt.startsWith("/limits")) {
                    this.bot.execute((new SendMessage(chatId, user.getLimitString())).disableWebPagePreview(true));
                } else if (messageTxt.startsWith("/updateVips")) {
                    if (this.config.getTelegramAdminIds().contains(String.valueOf(userId))) {
                        this.readVipFile();
                        this.updateVips();
                        this.bot.execute(new SendMessage(chatId, String.format("Updated. (%d vips total)", this.vipList.size())));
                    }
                } else if (messageTxt.startsWith("/stop")) {
                    this.sendCancelList(chatId, user);
                } else {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        this.startDownload(messageTxt, chatId, user);
                    });
                }
            } else {
                this.bot.execute((new SendMessage(chatId, BotUtils.getStringFromFile("./telegram/info.txt"))).parseMode(ParseMode.Markdown).disableWebPagePreview(true));
            }

        }
    }

    private void startDownload(String messageTxt, long chatId, User user) {
        if (messageTxt.startsWith("/download")) {
            messageTxt = messageTxt.substring(messageTxt.indexOf(" ") + 1);
        }

        if (!messageTxt.startsWith("http") && !messageTxt.startsWith("-link")) {
            this.bot.execute(new SendMessage(chatId, "Please post a valid URL"));
        } else {
            int allowedDownloads = this.config.getTelegramDownloadLimit();
            if (user.getTasksAmount() >= allowedDownloads && !user.isVip()) {
                this.bot.execute(new SendMessage(chatId, String.format("Only %d download(s) at a time allowed", allowedDownloads)));
            } else {
                DownloadTask newTask = new DownloadTask(messageTxt, user.isVip(), chatId);

                try {
                    log(String.format("[%s] %s", chatId, messageTxt));
                    user.startDownloadTask(newTask);
                } catch (ClassNotFoundException | InterruptedException | IllegalStateException | IOException var8) {
                    this.bot.execute(new SendMessage(chatId, var8.getMessage()));
                }
            }
        }

    }

    private void sendCancelList(long chatId, User user) {
        Map<UUID, DownloadTask> userDownloadTasks = user.getDownloadTasks();
        if (userDownloadTasks.size() == 0) {
            this.bot.execute(new SendMessage(chatId, "No downloads running"));
        } else {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            userDownloadTasks.forEach((uuid, task) -> {
                if (task.getNovel().metadata != null) {
                    String novelTitle = task.getNovel().metadata.getTitle();
                    inlineKeyboardMarkup.addRow(new InlineKeyboardButton[]{(new InlineKeyboardButton(novelTitle)).callbackData(uuid.toString())});
                }

            });
            this.bot.execute((new SendMessage(chatId, "Select which download to cancel:")).replyMarkup(inlineKeyboardMarkup));
        }

    }

    private void resetLimits() {
        this.users.forEach((id, user) -> {
            user.reset();
        });
    }

    private void updateVips() {
        this.users.forEach((userId, user) -> {
            user.setVip(this.vipList.contains(String.valueOf(userId)));
        });
    }

    private boolean isNewDay() {
        return LocalDate.now(ZoneId.systemDefault()).isAfter(this.yesterday);
    }

    private String getSourcesString() {
        StringBuilder sources = new StringBuilder();
        Iterator var2 = GrabberUtils.getSources().iterator();

        while(var2.hasNext()) {
            Source source = (Source)var2.next();
            sources.append("[" + source.getName() + "](" + source.getUrl() + ")\n");
        }

        return sources.toString();
    }

    private void readVipFile() {
        try {
            Stream<String> lines = Files.lines(Paths.get("./telegram/vip.txt"));
            Throwable var2 = null;

            try {
                this.vipList = (List)lines.collect(Collectors.toList());
            } catch (Throwable var12) {
                var2 = var12;
                throw var12;
            } finally {
                if (lines != null) {
                    if (var2 != null) {
                        try {
                            lines.close();
                        } catch (Throwable var11) {
                            var2.addSuppressed(var11);
                        }
                    } else {
                        lines.close();
                    }
                }

            }
        } catch (IOException var14) {
            GrabberUtils.err("VIP file not found!");
        }

    }

    public TelegramBot getBot() {
        return this.bot;
    }

    public static void log(String msg) {
        String time = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        try {
            Files.createDirectories(Paths.get("./telegram"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("./telegram/log.txt", true));
            Throwable var3 = null;

            try {
                writer.write("[" + time + "] " + msg);
                writer.write("\n");
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (writer != null) {
                    if (var3 != null) {
                        try {
                            writer.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        writer.close();
                    }
                }

            }
        } catch (IOException var15) {
            GrabberUtils.err(var15.getMessage(), var15);
        }

    }
}
