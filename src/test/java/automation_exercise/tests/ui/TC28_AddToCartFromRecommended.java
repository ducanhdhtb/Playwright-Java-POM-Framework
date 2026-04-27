package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC22 (site): Add to cart from Recommended items
 */
public class TC28_AddToCartFromRecommended extends BaseTest {

    @Test(
            description = "TC28: Add product from Recommended Items section to cart",
            priority = 28,
            groups = {"regression"}
    )
    @Description("Scrolls to bottom, finds Recommended Items, adds to cart, verifies in cart")
    @Step("TC28: Add recommended item to cart")
    public void testAddToCartFromRecommended() {
        // 1-3. Navigate and verify home page
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Scroll to bottom
        homePage.get().scrollToBottom();

        // 5. Verify RECOMMENDED ITEMS are visible
        homePage.get().verifyRecommendedItemsVisible();

        // 6. Click Add To Cart on first recommended product
        homePage.get().addFirstRecommendedItemToCart();

        // 7. Click View Cart
        productsPage.get().clickViewCart();

        // 8. Verify product is in cart
        assertThat(getPage()).hasURL("https://automationexercise.com/view_cart");
        cartPage.get().verifyCartCount(1);
    }
}
