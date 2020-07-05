package scripts;

import system.Accounts;
import grabber.Novel;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import system.init;

import java.io.IOException;
import java.util.Map;

public class Logins {
    public static void getLoginCookies(Novel novel) {
        System.out.println("[INFO]Login...");
        if(init.window != null) {
            init.window.appendText("auto","[INFO]Login...");
        }

        try {
            switch(novel.host.url) {
                case "https://booklat.com.ph/":
                    novel.cookies = booklat();
                    break;
                case "https://wuxiaworld.com/":
                    novel.cookies = wuxiaworld();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> booklat() throws IOException {
        Connection.Response res = Jsoup.connect("https://www.booklat.com.ph/Account/Login")
                .method(Connection.Method.GET)
                .timeout(30 * 1000)
                .execute();
        Document doc = res.parse();
        String token = doc.select("input[name=__RequestVerificationToken]").attr("value");
        res = Jsoup.connect("https://www.booklat.com.ph/Account/Login")
                .data("Email", Accounts.getUsername("Booklat"))
                .data("Password", Accounts.getPassword("Booklat"))
                .data("__RequestVerificationToken", token)
                .data("RememberMe", "false")
                .cookies(res.cookies())
                .method(Connection.Method.POST)
                .timeout(30 * 1000)
                .execute();
        return res.cookies();
    }

    private static Map<String, String> wuxiaworld() throws IOException {
        Connection.Response res = Jsoup.connect("https://www.wuxiaworld.com/account/login")
                .method(Connection.Method.GET)
                .timeout(30 * 1000)
                .execute();
        Document doc = res.parse();
        String token = doc.select("input[name=__RequestVerificationToken]").attr("value");
        res = Jsoup.connect("https://www.wuxiaworld.com/account/login")
                .data("Email", Accounts.getUsername("Wuxiaworld"))
                .data("Password", Accounts.getPassword("Wuxiaworld"))
                .data("__RequestVerificationToken", token)
                .data("RememberMe", "false")
                .cookies(res.cookies())
                .timeout(30 * 1000)
                .method(Connection.Method.POST)
                .execute();
        return res.cookies();
    }
}
