package grabber;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import system.init;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Selenium driver handler
 */
public class Driver {
    public WebDriver driver;
    public WebDriverWait wait;

    public Driver(String window, String browser) {
        driverSetup(window, browser);
        wait = new WebDriverWait(driver, 30);
    }

    /**
     * Selenium driver creation for selected browser.
     */
    private void driverSetup(String window, String browser) {
        if(init.gui != null && !window.equals("checker")) {
            init.gui.appendText(window, "[HEADLESS]Starting browser...");
        }
        switch (browser) {
            case "Chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("profile.managed_default_content_settings.images", 2);
                prefs.put("useAutomationExtension", false);
                chromeOptions.setExperimentalOption("prefs", prefs);
                chromeOptions.addArguments("--disable-gpu",
                        "--blink-settings=imagesEnabled=false",
                        "--disable-blink-features=AutomationControlled",
                        "--incognito");
                driver = new ChromeDriver(chromeOptions);
                break;
            case "Firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addPreference("permissions.default.image", 2);
                driver = new FirefoxDriver(firefoxOptions);
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
        }
    }

    public void close() {
        driver.quit();
    }
}
