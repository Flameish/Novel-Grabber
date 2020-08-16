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

/**
 * Selenium driver handler
 */
public class Driver {
    public WebDriver driver;
    public WebDriverWait wait;
    Novel novel;

    public Driver(Novel novel) {
        this.novel = novel;
        if(novel.browser == null) {
            init.gui.appendText(novel.window, "[HEADLESS]No browser selected. Set your browser in Settings -> General -> Browser");
            System.out.println("[HEADLESS]No browser selected. Set your browser in Settings -> General -> Browser");
            return;
        }
        driverSetup();
        wait = new WebDriverWait(driver, 30);
    }

    /**
     * Selenium driver creation for selected browser.
     */
    private void driverSetup() {
        if(init.gui != null && !novel.window.equals("checker")) {
            init.gui.appendText(novel.window, "[HEADLESS]Starting browser...");
        }
        switch (novel.browser) {
            case "Chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(new ChromeOptions().setHeadless(true));
                break;
            case "Firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(new FirefoxOptions().setHeadless(true));
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
