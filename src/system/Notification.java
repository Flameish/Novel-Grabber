package system;

import grabber.Novel;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import system.persistent.EmailConfig;
import system.persistent.Library;

import javax.activation.FileDataSource;
import java.io.File;

public class Notification {
    Mailer mailer;

    public Notification() {
        if(!EmailConfig.getHost().isEmpty()) {
            TransportStrategy SSL = TransportStrategy.SMTP;
            switch(EmailConfig.getSSL()) {
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
                    .withSMTPServer(EmailConfig.getHost(), EmailConfig.getPort(), EmailConfig.getUsername(), EmailConfig.getPassword())
                    .withTransportStrategy(SSL)
                    .withSessionTimeout(100 * 1000)
                    .buildMailer();
        } else {
            System.out.println("[ERROR]No email config found.");
        }
    }

    public void sendNotification(Novel novel) {
        StringBuilder links = new StringBuilder();
        for (int i = Library.getLastChapter(novel.novelLink); i < Library.getNewestChapter(novel.novelLink); i++) {
            links.append("<a href=\""+novel.chapters.get(i).chapterURL+"\">"+novel.chapters.get(i).name+"</a><br>");
        }
        Email email = EmailBuilder.startingBlank()
                .to(EmailConfig.getReceiverEmail())
                .from(EmailConfig.getReceiverEmail())
                .withSubject(novel.metadata.bookTitle + " - Update")
                .withHTMLText(links.toString())
                .buildEmail();

        mailer.sendMail(email);
    }

    public void sendAttachment(Novel novel) {
        StringBuilder links = new StringBuilder();
        for (int i = novel.options.firstChapter-1; i < novel.options.lastChapter; i++) {
            links.append("<a href=\""+novel.chapters.get(i).chapterURL+"\">"+novel.chapters.get(i).name+"</a><br>");
        }
        File epub = new File(novel.options.saveLocation+"/"+novel.epubFilename);
        System.out.println(epub);
        Email email = EmailBuilder.startingBlank()
                .to(EmailConfig.getReceiverEmail())
                .from(EmailConfig.getReceiverEmail())
                .withSubject(novel.metadata.bookTitle+" - Update")
                .withHTMLText(links.toString())
                .withAttachment(epub.getName(), new FileDataSource(epub))
                .buildEmail();

        mailer.sendMail(email);
    }
}
