package automation_exercise.tests;

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
            dataProviderClass = TestData.class
    )
    @Step("TC16: Login before checkout and place order")
    public void placeOrderLoginBeforeCheckout(
            String user,
            String password,
            String productIndex,
            String comment,
            String cardName, String cardNumber, String cvc, String expMonth, String expYear
    ) {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(page).hasTitle("Automation Exercise");

        String loginEmail = createLoggedInUser(user, password);
        homePage.clickLogout();

        homePage.clickSignupLogin();
        signupLoginPage.fillLoginForm(loginEmail, password);
        signupLoginPage.clickLoginButton();

        homePage.verifyLoggedInAs(user);

        homePage.clickProducts();
        productsPage.addProductToCartByIndex(Integer.parseInt(productIndex));
        productsPage.clickContinueShopping();

        homePage.clickCart();
        assertThat(page).hasURL("https://automationexercise.com/view_cart");

        cartPage.clickProceedToCheckout();

        checkoutPage.enterComment(comment);
        checkoutPage.clickPlaceOrder();

        checkoutPage.enterPaymentDetails(cardName, cardNumber, cvc, expMonth, expYear);
        checkoutPage.clickPayAndConfirm();

        checkoutPage.verifyOrderSuccess();

        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}
