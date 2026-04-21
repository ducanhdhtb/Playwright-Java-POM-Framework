package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC23 (site): Verify address details in checkout page match registration data
 */
public class TC29_VerifyAddressInCheckout extends BaseTest {

    private static final String FIRST_NAME  = "AddressTest";
    private static final String LAST_NAME   = "User";
    private static final String COMPANY     = "TestCorp";
    private static final String ADDRESS     = "123 Verify Street";
    private static final String COUNTRY     = "United States";
    private static final String STATE       = "Texas";
    private static final String CITY        = "Austin";
    private static final String ZIPCODE     = "73301";
    private static final String MOBILE      = "1234567890";
    private static final String PASSWORD    = "Password123";

    @Test(
            description = "TC29: Verify delivery and billing address in checkout matches registration",
            priority = 29,
            groups = {"regression", "e2e"}
    )
    @Description("Registers user, adds product, proceeds to checkout, verifies address matches registration data")
    @Step("TC29: Verify address details in checkout page")
    public void testVerifyAddressInCheckout() {
        // 1-3. Navigate and verify home page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click Signup / Login
        homePage.clickSignupLogin();

        // 5. Fill signup and create account
        String email = "addr_" + System.currentTimeMillis() + "@test.com";
        signupLoginPage.fillSignupForm(FIRST_NAME, email);
        signupLoginPage.clickSignupButton();
        accountPage.fillAccountDetails(PASSWORD, "10", "July", "1990");
        accountPage.fillAddressDetails(
                FIRST_NAME, LAST_NAME, COMPANY, ADDRESS,
                COUNTRY, STATE, CITY, ZIPCODE, MOBILE
        );
        accountPage.clickCreateAccount();

        // 6. Verify account created and continue
        assertThat(page.getByText("Account Created!")).isVisible();
        page.click("a[data-qa='continue-button']");

        // 7. Verify logged in
        homePage.verifyLoggedInAs(FIRST_NAME);

        // 8. Add product to cart
        homePage.clickProducts();
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        // 9. Verify cart page
        assertThat(page).hasURL("https://automationexercise.com/view_cart");

        // 10. Proceed to checkout
        cartPage.proceedToCheckout();

        // 11. Verify delivery address contains registration data
        checkoutPage.verifyDeliveryAddress(FIRST_NAME);
        checkoutPage.verifyDeliveryAddress(ADDRESS);

        // 12. Verify billing address contains registration data
        checkoutPage.verifyBillingAddress(FIRST_NAME);
        checkoutPage.verifyBillingAddress(ADDRESS);

        // 13. Cleanup
        homePage.deleteAccount();
        assertThat(page.getByText("Account Deleted!")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}
