package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.ExcelReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * TC38: Quantity field — Boundary Value Analysis + Equivalence Partitioning
 *
 * Field: input#quantity  type=number  min=1  max=(none)
 *
 * Partitions:
 *   - Invalid (below min): 0, -1, -999
 *   - Valid lower boundary: 1
 *   - Valid typical: 2, 5, 10
 *   - Valid upper boundary: 100, 999 (no max defined)
 *   - Invalid non-numeric: "abc", " ", "1.5"
 */
public class TC38_QuantityBoundaryTests extends BaseTest {

    @DataProvider(name = "validQuantities")
    public static Object[][] validQuantities() {
        // Data moved to Excel: sheet name should be 'validQuantities'
        return ExcelReader.getTestData("src/test/resources/AutomationTestData.xlsx", "validQuantities");
    }

    @DataProvider(name = "invalidQuantities")
    public static Object[][] invalidQuantities() {
        // Data moved to Excel: sheet name should be 'invalidQuantities'
        return ExcelReader.getTestData("src/test/resources/AutomationTestData.xlsx", "invalidQuantities");
    }

    @Test(
            description = "TC38a: Valid quantity values are accepted and reflected in cart",
            dataProvider = "validQuantities",
            priority = 38,
            groups = {"regression", "boundary"}
    )
    @Description("BVA: Valid quantity values (1, 2, 10, 99, 100) should be accepted and shown in cart")
    @Step("TC38a: Add product with quantity '{0}' — expect cart quantity '{1}'")
    public void testValidQuantityAddedToCart(String inputQty, String expectedQty) {
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickProducts();
        productsPage.get().clickViewProductOfFirstItem();

        productDetailPage.get().verifyProductDetailsVisible();
        productDetailPage.get().setQuantity(inputQty);
        productDetailPage.get().addToCart();
        productsPage.get().clickViewCart();

        assertThat(getPage().locator(".cart_quantity button")).hasText(expectedQty);
    }

    @Test(
            description = "TC38b: Quantity 0 or negative — browser validation prevents add to cart",
            dataProvider = "invalidQuantities",
            priority = 38,
            groups = {"regression", "boundary", "negative"}
    )
    @Description("BVA: Quantity 0 or negative should be blocked by HTML5 min=1 validation")
    @Step("TC38b: Quantity '{0}' should be blocked or corrected to min=1")
    public void testInvalidQuantityBlocked(String inputQty) {
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickProducts();
        productsPage.get().clickViewProductOfFirstItem();

        productDetailPage.get().verifyProductDetailsVisible();

        // Set invalid quantity
        getPage().locator("#quantity").fill(inputQty);

        // Try to add to cart
        getPage().locator("button.cart").click();

        // Either: stays on product page (validation blocked) OR
        // browser corrects to min=1 and adds to cart
        // We verify the page doesn't crash and quantity in cart is >= 1
        String currentUrl = getPage().url();
        if (currentUrl.contains("view_cart")) {
            // Browser corrected to min=1
            String cartQty = getPage().locator(".cart_quantity button").innerText().trim();
            int qty = Integer.parseInt(cartQty);
            assert qty >= 1 : "Cart quantity should be at least 1, got: " + qty;
        } else {
            // Stayed on product page — validation blocked
            assertThat(getPage()).hasURL(
                    java.util.regex.Pattern.compile(".*/product_details/.*"));
        }
    }

    @Test(
            description = "TC38c: Decimal quantity is truncated or blocked",
            priority = 38,
            groups = {"regression", "boundary", "negative"}
    )
    @Description("EP: Decimal quantity '1.5' should be truncated to 1 or blocked")
    @Step("TC38c: Decimal quantity '1.5' behavior")
    public void testDecimalQuantityBehavior() {
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickProducts();
        productsPage.get().clickViewProductOfFirstItem();

        productDetailPage.get().verifyProductDetailsVisible();
        getPage().locator("#quantity").fill("1.5");
        getPage().locator("button.cart").click();

        // Navigate to cart if modal appeared
        try {
            productsPage.get().clickViewCart();
        } catch (Exception ignored) {}

        if (getPage().url().contains("view_cart")) {
            String cartQty = getPage().locator(".cart_quantity button").innerText().trim();
            // Should be 1 (truncated) not 1.5
            assertEquals(cartQty, "1", "Decimal quantity should be truncated to 1");
        }
    }
}
