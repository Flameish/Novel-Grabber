package grabber;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import system.Config;

import java.util.Collections;
import java.util.logging.Level;

/**
 * Selenium driver handler
 */
public class Driver {
    public static String[] browserList = {"Chrome", "Firefox", "Edge", "Opera", "IE", "Headless"};
    public WebDriver driver;
    public WebDriverWait wait;

    public Driver(String window) {
        driverSetup(window);
        wait = new WebDriverWait(driver, 30);
    }

    /**
     * Selenium driver creation for selected browser.
     */
    private void driverSetup(String window) {
        GrabberUtils.info(window, "Starting browser...");
        switch (Config.getInstance().getBrowser()) {
            case "Chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                driver = new ChromeDriver(chromeOptions);
                break;
            case "Firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170";
                firefoxOptions.addPreference("general.useragent.override", userAgent);
                firefoxOptions.addPreference("permissions.default.image", 2);
                driver = new FirefoxDriver();
                break;
            case "Opera":
                WebDriverManager.operadriver().setup();
                driver = new OperaDriver();
                break;
            case "Edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            case "IE":
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver();
                break;
            case "Headless":
                driver = new HtmlUnitDriver(BrowserVersion.BEST_SUPPORTED);
                java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
                break;
        }
    }

    public void close() {
        driver.quit();
    }
}
