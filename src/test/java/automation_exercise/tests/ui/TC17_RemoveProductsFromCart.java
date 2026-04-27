package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC17_RemoveProductsFromCart extends BaseTest {

    @Test(description = "Test Case 17: Remove Products From Cart",priority = 17, groups = {"regression"})
    @Step("TC17: Remove products from cart")
    public void removeProductsFromCart() {
        // 1-3. Launch and Verify Home Page
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Add products to cart
        homePage.get().clickProducts();
        productsPage.get().addProductToCartByIndex(0);
        productsPage.get().clickContinueShopping();

        // 5-6. Click 'Cart' button and Verify cart page
        homePage.get().clickCart();
        assertThat(getPage()).hasURL("https://automationexercise.com/view_cart");

        // 7. Click 'X' button corresponding to particular product
        cartPage.get().removeProductByIndex(0);

        // 8. Verify that product is removed from the cart
        cartPage.get().verifyProductIsRemoved();
    }
}
