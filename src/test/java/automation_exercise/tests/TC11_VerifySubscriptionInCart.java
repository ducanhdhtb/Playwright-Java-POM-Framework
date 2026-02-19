package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC11_VerifySubscriptionInCart extends BaseTest {

    @Test(description = "Test Case 11: Verify Subscription in Cart page")
    public void verifySubscriptionInCart() {
        // 1 & 2. Launch browser and Navigate to URL
        homePage.navigate();

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click 'Cart' button
        // Note: Reusing the existing navigation logic in HomePage or direct click
        page.click("header .fa-shopping-cart");

        // 5. Scroll down to footer
        cartPage.scrollToFooter();

        // 6. Verify text 'SUBSCRIPTION'
        cartPage.verifySubscriptionTitleIsVisible();

        // 7. Enter email address and click arrow button
        cartPage.subscribe("cart_tester@example.com");

        // 8. Verify success message 'You have been successfully subscribed!' is visible
        cartPage.verifySuccessMessage();
    }
}