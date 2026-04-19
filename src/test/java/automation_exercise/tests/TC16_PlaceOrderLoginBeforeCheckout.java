package automation_exercise.tests;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC16_PlaceOrderLoginBeforeCheckout extends BaseTest {

    @Test(description = "Test Case 16: Place Order: Login before Checkout", priority = 16)
    @Step("TC16: Login before checkout and place order")
    public void placeOrderLoginBeforeCheckout() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(page).hasTitle("Automation Exercise");

        String loginEmail = createLoggedInUser("\u004e\u0067\u0075\u0079\u1ec5\u006e \u0110\u1ee9c \u0041\u006e\u0068", "ducanh123");
        homePage.clickLogout();

        homePage.clickSignupLogin();
        signupLoginPage.fillLoginForm(loginEmail, "ducanh123");
        signupLoginPage.clickLoginButton();

        homePage.verifyLoggedInAs("\u004e\u0067\u0075\u0079\u1ec5\u006e \u0110\u1ee9c \u0041\u006e\u0068");

        homePage.clickProducts();
        productsPage.addProductToCartByIndex(0);
        productsPage.clickContinueShopping();

        homePage.clickCart();
        assertThat(page).hasURL("https://automationexercise.com/view_cart");

        cartPage.clickProceedToCheckout();

        checkoutPage.enterComment("Returning customer order.");
        checkoutPage.clickPlaceOrder();

        checkoutPage.enterPaymentDetails("Automation Tester", "411111111111", "123", "05", "2029");
        checkoutPage.clickPayAndConfirm();

        checkoutPage.verifyOrderSuccess();

        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}
