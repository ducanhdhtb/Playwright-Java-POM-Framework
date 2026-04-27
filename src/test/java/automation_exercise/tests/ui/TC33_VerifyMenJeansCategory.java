package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC33: Verify Men > Jeans category products
 *
 * Discovered via MCP exploration: Men has 2 sub-categories
 * (Tshirts, Jeans) but only Tshirts was tested in TC18.
 */
public class TC33_VerifyMenJeansCategory extends BaseTest {

    @Test(
            description = "TC33: Verify Men > Jeans category page loads with products",
            priority = 33,
            groups = {"regression"}
    )
    @Description("Clicks Men > Jeans category and verifies category page with products")
    @Step("TC33: View Men > Jeans category")
    public void testVerifyMenJeansCategory() {
        // 1-3. Navigate and verify home page
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().verifyCategoriesVisible();

        // 4. Click Men > Jeans
        homePage.get().selectCategory("a[href='#Men']", "Jeans");

        // 5. Verify category page title
        homePage.get().verifyCategoryPageTitle("MEN - JEANS PRODUCTS");

        // 6. Verify products are displayed
        assertThat(getPage().locator(".features_items")).isVisible();
        assertThat(getPage().locator(".single-products").first()).isVisible();

        // 7. Verify URL contains category_products/6 (Jeans = category 6)
        assertThat(getPage()).hasURL(
                java.util.regex.Pattern.compile(".*/category_products/6.*"));
    }
}
