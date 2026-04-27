package automation_exercise.tests.ui;

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
            dataProviderClass = TestData.class,
            groups = {"e2e", "regression"}
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
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4-5. Add products and go to Cart
        productsPage.get().addFirstProductToCart();
        productsPage.get().clickViewCart();

        // 6-8. Proceed to Checkout and trigger Register/Login
        assertThat(getPage()).hasURL("https://automationexercise.com/view_cart");
        cartPage.get().clickProceedToCheckout();
        cartPage.get().clickRegisterLoginModal();

        // 9-10. Signup Process (Simplified for brevity)
        signupPage.get().fillSignupDetailsWithRandomEmail(user);
        signupPage.get().fillAccountInformation(password, day, month, year);
        signupPage.get().fillAddressDetails(user, lastName, address, state, city, zipcode, mobile);
        signupPage.get().clickCreateAccount();
        assertThat(getPage().locator("h2:has-text('Account Created!')")).isVisible();
        getPage().click("a[data-qa='continue-button']");

        // 11. Verify Logged in as username
        assertThat(getPage().locator("text=Logged in as " + user)).isVisible();

        // 12-14. Return to Cart and Checkout
        homePage.get().clickCart();
        cartPage.get().clickProceedToCheckout();
        // Verify address logic here...

        // 15. Place Order with Comment
        checkoutPage.get().enterComment(comment);
        checkoutPage.get().clickPlaceOrder();

        // 16-18. Payment and Success
        checkoutPage.get().enterPaymentDetails(cardName, cardNumber, cvc, expMonth, expYear);
        checkoutPage.get().clickPayAndConfirm();
        checkoutPage.get().verifyOrderSuccess();

        // 19-20. Cleanup
        homePage.get().deleteAccount();
        assertThat(getPage().locator("h2:has-text('Account Deleted!')")).isVisible();
        getPage().click("a[data-qa='continue-button']");
    }
}
