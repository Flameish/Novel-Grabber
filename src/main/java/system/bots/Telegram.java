package system.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import grabber.CLI;
import grabber.GrabberUtils;
import grabber.Novel;
import system.data.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private static final String supportedSourcesFile = "supported_Sources.txt";
    private static final String infoFile = "info.txt";
    private static final String telegramDir = "./telegram";
    private static final String cliText = "" +
            "Needs to start with \"-link\". All arguments are case sensitive." +
            "[-link] | {novel_URL} | URL to the novel's table of contents page. Every other parameter is optional.\n" +
            "[-wait] | {miliseconds} | Time between each chapter grab.\n" +
            "[-chapters] | {all}, {5 27}, {12 last} | Specify which chapters to download.\n" +
            "[-noDesc] | Don't create a description page.\n" +
            "[-removeStyle] | Remove all styling from chapter body.\n" +
            "[-getImages] | Grab images from chapter body as well.\n" +
            "[-displayTitle] | Write the chapter title at the top of each chapter text.\n" +
            "[-invertOrder] | Invert the chapter order.\n\n" +
            "Example: -link http://novelhost.com/anovel/ -chapters 5 10 -getImages";
    private ConcurrentHashMap currentlyDownloading = new ConcurrentHashMap<>();
    private ConcurrentHashMap downloadMsgIds = new ConcurrentHashMap<>();


    // Initialization with api token
    private Telegram() {
        GrabberUtils.info("Starting Telegram bot...");
        String token = Settings.getInstance().getTelegramApiToken();
        if(!token.isEmpty()) {
            novelly = new TelegramBot(token);
            GrabberUtils.info("Telegram bot started.");
        } else {
            GrabberUtils.err("Token empty");
        }
    }

    // Singleton
    public static Telegram getInstance() {
        if(telegramBot == null) {
            telegramBot = new Telegram();
        }
        return telegramBot;
    }

    public void run() {
        // Poll for new messages
        novelly.setUpdatesListener(updates -> {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            // Process each update in new thread
            for(Update update: updates) {
                executor.execute(() -> processMessage(update.message()));
            }
            executor.shutdown();
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processMessage(Message message) {
        long chatId = message.chat().id();
        String messageTxt = message.text();

        GrabberUtils.info(messageTxt);

        if(messageTxt.startsWith("/info") || messageTxt.startsWith("/start")) {
            novelly.execute(new SendMessage(chatId, getStringFromFile(infoFile)));
        }
        else if(messageTxt.startsWith("/sources")) {
            novelly.execute(new SendMessage(chatId, getStringFromFile(supportedSourcesFile))
                    .parseMode(ParseMode.Markdown)
                    .disableWebPagePreview(true));
        }
        else if(messageTxt.startsWith("/cli")) {
            novelly.execute(new SendMessage(chatId, cliText));
        }
        else if(messageTxt.startsWith("/stop")) {
            if(currentlyDownloading.containsKey(chatId)) {
                ((Novel) currentlyDownloading.get(chatId)).killTask = true;
                currentlyDownloading.remove(chatId);
            } else {
                novelly.execute(new SendMessage(chatId, "No current download."));
            }
        }
        else {
            if(messageTxt.startsWith("/download")) {
                messageTxt = messageTxt.substring(messageTxt.indexOf(" ")+1);
            }
            if(messageTxt.startsWith("http")) {
                if(!currentlyDownloading.containsKey(chatId)) {
                    log(messageTxt);
                    currentlyDownloading.put(chatId, "");
                    try {
                        downloadNovel(chatId, messageTxt);
                    } catch(Exception e) {
                        novelly.execute(new SendMessage(chatId, "Error: " + e.getMessage()));
                    }
                    currentlyDownloading.remove(chatId);
                } else {
                    novelly.execute(new SendMessage(chatId, "Only one download at a time allowed."));
                }
            } else if(messageTxt.startsWith("-link")) {
                if(!currentlyDownloading.containsKey(chatId)) {
                    log(messageTxt);
                    currentlyDownloading.put(chatId, "");
                    try {
                        downloadNovelCLI(chatId, messageTxt);
                    } catch(Exception e) {
                        novelly.execute(new SendMessage(chatId, "Error: " + e.getMessage()));
                    }
                    currentlyDownloading.remove(chatId);
                } else {
                    novelly.execute(new SendMessage(chatId, "Only one download at a time allowed."));
                }
            } else {
                novelly.execute(new SendMessage(chatId, "Please post a valid URL"));
            }
        }
    }

    private void downloadNovel(long chatId, String messageTxt) throws  Exception {
        Novel novel = Novel.builder()
                .novelLink(messageTxt)
                .window("auto")
                .saveLocation("./telegram/requests/"+ chatId)
                .getImages(true)
                .telegramChatId(chatId)
                .build();
        novel.check();

        if(novel.chapterList.isEmpty()) throw new Exception();
        currentlyDownloading.put(chatId, novel);
        // Send confirmation message and store message id
        novelly.execute(new SendMessage(chatId, "Downloading: "+novel.metadata.getTitle()));
        int messageId = novelly.execute(new SendMessage(chatId, "Progress: ")).message().messageId();
        downloadMsgIds.put(chatId, messageId);

        novel = Novel.modifier(novel)
                .firstChapter(1)
                .lastChapter(novel.chapterList.size())
                .build();
        novel.downloadChapters();

        if(!novel.killTask) {
            novel.output();
            File epub = new File(novel.saveLocation+"/"+novel.epubFilename);
            if(epub.exists()) {
                novelly.execute(new SendDocument(chatId, epub));
                GrabberUtils.info("EPUB sent: " + novel.epubFilename);
            } else {
                novelly.execute(new SendMessage(chatId, "Sorry. Could not download the novel."));
                GrabberUtils.err("EPUB not downloaded: " + messageTxt);
            }
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
                .saveLocation("./telegram/requests/"+ chatId)
                .build();

        novel.check();
        if(novel.chapterList.isEmpty()) throw new Exception();
        currentlyDownloading.put(chatId, novel);

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

        if(!novel.killTask) {
            novel.output();
            File epub = new File(novel.saveLocation+"/"+novel.epubFilename);
            if(epub.exists()) {
                novelly.execute(new SendDocument(chatId, epub));
                GrabberUtils.info("EPUB sent: " + novel.epubFilename);
            } else {
                novelly.execute(new SendMessage(chatId, "Sorry. Could not download the novel."));
                GrabberUtils.err("EPUB not downloaded: " + messageTxt);
            }
        }
    }

    public void updateProgress(long chatId, int currChapter, int lastChapter) {
        int messageId = (int) downloadMsgIds.get(chatId);
        novelly.execute(new EditMessageText(chatId, messageId, "Progress: "+(currChapter+1)+"/"+lastChapter));
    }

    public static void log(String msg) {
        String time = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        try {
            Files.createDirectories(Paths.get(telegramDir));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(telegramDir + "/log.txt", true))) {
                writer.write("[" + time + "] " + msg);
                writer.write("\n");
            }
        } catch (IOException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
    }

    private static String getStringFromFile(String fileName) {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(telegramDir+"/"+fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            GrabberUtils.err("File found: "+ telegramDir+"/"+fileName);
        }
        return resultStringBuilder.toString();
    }

    public void sendMsg(long chatId, String msg) {
        novelly.execute(new SendMessage(chatId, msg));
    }

}
