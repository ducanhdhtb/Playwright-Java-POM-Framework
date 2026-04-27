package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC12_AddProductsInCart extends BaseTest {

    @Test(description = "Test Case 12: Add Products in Cart",priority = 12, groups = {"smoke"})
    @Step("TC12: Add products to cart")
    public void addProductsToCart() {
        // 1-3. Launch and Verify Home Page
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Click 'Products' button
        homePage.get().clickProducts();

        // 5-6. Add first product and continue
        productsPage.get().addProductToCartByIndex(0);
        productsPage.get().clickContinueShopping();

        // 7. Add second product
        productsPage.get().addProductToCartByIndex(1);

        // 8. Click 'View Cart' button
        productsPage.get().clickViewCart();

        // 9. Verify both products are added
        cartPage.get().verifyCartCount(2);

        // 10. Verify their prices, quantity and total price
        // Note: Prices are hardcoded here based on the specific site data for item 1 and 2
        cartPage.get().verifyProductDetails(0, "Rs. 500", "1", "Rs. 500");
        cartPage.get().verifyProductDetails(1, "Rs. 400", "1", "Rs. 400");
    }
}
