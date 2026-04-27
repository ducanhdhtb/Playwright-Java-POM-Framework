package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC16_PlaceOrderLoginBeforeCheckout extends BaseTest {

    @Test(
            description = "Test Case 16: Place Order: Login before Checkout",
            priority = 16,
            dataProvider = "tc16DataProvider",
            dataProviderClass = TestData.class,
            groups = {"e2e", "regression"}
    )
    @Step("TC16: Login before checkout and place order")
    public void placeOrderLoginBeforeCheckout(
            String user,
            String password,
            String productIndex,
            String comment,
            String cardName, String cardNumber, String cvc, String expMonth, String expYear
    ) {
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(getPage()).hasTitle("Automation Exercise");

        String loginEmail = createLoggedInUser(user, password);
        homePage.get().clickLogout();

        homePage.get().clickSignupLogin();
        signupLoginPage.get().fillLoginForm(loginEmail, password);
        signupLoginPage.get().clickLoginButton();

        homePage.get().verifyLoggedInAs(user);

        homePage.get().clickProducts();
        productsPage.get().addProductToCartByIndex(Integer.parseInt(productIndex));
        productsPage.get().clickContinueShopping();

        homePage.get().clickCart();
        assertThat(getPage()).hasURL("https://automationexercise.com/view_cart");

        cartPage.get().clickProceedToCheckout();

        checkoutPage.get().enterComment(comment);
        checkoutPage.get().clickPlaceOrder();

        checkoutPage.get().enterPaymentDetails(cardName, cardNumber, cvc, expMonth, expYear);
        checkoutPage.get().clickPayAndConfirm();

        checkoutPage.get().verifyOrderSuccess();

        homePage.get().deleteAccount();
        assertThat(getPage().locator("h2:has-text('Account Deleted!')")).isVisible();
        getPage().click("a[data-qa='continue-button']");
    }
}
