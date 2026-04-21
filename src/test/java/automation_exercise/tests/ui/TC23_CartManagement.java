package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC23: Cart management scenarios.
 *
 * Covers:
 *   a) Add multiple products to cart and verify count
 *   b) Remove a product from cart
 *   c) Cart persists after login (API-backed user setup)
 */
public class TC23_CartManagement extends BaseTest {

    @Test(
            description = "TC23a: Add two products to cart and verify count",
            groups = {"regression", "cart"}
    )
    @Description("Adds two different products to cart and verifies cart shows 2 items")
    @Step("TC23a: Add two products and verify cart count")
    public void testAddMultipleProductsToCart() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();

        // Add first product
        productsPage.addProductToCartByIndex(0);
        productsPage.clickContinueShopping();

        // Add second product
        productsPage.addProductToCartByIndex(1);
        productsPage.clickViewCart();

        // Verify 2 items in cart
        cartPage.verifyCartCount(2);
    }

    @Test(
            description = "TC23b: Remove product from cart leaves cart empty",
            groups = {"regression", "cart"}
    )
    @Description("Adds one product, removes it, and verifies the cart is empty")
    @Step("TC23b: Remove product from cart")
    public void testRemoveProductFromCart() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();

        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        // Verify 1 item before removal
        cartPage.verifyCartCount(1);

        // Remove the product
        cartPage.removeProductByIndex(0);

        // Verify cart is now empty
        cartPage.verifyProductIsRemoved();
    }

    @Test(
            description = "TC23c: Cart persists after login for API-created user",
            groups = {"regression", "cart", "api-ui"}
    )
    @Description("Adds product as guest, logs in with API-created user, verifies cart is accessible")
    @Step("TC23c: Cart accessible after login")
    public void testCartAccessibleAfterLogin() {
        String name = "CartUser";
        String password = "Password123";

        // Setup user via API
        String email = userApi.setupUser(name, password);

        try {
            // 1. Navigate and add product as guest
            homePage.navigate(ConfigReader.getProperty("baseUrl"));
            homePage.clickProducts();
            productsPage.addFirstProductToCart();
            productsPage.clickViewCart();

            // 2. Proceed to checkout — triggers login modal
            cartPage.proceedToCheckout();
            cartPage.clickRegisterLoginOnModal();

            // 3. Login with API-created user
            signupLoginPage.fillLoginForm(email, password);
            signupLoginPage.clickLoginButton();

            // 4. Navigate back to cart
            homePage.clickCart();

            // 5. Verify cart page loads correctly
            assertThat(page).hasURL(
                    java.util.regex.Pattern.compile(".*view_cart.*"));
        } finally {
            userApi.teardownUser(email, password);
        }
    }
}
