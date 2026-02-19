package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC14_PlaceOrderRegisterWhileCheckout extends BaseTest {

    @Test(description = "Test Case 14: Place Order: Register while Checkout",priority = 14)
    public void placeOrderWithRegistration() {
        // 1-3. Launch and Verify Home Page
        homePage.navigate();
        assertThat(page).hasTitle("Automation Exercise");

        // 4-5. Add products and go to Cart
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        // 6-8. Proceed to Checkout and trigger Register/Login
        assertThat(page).hasURL("https://automationexercise.com/view_cart");
        cartPage.clickProceedToCheckout();
        cartPage.clickRegisterLoginModal();

        // 9-10. Signup Process (Simplified for brevity)
        // signupPage.fillSignupDetails("Automation Test", "aoto_test@test.com");
        String user = "Automation Test";
        signupPage.fillSignupDetailsWithRandomEmail(user);


        signupPage.fillAccountInformation("Password123", "1", "January", "1990");
        signupPage.fillAddressDetails(user, "Auto", "119 Trần duy hưng", "VN", "Hà Nội", "94043", "0123456789");
        signupPage.clickCreateAccount();
        assertThat(page.locator("h2:has-text('Account Created!')")).isVisible();
        page.click("a[data-qa='continue-button']");

        // 11. Verify Logged in as username
        assertThat(page.locator("text=Logged in as " + user)).isVisible();

        // 12-14. Return to Cart and Checkout
        homePage.clickCart();
        cartPage.clickProceedToCheckout();
        // Verify address logic here...

        // 15. Place Order with Comment
        checkoutPage.enterComment("Please deliver during business hours.");
        checkoutPage.clickPlaceOrder();

        // 16-18. Payment and Success
        checkoutPage.enterPaymentDetails(user, "411111111111", "123", "12", "2028");
        checkoutPage.clickPayAndConfirm();
        checkoutPage.verifyOrderSuccess();

        // 19-20. Cleanup
        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}