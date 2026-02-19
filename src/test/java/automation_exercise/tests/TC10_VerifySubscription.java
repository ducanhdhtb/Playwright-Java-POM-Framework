package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC10_VerifySubscription extends BaseTest {

    @Test(priority = 10)
    public void verifySubscription() {
        // 1 & 2. Launch browser and Navigate to URL
        homePage.navigate();

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Scroll down to footer
        homePage.scrollToFooter();

        // 5. Verify text 'SUBSCRIPTION'
        homePage.verifySubscriptionTitleIsVisible();

        // 6. Enter email address and click arrow button
        homePage.subscribe("tester_pro@example.com");

        // 7. Verify success message is visible
        homePage.verifySuccessMessage();
    }
}