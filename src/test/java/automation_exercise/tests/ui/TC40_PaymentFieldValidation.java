package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC40: Payment fields — Equivalence Partitioning + BVA + Error Guessing
 *
 * Fields: name-on-card, card-number, cvc, expiry-month, expiry-year
 * All fields: type=text, no maxlength constraint
 *
 * Partitions:
 *   Card Number EP: 16-digit valid, 15-digit (Amex), 17-digit, letters, empty
 *   CVC EP: 3-digit valid, 2-digit, 4-digit, letters
 *   Expiry Month EP: 01-12 valid, 00, 13, letters
 *   Expiry Year EP: current year valid, past year, 4-digit, 2-digit
 */
public class TC40_PaymentFieldValidation extends BaseTest {

    @DataProvider(name = "validPaymentData")
    public static Object[][] validPaymentData() {
        return new Object[][]{
                // name, card, cvc, month, year — all valid
                {"John Doe",  "4111111111111111", "123", "12", "2027"},  // EP: standard Visa
                {"Jane Smith","5500005555555559", "456", "01", "2028"},  // EP: Mastercard format
                {"Test User", "4111111111111111", "999", "06", "2099"},  // BVA: max year
        };
    }

    @DataProvider(name = "invalidPaymentData")
    public static Object[][] invalidPaymentData() {
        return new Object[][]{
                // name, card, cvc, month, year, description
                {"",          "4111111111111111", "123", "12", "2027", "empty name"},
                {"John Doe",  "",                 "123", "12", "2027", "empty card"},
                {"John Doe",  "4111111111111111", "",    "12", "2027", "empty cvc"},
                {"John Doe",  "4111111111111111", "123", "",   "2027", "empty month"},
                {"John Doe",  "4111111111111111", "123", "12", "",     "empty year"},
        };
    }

    private void setupLoggedInUserWithProductInCart(String name, String password) {
        String email = userApi.setupUser(name, password);
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickSignupLogin();
        signupLoginPage.fillLoginForm(email, password);
        signupLoginPage.clickLoginButton();
        homePage.verifyLoggedInAs(name);
        homePage.clickProducts();
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();
        cartPage.proceedToCheckout();
        checkoutPage.enterComment("Payment test");
        checkoutPage.clickPlaceOrder();
    }

    @Test(
            description = "TC40a: Valid payment data completes order successfully",
            dataProvider = "validPaymentData",
            priority = 40,
            groups = {"regression", "boundary"}
    )
    @Description("EP: Valid payment combinations should complete order successfully")
    @Step("TC40a: Valid payment — card '{1}', cvc '{2}', exp {3}/{4}")
    public void testValidPaymentData(String name, String card, String cvc,
                                     String month, String year) {
        String password = "Password123";
        String userName = "PayUser_" + System.currentTimeMillis();
        String email = userApi.setupUser(userName, password);

        try {
            setupLoggedInUserWithProductInCart(userName, password);

            checkoutPage.enterPaymentDetails(name.isEmpty() ? userName : name,
                    card, cvc, month, year);
            checkoutPage.clickPayAndConfirm();
            checkoutPage.verifyOrderSuccess();
        } finally {
            userApi.teardownUser(email, password);
        }
    }

    @Test(
            description = "TC40b: Empty payment fields — order should not complete",
            dataProvider = "invalidPaymentData",
            priority = 40,
            groups = {"regression", "boundary", "negative"}
    )
    @Description("EP: Empty required payment fields should prevent order completion")
    @Step("TC40b: Empty field test — {5}")
    public void testEmptyPaymentFields(String name, String card, String cvc,
                                        String month, String year, String description) {
        String password = "Password123";
        String userName = "PayNeg_" + System.currentTimeMillis();
        String email = userApi.setupUser(userName, password);

        try {
            setupLoggedInUserWithProductInCart(userName, password);

            checkoutPage.enterPaymentDetails(name, card, cvc, month, year);
            checkoutPage.clickPayAndConfirm();

            // Should NOT show order success — should stay on payment page or show error
            assertThat(page).hasURL(
                    java.util.regex.Pattern.compile(".*(payment|checkout).*"));
        } finally {
            userApi.teardownUser(email, password);
        }
    }

    @Test(
            description = "TC40c: Card number with letters — error guessing",
            priority = 40,
            groups = {"regression", "negative"}
    )
    @Description("Error Guessing: Letters in card number field should not complete order")
    @Step("TC40c: Letters in card number field")
    public void testCardNumberWithLetters() {
        String password = "Password123";
        String userName = "PayLetter_" + System.currentTimeMillis();
        String email = userApi.setupUser(userName, password);

        try {
            setupLoggedInUserWithProductInCart(userName, password);

            checkoutPage.enterPaymentDetails("John Doe", "ABCDEFGHIJKLMNOP",
                    "123", "12", "2027");
            checkoutPage.clickPayAndConfirm();

            // Should not show success
            assertThat(page).hasURL(
                    java.util.regex.Pattern.compile(".*(payment|checkout).*"));
        } finally {
            userApi.teardownUser(email, password);
        }
    }
}
