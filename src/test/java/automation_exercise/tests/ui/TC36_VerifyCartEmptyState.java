package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC36: Verify cart empty state message and navigation
 *
 * Discovered via MCP exploration: empty cart shows
 * "Cart is empty! Click here to buy products." with a link to /products.
 * This state was never explicitly tested.
 */
public class TC36_VerifyCartEmptyState extends BaseTest {

    @Test(
            description = "TC36a: Empty cart shows correct message with link to products",
            priority = 36,
            groups = {"regression"}
    )
    @Description("Navigates to cart when empty, verifies empty state message and products link")
    @Step("TC36a: Verify empty cart message")
    public void testEmptyCartShowsMessage() {
        // Navigate directly to cart (should be empty for new session)
        homePage.navigate("https://automationexercise.com/view_cart");

        // Verify empty cart message is visible
        assertThat(page.locator("#empty_cart")).isVisible();
        assertThat(page.locator("#empty_cart")).containsText("Cart is empty!");

        // Verify link to products page exists
        assertThat(page.locator("#empty_cart a[href='/products']")).isVisible();
    }

    @Test(
            description = "TC36b: Click 'here' link in empty cart navigates to products page",
            priority = 36,
            groups = {"regression"}
    )
    @Description("Clicks the 'here' link in empty cart message and verifies navigation to products")
    @Step("TC36b: Empty cart link navigates to products")
    public void testEmptyCartLinkNavigatesToProducts() {
        // Navigate to empty cart
        homePage.navigate("https://automationexercise.com/view_cart");

        // Verify empty state
        assertThat(page.locator("#empty_cart")).isVisible();

        // Click the 'here' link
        page.locator("#empty_cart a[href='/products']").click();

        // Verify navigated to products page
        assertThat(page).hasURL("https://automationexercise.com/products");
        assertThat(page.getByRole(
                com.microsoft.playwright.options.AriaRole.HEADING,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("All Products").setExact(true)
        )).isVisible();
    }

    @Test(
            description = "TC36c: Cart becomes empty after removing all products",
            priority = 36,
            groups = {"regression", "cart"}
    )
    @Description("Adds a product, removes it, verifies cart shows empty state")
    @Step("TC36c: Cart empty state after removing all products")
    public void testCartEmptyAfterRemovingAllProducts() {
        // Add one product
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        // Verify product is in cart
        cartPage.verifyCartCount(1);

        // Remove the product
        cartPage.removeProductByIndex(0);

        // Verify empty cart state
        assertThat(page.locator("#empty_cart")).isVisible();
        assertThat(page.locator("#empty_cart")).containsText("Cart is empty!");
    }
}
