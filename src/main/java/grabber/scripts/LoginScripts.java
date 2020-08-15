package grabber.scripts;

import grabber.Novel;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import system.data.accounts.Account;
import system.init;
import system.data.accounts.Accounts;

import java.io.IOException;
import java.util.Map;

/**
 *  Custom scripts to fetch login cookies from host sites.
 */
public class LoginScripts {
    public static void getLoginCookies(Novel novel) {
        System.out.println("[INFO]Login...");
        if(init.gui != null && !novel.window.equals("checker")) {
            init.gui.appendText("auto","[INFO]Login...");
        }
        try {
            switch(novel.url) {
                case "https://booklat.com.ph/":
                    novel.cookies = booklat();
                    break;
                case "https://wattpad.com/":
                    novel.cookies = wattpad();
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
        Account account = Accounts.getInstance().getAccount("Booklat");
        Connection.Response res = Jsoup.connect("https://www.booklat.com.ph/Account/Login")
                .method(Connection.Method.GET)
                .timeout(30 * 1000)
                .execute();
        String token = res.parse().select("input[name=__RequestVerificationToken]").attr("value");
        res = Jsoup.connect("https://www.booklat.com.ph/Account/Login")
                .data("Email", account.getUsername())
                .data("Password", account.getPassword())
                .data("__RequestVerificationToken", token)
                .data("RememberMe", "false")
                .cookies(res.cookies())
                .method(Connection.Method.POST)
                .timeout(30 * 1000)
                .execute();
        return res.cookies();
    }

    private static Map<String, String> wattpad() throws IOException {
        Account account = Accounts.getInstance().getAccount("WattPad");
        Connection.Response res = Jsoup.connect("https://www.wattpad.com/")
                .method(Connection.Method.GET)
                .timeout(30 * 1000)
                .execute();
        res = Jsoup.connect("https://www.wattpad.com/login")
                .data("username", account.getUsername())
                .data("password", account.getPassword())
                .cookies(res.cookies())
                .method(Connection.Method.POST)
                .timeout(30 * 1000)
                .execute();
        return res.cookies();
    }

    private static Map<String, String> wuxiaworld() throws IOException {
        Account account = Accounts.getInstance().getAccount("Wuxiaworld");
        Connection.Response res = Jsoup.connect("https://www.wuxiaworld.com/account/login")
                .method(Connection.Method.GET)
                .timeout(30 * 1000)
                .execute();
        String token = res.parse().select("input[name=__RequestVerificationToken]").attr("value");
        res = Jsoup.connect("https://www.wuxiaworld.com/account/login")
                .data("Email", account.getUsername())
                .data("Password", account.getPassword())
                .data("__RequestVerificationToken", token)
                .data("RememberMe", "false")
                .cookies(res.cookies())
                .timeout(30 * 1000)
                .method(Connection.Method.POST)
                .execute();
        return res.cookies();
    }
}
