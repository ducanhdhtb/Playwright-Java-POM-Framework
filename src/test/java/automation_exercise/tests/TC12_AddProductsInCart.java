package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC12_AddProductsInCart extends BaseTest {

    @Test(description = "Test Case 12: Add Products in Cart")
    public void addProductsToCart() {
        // 1-3. Launch and Verify Home Page
        homePage.navigate();
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click 'Products' button
        homePage.clickProducts();

        // 5-6. Add first product and continue
        productsPage.addProductToCartByIndex(0);
        productsPage.clickContinueShopping();

        // 7. Add second product
        productsPage.addProductToCartByIndex(1);

        // 8. Click 'View Cart' button
        productsPage.clickViewCart();

        // 9. Verify both products are added
        cartPage.verifyCartCount(2);

        // 10. Verify their prices, quantity and total price
        // Note: Prices are hardcoded here based on the specific site data for item 1 and 2
        cartPage.verifyProductDetails(0, "Rs. 500", "1", "Rs. 500");
        cartPage.verifyProductDetails(1, "Rs. 400", "1", "Rs. 400");
    }
}