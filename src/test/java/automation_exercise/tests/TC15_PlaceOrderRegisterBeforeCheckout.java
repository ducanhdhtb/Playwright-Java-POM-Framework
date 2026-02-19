package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC15_PlaceOrderRegisterBeforeCheckout extends BaseTest {

    @Test(description = "Test Case 15: Place Order: Register before Checkout",priority = 15)
    public void placeOrderRegisterBeforeCheckout() {
        // 1-3. Launch and Verify Home Page
        homePage.navigate();
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click 'Signup / Login' button
        homePage.clickSignupLogin();

        // 5. Fill all details in Signup and create account
        String username = "Herry";
        String email = "user_pro_" + System.currentTimeMillis() + "@test.com"; // Email ngẫu nhiên

        signupLoginPage.fillSignupDetails(username, email);
        signupLoginPage.fillAccountInformation("Password123", "15", "May", "1995");
        signupLoginPage.fillAddressDetails("Herry", "Mr", "1600 Amphitheatre", "California", "Mountain View", "94043", "0987654321");
        signupLoginPage.clickCreateAccount();

        // 6. Verify 'ACCOUNT CREATED!' and click 'Continue' button
        assertThat(page.locator("h2:has-text('Account Created!')")).isVisible();
        page.click("a[data-qa='continue-button']");

        // 7. Verify 'Logged in as username' at top
        homePage.verifyLoggedInAs(username);

        // 8. Add products to cart
        // Giả sử ta thêm sản phẩm đầu tiên bằng logic hover đã xây dựng
        homePage.clickProducts();
        productsPage.addProductToCartByIndex(0);
        productsPage.clickContinueShopping();

        // 9-10. Click 'Cart' button and Verify cart page
        homePage.clickCart();
        assertThat(page).hasURL("https://automationexercise.com/view_cart");

        // 11. Click Proceed To Checkout
        cartPage.clickProceedToCheckout();

        // 12. Verify Address Details and Review Your Order
        // (Thực hiện verify nội dung địa chỉ nếu cần thiết)

        // 13. Enter description in comment and click 'Place Order'
        checkoutPage.enterComment("Please deliver by evening.");
        checkoutPage.clickPlaceOrder();

        // 14-15. Enter payment details and Confirm
        checkoutPage.enterPaymentDetails("Herry", "411111111111", "123", "12", "2028");
        checkoutPage.clickPayAndConfirm();

        // 16. Verify success message 'Your order has been placed successfully!'
        checkoutPage.verifyOrderSuccess();

        // 17-20. Delete Account and Verify
        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}