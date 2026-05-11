package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.URL;
import java.util.HashMap;

public class DriverManager {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void initDriver() {

        // ===== GET FROM ENV (JENKINS) =====
        String username = System.getenv("LT_USERNAME");
        String accessKey = System.getenv("LT_ACCESS_KEY");

        // ===== FALLBACK TO CONFIG FILE =====
        if (username == null || accessKey == null) {
            username = ConfigReader.get("lt.username");
            accessKey = ConfigReader.get("lt.accessKey");
            System.out.println("Using config.properties credentials");
        } else {
            System.out.println("Using Jenkins credentials");
        }

        try {
            DesiredCapabilities caps = new DesiredCapabilities();

            caps.setCapability("browserName", "Chrome");
            caps.setCapability("browserVersion", "latest");

            HashMap<String, Object> ltOptions = new HashMap<>();
            ltOptions.put("user", username);
            ltOptions.put("accessKey", accessKey);
            ltOptions.put("platformName", "Windows 11");
            ltOptions.put("project", "Cucumber Demo");
            ltOptions.put("build", "Jenkins Build");
            ltOptions.put("name", "Sample Test");
            ltOptions.put("video", true);

            caps.setCapability("LT:Options", ltOptions);

            WebDriver remoteDriver = new RemoteWebDriver(
                    new URL("https://hub.lambdatest.com/wd/hub"),
                    caps
            );

            driver.set(remoteDriver);

            System.out.println("LambdaTest session started");

        } catch (Exception e) {

            System.out.println("LambdaTest failed → switching to local Chrome");

            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");

            driver.set(new ChromeDriver());
        }
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}