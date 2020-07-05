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

public class Driver {
    public WebDriver driver;
    public WebDriverWait wait;
    Novel novel;

    public Driver(Novel novel) {
        this.novel = novel;
        driverSetup();
        wait = new WebDriverWait(driver, 30);
    }

    private void driverSetup() {
        if(init.window != null) {
            init.window.appendText(novel.options.window, "[INFO]Starting headerless browser...");
        }
        switch (novel.options.browser) {
            case "Chrome":
                WebDriverManager.chromedriver().setup();
                if(novel.options.headlessGUI) {
                    driver = new ChromeDriver();
                } else {
                    driver = new ChromeDriver(new ChromeOptions().setHeadless(true));
                }
                break;
            case "Firefox":
                WebDriverManager.firefoxdriver().setup();
                if(novel.options.headlessGUI) {
                    driver = new FirefoxDriver();
                } else {
                    driver = new FirefoxDriver(new FirefoxOptions().setHeadless(true));
                }
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
