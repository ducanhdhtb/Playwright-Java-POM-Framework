package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC13_VerifyProductQuantityInCart extends BaseTest {

    @Test(description = "Test Case 13: Verify Product quantity in Cart",priority = 13, groups = {"regression"})
    @Step("TC13: Verify product quantity in cart")
    public void verifyProductQuantity() {
        // 1-3. Launch and Verify Home Page
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Click 'View Product' for the first product
        productsPage.get().clickViewProductByIndex(0);

        // 5. Verify product detail is opened
        productDetailPage.get().verifyProductDetailsVisible();

        // 6. Increase quantity to 4
        productDetailPage.get().setQuantity("4");

        // 7. Click 'Add to cart' button
        productDetailPage.get().addToCart();

        // 8. Click 'View Cart' button
        productsPage.get().clickViewCart();

        // 9. Verify that product is displayed in cart page with exact quantity
        // Verifying the quantity column specifically
        assertThat(getPage().locator(".cart_quantity button")).hasText("4");
    }
}
