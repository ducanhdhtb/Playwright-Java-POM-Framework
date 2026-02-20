package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC17_RemoveProductsFromCart extends BaseTest {

    @Test(description = "Test Case 17: Remove Products From Cart",priority = 17)
    public void removeProductsFromCart() {
        // 1-3. Launch and Verify Home Page
        homePage.navigate();
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Add products to cart
        homePage.clickProducts();
        productsPage.addProductToCartByIndex(0);
        productsPage.clickContinueShopping();

        // 5-6. Click 'Cart' button and Verify cart page
        homePage.clickCart();
        assertThat(page).hasURL("https://automationexercise.com/view_cart");

        // 7. Click 'X' button corresponding to particular product
        cartPage.removeProductByIndex(0);

        // 8. Verify that product is removed from the cart
        cartPage.verifyProductIsRemoved();
    }
}