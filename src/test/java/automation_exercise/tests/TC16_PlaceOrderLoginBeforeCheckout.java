package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC16_PlaceOrderLoginBeforeCheckout extends BaseTest {

    @Test(description = "Test Case 16: Place Order: Login before Checkout",priority = 16)
    public void placeOrderLoginBeforeCheckout() {
        // 1-3. Launch and Verify Home Page
        homePage.navigate();
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click 'Signup / Login' button
        homePage.clickSignupLogin();

        // 5. Fill email, password and click 'Login' button
        // Sử dụng các hàm cũ đã có trong SignupLoginPage của bạn
        signupLoginPage.fillLoginForm("ducanhdhtb@gmail.com", "ducanh123");
        signupLoginPage.clickLoginButton();

        // 6. Verify 'Logged in as username' at top
        homePage.verifyLoggedInAs("Nguyễn Đức Anh");

        // 7. Add products to cart
        homePage.clickProducts();
        productsPage.addProductToCartByIndex(0);
        productsPage.clickContinueShopping();

        // 8-9. Click 'Cart' button and Verify cart page
        homePage.clickCart();
        assertThat(page).hasURL("https://automationexercise.com/view_cart");

        // 10. Click Proceed To Checkout
        cartPage.clickProceedToCheckout();

        // 11. Verify Address Details and Review Your Order
        // Playwright tự động đợi các element này hiển thị

        // 12. Enter description in comment and click 'Place Order'
        checkoutPage.enterComment("Returning customer order.");
        checkoutPage.clickPlaceOrder();

        // 13-14. Enter payment details and Confirm
        checkoutPage.enterPaymentDetails("Automation Tester", "411111111111", "123", "05", "2029");
        checkoutPage.clickPayAndConfirm();

        // 15. Verify success message
        checkoutPage.verifyOrderSuccess();

        // 16-17. Delete Account and Verify
        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}