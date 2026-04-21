package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC25: Verify Scroll Up using 'Arrow' button and Scroll Down functionality
 */
public class TC25_ScrollUpWithArrowButton extends BaseTest {

    @Test(
            description = "TC25: Verify Scroll Up using Arrow button after scrolling down",
            priority = 25,
            groups = {"regression"}
    )
    @Description("Scrolls to bottom, verifies SUBSCRIPTION visible, clicks arrow button, verifies hero text")
    @Step("TC25: Scroll down then scroll up via arrow button")
    public void testScrollUpWithArrowButton() {
        // 1-3. Navigate and verify home page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Scroll down to bottom
        homePage.scrollToBottom();

        // 5. Verify SUBSCRIPTION is visible
        homePage.verifySubscriptionTitleIsVisible();

        // 6. Click scroll-up arrow button
        // Wait for the button to become visible after scrolling
        page.locator("#scrollUp").waitFor(
                new com.microsoft.playwright.Locator.WaitForOptions()
                        .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                        .setTimeout(10_000)
        );
        homePage.clickScrollUpArrow();

        // 7. Verify page scrolled up — hero text visible
        page.waitForTimeout(1000); // allow scroll animation
        homePage.verifyHeroText("Full-Fledged practice website for Automation Engineers");
    }
}
