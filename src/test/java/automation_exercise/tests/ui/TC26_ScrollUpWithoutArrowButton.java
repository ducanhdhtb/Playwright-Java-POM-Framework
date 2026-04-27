package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC26: Verify Scroll Up without 'Arrow' button and Scroll Down functionality
 */
public class TC26_ScrollUpWithoutArrowButton extends BaseTest {

    @Test(
            description = "TC26: Verify Scroll Up without Arrow button after scrolling down",
            priority = 26,
            groups = {"regression"}
    )
    @Description("Scrolls to bottom, verifies SUBSCRIPTION visible, scrolls up manually, verifies hero text")
    @Step("TC26: Scroll down then scroll up manually (no arrow button)")
    public void testScrollUpWithoutArrowButton() {
        // 1-3. Navigate and verify home page
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Scroll down to bottom
        homePage.get().scrollToBottom();

        // 5. Verify SUBSCRIPTION is visible
        homePage.get().verifySubscriptionTitleIsVisible();

        // 6. Scroll up manually (no arrow button)
        homePage.get().scrollToTop();

        // 7. Verify page scrolled up — hero text visible
        getPage().waitForTimeout(1000);
        homePage.get().verifyHeroText("Full-Fledged practice website for Automation Engineers");
    }
}
