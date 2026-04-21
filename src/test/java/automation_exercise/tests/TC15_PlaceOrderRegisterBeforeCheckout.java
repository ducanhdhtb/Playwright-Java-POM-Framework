package automation_exercise.tests;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC15_PlaceOrderRegisterBeforeCheckout extends BaseTest {

    @Test(
            description = "Test Case 15: Place Order: Register before Checkout",
            priority = 15,
            dataProvider = "tc15DataProvider",
            dataProviderClass = TestData.class,
            groups = {"e2e", "regression"}
    )
    @Step("TC15: Register before checkout and place order")
    public void placeOrderRegisterBeforeCheckout(
            String username,
            String emailPrefix,
            String password, String day, String month, String year,
            String firstName, String lastName, String address, String state, String city, String zipcode, String mobile,
            String productIndex,
            String comment,
            String cardName, String cardNumber, String cvc, String expMonth, String expYear
    ) {
        // 1-3. Launch and Verify Home Page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click 'Signup / Login' button
        homePage.clickSignupLogin();

        // 5. Fill all details in Signup and create account
        String email = (emailPrefix == null ? "user_pro_" : emailPrefix) + System.currentTimeMillis() + "@test.com";

        signupLoginPage.fillSignupDetails(username, email);
        signupLoginPage.fillAccountInformation(password, day, month, year);
        signupLoginPage.fillAddressDetails(firstName, lastName, address, state, city, zipcode, mobile);
        signupLoginPage.clickCreateAccount();

        // 6. Verify 'ACCOUNT CREATED!' and click 'Continue' button
        assertThat(page.locator("h2:has-text('Account Created!')")).isVisible();
        page.click("a[data-qa='continue-button']");

        // 7. Verify 'Logged in as username' at top
        homePage.verifyLoggedInAs(username);

        // 8. Add products to cart
        // Giả sử ta thêm sản phẩm đầu tiên bằng logic hover đã xây dựng
        homePage.clickProducts();
        productsPage.addProductToCartByIndex(Integer.parseInt(productIndex));
        productsPage.clickContinueShopping();

        // 9-10. Click 'Cart' button and Verify cart page
        homePage.clickCart();
        assertThat(page).hasURL("https://automationexercise.com/view_cart");

        // 11. Click Proceed To Checkout
        cartPage.clickProceedToCheckout();

        // 12. Verify Address Details and Review Your Order
        // (Thực hiện verify nội dung địa chỉ nếu cần thiết)

        // 13. Enter description in comment and click 'Place Order'
        checkoutPage.enterComment(comment);
        checkoutPage.clickPlaceOrder();

        // 14-15. Enter payment details and Confirm
        checkoutPage.enterPaymentDetails(cardName, cardNumber, cvc, expMonth, expYear);
        checkoutPage.clickPayAndConfirm();

        // 16. Verify success message 'Your order has been placed successfully!'
        checkoutPage.verifyOrderSuccess();

        // 17-20. Delete Account and Verify
        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}
