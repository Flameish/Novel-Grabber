package system.notification;

import grabber.Novel;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import system.data.EmailConfig;
import system.data.library.Library;
import system.data.library.LibraryNovel;

import javax.activation.FileDataSource;
import java.io.File;

public class EmailNotification {
    Mailer mailer;
    EmailConfig emailConfig;

    /**
     * Creates Mailer from email config.
     */
    public EmailNotification() {
        emailConfig = EmailConfig.getInstance();
        if(!emailConfig.getHost().isEmpty()) {
            TransportStrategy SSL = TransportStrategy.SMTP;
            switch(emailConfig.getSsl()) {
                case 0:
                    SSL = TransportStrategy.SMTP;
                    break;
                case 1:
                    SSL = TransportStrategy.SMTPS;
                    break;
                case 2:
                    SSL = TransportStrategy.SMTP_TLS;
                    break;
            }
            mailer = MailerBuilder
                    .withSMTPServer(emailConfig.getInstance().getHost(), emailConfig.getPort(), emailConfig.getUsername(), emailConfig.getPassword())
                    .withTransportStrategy(SSL)
                    .withSessionTimeout(100 * 1000)
                    .buildMailer();
        } else {
            System.out.println("[MAILER]Could not create mailer. Check email config.");
        }
    }

    /**
     * Sends Email with chapter names/links.
     * @param novel
     */
    public void sendNotification(Novel novel) {
        LibraryNovel libraryNovel = Library.getInstance().getNovel(novel.novelLink);
        StringBuilder links = new StringBuilder();

        for (int i = libraryNovel.getLastChapter(); i < libraryNovel.getNewestChapter(); i++) {
            links.append("<a href=\""+novel.chapterList.get(i).chapterURL+"\">"+novel.chapterList.get(i).name+"</a><br>");
        }
        Email email = EmailBuilder.startingBlank()
                .to(emailConfig.getInstance().getReceiverEmail())
                .from(emailConfig.getInstance().getReceiverEmail())
                .withSubject("[Novel-Grabber]"+novel.bookTitle + " - Update")
                .withHTMLText(links.toString())
                .buildEmail();

        mailer.sendMail(email);
    }

    /**
     * Sends an Email with chapter names/links and created EPUB of novel as attachment.
     * @param novel
     */
    public void sendAttachment(Novel novel) {
        StringBuilder links = new StringBuilder();
        for (int i = novel.firstChapter-1; i < novel.lastChapter; i++) {
            links.append("<a href=\""+novel.chapterList.get(i).chapterURL+"\">"+novel.chapterList.get(i).name+"</a><br>");
        }
        File epub = new File(novel.saveLocation+"/"+novel.epubFilename);
        Email email = EmailBuilder.startingBlank()
                .to(emailConfig.getInstance().getReceiverEmail())
                .from(emailConfig.getInstance().getReceiverEmail())
                .withSubject("[Novel-Grabber]"+novel.bookTitle+" - Update")
                .withHTMLText(links.toString())
                .withAttachment(epub.getName(), new FileDataSource(epub))
                .buildEmail();

        mailer.sendMail(email);
    }
}
