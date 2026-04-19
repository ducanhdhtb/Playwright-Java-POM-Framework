package automation_exercise;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import io.qameta.allure.Step;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class E2EPurchaseTest extends BaseTest {

    @Test(description = "Full E2E test: Search, Add to Cart, Register, Checkout, and Delete Account")
    @Step("E2E: search product, register, checkout, and delete account")
    public void testEndToEndProductOrderFlow() {
        // 1. Navigate to URL and go to the products page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();

        // 2. Search for a product, add it to the cart, and view the cart
        productsPage.searchProduct("Blue Top");
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        // 3. Proceed to checkout and click the 'Register / Login' button
        cartPage.proceedToCheckout();
        cartPage.clickRegisterLoginOnModal();

        // 4. Register a new user with random email
        String username = "E2E_User";
        signupLoginPage.fillSignupDetailsWithRandomEmail(username);
        signupLoginPage.fillAccountInformation("a_strong_password", "10", "July", "2000");
        signupLoginPage.fillAddressDetails(username, "Test", "123 Test Lane", "Texas", "Austin", "73301", "1234567890");
        signupLoginPage.clickCreateAccount();

        // 5. Verify account creation and continue
        assertThat(page.locator("h2:has-text('Account Created!')")).isVisible();
        page.click("a[data-qa='continue-button']");

        // 6. Verify that the user is logged in
        homePage.verifyLoggedInAs(username);

        // 7. Go back to the cart and proceed to checkout again
        homePage.clickCart();
        cartPage.proceedToCheckout();

        // 8. Verify address details are correct and place the order
        assertThat(page.locator("#address_delivery")).isVisible(); // Verify address section is visible
        checkoutPage.enterComment("This is an E2E Test Order");
        checkoutPage.clickPlaceOrder();

        // 9. Enter payment details and confirm the order
        checkoutPage.enterPaymentDetails(username, "4100000000000000", "123", "01", "2025");
        checkoutPage.clickPayAndConfirm();

        // 10. Verify the order success message
        checkoutPage.verifyOrderSuccess();

        // 11. Clean up by deleting the newly created account
        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}
