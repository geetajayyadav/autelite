package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.DriverManager;
import utils.ScreenshotUtil;

public class Hooks {

    @Before
    public void setUp() {
        DriverManager.initDriver();
        System.out.println("🚀 Browser launched");
    }

    @After
    public void tearDown(Scenario scenario) {

        try {
            // 📸 Screenshot on failure
            if (scenario.isFailed()) {
                ScreenshotUtil.captureStep(scenario, "Failure Screenshot");
            }

            // 🎥 LambdaTest Video
            try {
                RemoteWebDriver driver = (RemoteWebDriver) DriverManager.getDriver();

                String sessionId = driver.getSessionId().toString();
                String videoUrl = "https://automation.lambdatest.com/logs/?sessionID=" + sessionId;

                scenario.attach(videoUrl.getBytes(), "text/plain", "LambdaTest Video");

                System.out.println("🎥 Video: " + videoUrl);

            } catch (Exception e) {
                System.out.println("⚠ No video (local run)");
            }

        } finally {
            DriverManager.quitDriver();
            System.out.println("🧹 Browser closed");
        }
    }
}