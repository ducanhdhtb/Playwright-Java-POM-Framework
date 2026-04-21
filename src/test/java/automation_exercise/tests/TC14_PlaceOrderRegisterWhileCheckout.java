package automation_exercise.tests;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC14_PlaceOrderRegisterWhileCheckout extends BaseTest {

    @Test(
            description = "Test Case 14: Place Order: Register while Checkout",
            priority = 14,
            dataProvider = "tc14DataProvider",
            dataProviderClass = TestData.class
    )
    @Step("TC14: Register while checkout and place order")
    public void placeOrderWithRegistration(
            String user,
            String password, String day, String month, String year,
            String lastName, String address, String state, String city, String zipcode, String mobile,
            String comment,
            String cardName, String cardNumber, String cvc, String expMonth, String expYear
    ) {
        // 1-3. Launch and Verify Home Page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(page).hasTitle("Automation Exercise");

        // 4-5. Add products and go to Cart
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        // 6-8. Proceed to Checkout and trigger Register/Login
        assertThat(page).hasURL("https://automationexercise.com/view_cart");
        cartPage.clickProceedToCheckout();
        cartPage.clickRegisterLoginModal();

        // 9-10. Signup Process (Simplified for brevity)
        signupPage.fillSignupDetailsWithRandomEmail(user);
        signupPage.fillAccountInformation(password, day, month, year);
        signupPage.fillAddressDetails(user, lastName, address, state, city, zipcode, mobile);
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
        checkoutPage.enterComment(comment);
        checkoutPage.clickPlaceOrder();

        // 16-18. Payment and Success
        checkoutPage.enterPaymentDetails(cardName, cardNumber, cvc, expMonth, expYear);
        checkoutPage.clickPayAndConfirm();
        checkoutPage.verifyOrderSuccess();

        // 19-20. Cleanup
        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}
