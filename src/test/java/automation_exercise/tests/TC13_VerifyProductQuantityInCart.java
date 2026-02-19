package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC13_VerifyProductQuantityInCart extends BaseTest {

    @Test(description = "Test Case 13: Verify Product quantity in Cart",priority = 13)
    public void verifyProductQuantity() {
        // 1-3. Launch and Verify Home Page
        homePage.navigate();
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click 'View Product' for the first product
        productsPage.clickViewProductByIndex(0);

        // 5. Verify product detail is opened
        productDetailPage.verifyProductDetailsVisible();

        // 6. Increase quantity to 4
        productDetailPage.setQuantity("4");

        // 7. Click 'Add to cart' button
        productDetailPage.addToCart();

        // 8. Click 'View Cart' button
        productsPage.clickViewCart();

        // 9. Verify that product is displayed in cart page with exact quantity
        // Verifying the quantity column specifically
        assertThat(page.locator(".cart_quantity button")).hasText("4");
    }
}