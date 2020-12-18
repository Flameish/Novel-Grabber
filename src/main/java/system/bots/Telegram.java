package system.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import grabber.CLI;
import grabber.Novel;
import system.data.Settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Telegram {
    private static Telegram telegramBot;
    public TelegramBot novelly;
    private ConcurrentHashMap certificationCosts = new ConcurrentHashMap<>();
    private ConcurrentHashMap downloadMsgIds = new ConcurrentHashMap<>();
    private Set currentlyDownloading = certificationCosts.newKeySet();
    private static final String sources = "" +
            "[Asian Hobbyist](https://www.asianhobbyist.com/)\n" +
            "[Booklat](https://booklat.com.ph)\n" +
            "[BoxNovel.com](https://boxnovel.com)\n" +
            "[BoxNovel.net](https://boxnovel.net)\n" +
            "[BoxNovel.org](https://boxnovel.org/)\n" +
            "[Chrysanthemum Garden](https://chrysanthemumgarden.com)\n" +
            "[Comrade Mao](https://comrademao.com)\n" +
            "[Creative Novels](https://creativenovels.com)\n" +
            "[Dreame](https://dreame.com)\n" +
            "[FanFiction](https://fanfiction.net)\n" +
            "[FanFiktion](https://fanfiktion.de)\n" +
            "[FicFun](https://ficfun.com)\n" +
            "[Foxaholic](https://foxaholic.com)\n" +
            "[Foxteller](https://foxteller.com)\n" +
            "[Honeyfeed](https://honeyfeed.fm)\n" +
            "[Inkitt](https://inkitt.com)\n" +
            "[ISO Translations](https://isotls.com)\n" +
            "[LiberSpark](https://liberspark.com)\n" +
            "[Light Novels Translations](https://lightnovelstranslations.com)\n" +
            "[LNMTL](https://lnmtl.com/)\n" +
            "[MoonQuill](https://moonquill.com)\n" +
            "[MTLNovel](https://mtlnovel.com)\n" +
            "[Novel Full](https://novelfull.com)\n" +
            "[Novelhall](https://novelhall.com)\n" +
            "[Novelsrock](https://novelsrock.com/)\n" +
            "[Novel Updates](https://novelupdates.com)\n" +
            "[Quotev](https://quotev.com)\n" +
            "[ReadLightNovel](https://www.readlightnovel.org/)\n" +
            "[ReadNovelFull.Com](https://readnovelfull.com/)\n" +
            "[Re:Library](https://re-library.com)\n" +
            "[Royal Road](https://royalroad.com)\n" +
            "[Scribble Hub](https://scribblehub.com)\n" +
            "[Tapas](https://tapas.io)\n" +
            "[TapRead](https://tapread.com)\n" +
            "[Veratales](https://veratales.com/)\n" +
            "[VipNovel](https://vipnovel.com/)\n" +
            "[Volare Novels](https://volarenovels.com)\n" +
            "[Wattpad](https://wattpad.com)\n" +
            "[Webnovel](https://webnovel.com)\n" +
            "[Wordrain](https://wordrain69.com)\n" +
            "[WuxiaWorld.co](https://wuxiaworld.co)\n" +
            "[Wuxiaworld.com](https://wuxiaworld.com)\n" +
            "[WuxiaWorld.online](https://wuxiaworld.online)\n" +
            "[WuxiaWorld.site](https://wuxiaworld.site)" +
            "";

    // Initialization with api token
    private Telegram() {
        System.out.print("Starting Telegram bot...");
        String token = Settings.getInstance().getTelegramApiToken();
        if(!token.isEmpty()) {
            novelly = new TelegramBot(token);
            System.out.println("done");
        } else {
            System.err.println("[ERROR] Token empty");
        }
    }

    // Singleton
    public static Telegram getInstance() {
        if(telegramBot == null) {
            telegramBot = new Telegram();
        }
        return telegramBot;
    }
    // Poll for new messages
    public void run() {
        novelly.setUpdatesListener(updates -> {
            ExecutorService executor = Executors.newFixedThreadPool(10);
           // Loop through each new message in new thread
            for(Update update: updates) {
                executor.execute(() -> {
                    long chatId = update.message().chat().id();
                    String messageTxt = update.message().text();
                    if(messageTxt.startsWith("/info") || messageTxt.startsWith("/start")) {
                        novelly.execute(new SendMessage(chatId, "" +
                                "To start downloading a novel just paste the it's link.\n\n" +
                                "You can list all supported websites with /sources\n\n" +
                                "If you want to only download specific chapters or you want more control over your EPUB, " +
                                "take a look at the possible download arguments with /cli"
                        ));
                    }
                    else if(messageTxt.startsWith("/sources")) {
                        novelly.execute(new SendMessage(chatId, sources).parseMode(ParseMode.Markdown).disableWebPagePreview(true));
                    }
                    else if(messageTxt.startsWith("/cli")) {
                        novelly.execute(new SendMessage(chatId,
                                "[-link] | {novel_URL} | URL to the novel's table of contents page.\n" +
                                "[-wait] | {miliseconds} | Time between each chapter grab.\n" +
                                "[-chapters] | {all}, {5 27}, {12 last} | Specify which chapters to download.\n" +
                                "[-noDesc] | Don't create a description page.\n" +
                                "[-removeStyle] | Remove all styling from chapter body.\n" +
                                "[-getImages] | Grab images from chapter body as well.\n" +
                                "[-displayTitle] | Write the chapter title at the top of each chapter text.\n" +
                                "[-invertOrder] | Invert the chapter order.\n\n" +
                                "Example: -link http://yournovelhost.com/anovel/ -chapters 5 10 -getImages"
                        ));
                    }
                    else if(messageTxt.startsWith("http")) {
                        if(!currentlyDownloading.contains(chatId)) {
                            writeLog(messageTxt);
                            currentlyDownloading.add(chatId);
                            try {
                                downloadNovel(chatId, messageTxt);
                            } catch(Exception e) {
                                novelly.execute(new SendMessage(chatId, "Sorry. Could not download the novel."));
                            }
                            currentlyDownloading.remove(chatId);
                        } else {
                            novelly.execute(new SendMessage(chatId, "Only one download at a time allowed."));
                        }
                    } else if(messageTxt.startsWith("-link")) {
                        if(!currentlyDownloading.contains(chatId)) {
                            writeLog(messageTxt);
                            currentlyDownloading.add(chatId);
                            try {
                                downloadNovelCLI(chatId, messageTxt);
                            } catch(Exception e) {
                                novelly.execute(new SendMessage(chatId, "Sorry. Could not download the novel."));
                            }
                            currentlyDownloading.remove(chatId);
                        } else {
                            novelly.execute(new SendMessage(chatId, "Only one download at a time allowed."));
                        }
                    } else {
                        novelly.execute(new SendMessage(chatId, "Please post a valid URL"));
                    }
                });
            }
            executor.shutdown();
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void downloadNovel(long chatId, String messageTxt) throws  Exception {
        Novel novel = Novel.builder()
                .novelLink(messageTxt)
                .window("auto")
                .saveLocation("./requests/"+ chatId)
                .getImages(true)
                .build();
        novel.check();

        if(novel.chapterList.isEmpty()) throw new Exception();

        // Send confirmation message and store message id
        novelly.execute(new SendMessage(chatId, "Downloading: "+novel.metadata.getTitle()));
        int messageId = novelly.execute(new SendMessage(chatId, "Progress: ")).message().messageId();
        downloadMsgIds.put(chatId, messageId);

        novel = Novel.modifier(novel)
                .firstChapter(1)
                .lastChapter(novel.chapterList.size())
                .telegramChatId(chatId)
                .build();
        novel.downloadChapters();
        novel.output();

        File epub = new File(novel.saveLocation+"/"+novel.epubFilename);
        if(epub.exists()) {
            novelly.execute(new SendDocument(chatId, epub));
        } else {
            novelly.execute(new SendMessage(chatId, "Sorry. Could not download the novel."));
        }
    }

    private void downloadNovelCLI(long chatId, String messageTxt) throws  Exception {
        String[] args = CLI.createArgsFromString(messageTxt);
        Map<String, List<String>> params = CLI.createParamsFromArgs(args);

        Novel novel = Novel.builder().fromCLI(params)
                .window("auto")
                .useHeadless(false)
                .useAccount(false)
                .telegramChatId(chatId)
                .saveLocation("./requests/"+ chatId)
                .build();

        novel.check();
        if(novel.chapterList.isEmpty()) throw new Exception();
        // Chapter range needs to be set after fetching the chapter list
        if(params.containsKey("chapters")) {
            if(params.get("chapters").get(0).equals("all")) {
                novel.firstChapter = 1;
                novel.lastChapter = novel.chapterList.size();
            } else {
                novel.firstChapter = Integer.parseInt(params.get("chapters").get(0));
                if(params.get("chapters").get(1).equals("last")) {
                    novel.lastChapter = novel.chapterList.size();
                } else {
                    novel.lastChapter = Integer.parseInt(params.get("chapters").get(1));
                }
            }
        } else {
            novel.firstChapter = 1;
            novel.lastChapter = novel.chapterList.size();
        }

        // Send confirmation message and store message id
        novelly.execute(new SendMessage(chatId, "Downloading: "+novel.metadata.getTitle()));
        int messageId = novelly.execute(new SendMessage(chatId, "Progress: ")).message().messageId();
        downloadMsgIds.put(chatId, messageId);
        novel.downloadChapters();
        novel.output();

        File epub = new File(novel.saveLocation+"/"+novel.epubFilename);
        if(epub.exists()) {
            novelly.execute(new SendDocument(chatId, epub));
        } else {
            novelly.execute(new SendMessage(chatId, "Sorry. Could not download the novel."));
        }
    }

    public void updateProgress(long chatId, int currChapter, int lastChapter) {
        int messageId = (int) downloadMsgIds.get(chatId);
        novelly.execute(new EditMessageText(chatId, messageId, "Progress: "+(currChapter+1)+"/"+lastChapter));
    }

    public static void writeLog(String info) {
        String filename = "./telegramBot.log";
        String time = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write("[" + time + "] " + info);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
