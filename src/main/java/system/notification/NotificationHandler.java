package system.notification;

import dorkbox.notify.Notify;
import dorkbox.util.ImageUtil;
import grabber.GrabberUtils;
import grabber.Novel;
import system.Config;
import system.data.EmailConfig;
import system.data.library.LibraryNovel;
import system.init;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class NotificationHandler {
    private static EmailNotification mailer;
    private static EmailConfig emailConfig;

    public NotificationHandler() {
        emailConfig = EmailConfig.getInstance();
        mailer = new EmailNotification();
    }

    public void sendNotifications(LibraryNovel libraryNovel, Novel autoNovel) {
        if(libraryNovel.isSendEmailNotification()) sendMailNotification(autoNovel);
        if(init.gui != null) {
            if(libraryNovel.isSendDesktopNotification()) {
                sendDesktopNotification(libraryNovel, autoNovel);
            }
        }
    }

    public void sendEmailAttachment(Novel autoNovel) {
        if(!emailConfig.getHost().isEmpty()) {
            mailer.sendAttachment(autoNovel);
            System.out.println("[LIBRARY]Email with attachment send.");
        }
    }

    private void sendMailNotification(Novel autoNovel) {
        if(!emailConfig.getHost().isEmpty()) {
            mailer.sendNotification(autoNovel);
            System.out.println("[LIBRARY]Notification send.");
        }
    }

    private void sendDesktopNotification(LibraryNovel libNovel, Novel autoNovel) {
        try {
            URI uri = new URI(autoNovel.chapterList.get(libNovel.getLastChapter()).chapterURL);
            File imageFile = new File(Config.getInstance().home_path + "/" + Config.getInstance().home_folder + "/"
                    + libNovel.getTitle() + "/" + libNovel.getCover());
            InputStream resourceAsStream = new FileInputStream(imageFile);
            Image image = ImageUtil.getImageImmediate(ImageIO.read(resourceAsStream));
            image = image.getScaledInstance(100, 133, Image.SCALE_SMOOTH);

            Notify.create()
                    .title(libNovel.getTitle())
                    .text(autoNovel.chapterList.get(libNovel.getLastChapter()).name)
                    .darkStyle()
                    .image(image)
                    .hideAfter(5000)
                    .hideCloseButton()
                    .onAction(arg0 -> GrabberUtils.openWebpage(uri))
                    .show();

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
