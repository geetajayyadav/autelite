package stepdefinitions;

import io.cucumber.java.en.*;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.DriverManager;
import utils.ScreenshotUtil;

public class LoginSteps {

    private Scenario scenario;

    @Before
    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("user opens the browser")
    public void user_opens_the_browser() {
        System.out.println("Browser initialized via Hooks");

        ScreenshotUtil.captureStep(scenario, "Open Browser");
    }

    @When("user navigates to example website")
    public void user_navigates_to_example_website() {

        DriverManager.getDriver().get("https://example.com");

        ScreenshotUtil.captureStep(scenario, "Navigate to Website");
    }

    @Then("page title should be correct")
    public void page_title_should_be_correct() {

        String actualTitle = DriverManager.getDriver().getTitle();
        String expectedTitle = "Example Domain";

        ScreenshotUtil.captureStep(scenario, "Verify Title");

        if (!actualTitle.equals(expectedTitle)) {
            throw new AssertionError(
                    "Expected: " + expectedTitle + " but got: " + actualTitle
            );
        }
    }
}