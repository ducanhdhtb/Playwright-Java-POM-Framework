package automation_exercise;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import io.qameta.allure.Step;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class E2EPurchaseTest extends BaseTest {

    @Test(
            description = "Full E2E test: Search, Add to Cart, Register, Checkout, and Delete Account",
            dataProvider = "e2ePurchaseDataProvider",
            dataProviderClass = TestData.class
    )
    @Step("E2E: search product, register, checkout, and delete account")
    public void testEndToEndProductOrderFlow(
            String searchKey,
            String username,
            String password, String day, String month, String year,
            String lastName, String address, String state, String city, String zipcode, String mobile,
            String comment,
            String cardName, String cardNumber, String cvc, String expMonth, String expYear
    ) {
        // 1. Navigate to URL and go to the products page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();

        // 2. Search for a product, add it to the cart, and view the cart
        productsPage.searchProduct(searchKey);
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        // 3. Proceed to checkout and click the 'Register / Login' button
        cartPage.proceedToCheckout();
        cartPage.clickRegisterLoginOnModal();

        // 4. Register a new user with random email
        signupLoginPage.fillSignupDetailsWithRandomEmail(username);
        signupLoginPage.fillAccountInformation(password, day, month, year);
        signupLoginPage.fillAddressDetails(username, lastName, address, state, city, zipcode, mobile);
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
        checkoutPage.enterComment(comment);
        checkoutPage.clickPlaceOrder();

        // 9. Enter payment details and confirm the order
        checkoutPage.enterPaymentDetails(cardName, cardNumber, cvc, expMonth, expYear);
        checkoutPage.clickPayAndConfirm();

        // 10. Verify the order success message
        checkoutPage.verifyOrderSuccess();

        // 11. Clean up by deleting the newly created account
        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}
