package utils;

import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class ScreenshotUtil {

    public static void captureStep(Scenario scenario, String stepName) {
        try {
            byte[] screenshot = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);

            scenario.attach(screenshot, "image/png", stepName);

            System.out.println("📸 Screenshot: " + stepName);

        } catch (Exception e) {
            System.out.println("⚠ Screenshot failed: " + stepName);
        }
    }
}