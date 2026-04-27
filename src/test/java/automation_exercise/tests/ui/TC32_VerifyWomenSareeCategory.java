package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC32: Verify Women > Saree category products
 *
 * Discovered via MCP exploration: Women has 3 sub-categories
 * (Dress, Tops, Saree) but only Dress was tested in TC18/TC22.
 */
public class TC32_VerifyWomenSareeCategory extends BaseTest {

    @Test(
            description = "TC32: Verify Women > Saree category page loads with products",
            priority = 32,
            groups = {"regression"}
    )
    @Description("Clicks Women > Saree category and verifies category page with products")
    @Step("TC32: View Women > Saree category")
    public void testVerifyWomenSareeCategory() {
        // 1-3. Navigate and verify home page
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().verifyCategoriesVisible();

        // 4. Click Women > Saree
        homePage.get().selectCategory("a[href='#Women']", "Saree");

        // 5. Verify category page title
        homePage.get().verifyCategoryPageTitle("WOMEN - SAREE PRODUCTS");

        // 6. Verify products are displayed
        assertThat(getPage().locator(".features_items")).isVisible();
        assertThat(getPage().locator(".single-products").first()).isVisible();

        // 7. Verify URL contains category_products/7 (Saree = category 7)
        assertThat(getPage()).hasURL(
                java.util.regex.Pattern.compile(".*/category_products/7.*"));
    }
}
