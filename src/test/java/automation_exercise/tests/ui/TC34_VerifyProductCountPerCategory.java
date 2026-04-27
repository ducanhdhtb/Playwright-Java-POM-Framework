package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;
import utils.ExcelReader;

/**
 * TC34: Verify each category page displays at least 1 product
 *
 * Discovered via MCP exploration: category_products/1 has 3 products.
 * All 7 category URLs should return products, not empty pages.
 */
public class TC34_VerifyProductCountPerCategory extends BaseTest {

    @DataProvider(name = "categoryData")
    public static Object[][] categoryData() {
        // Data moved to Excel: sheet name should be 'categoryData'
        return ExcelReader.getTestData("src/test/resources/AutomationTestData.xlsx", "categoryData");
    }

    @Test(
            description = "TC34: Each category URL displays correct title and at least 1 product",
            dataProvider = "categoryData",
            priority = 34,
            groups = {"regression"}
    )
    @Description("Navigates directly to each category URL and verifies title + product count > 0")
    @Step("TC34: Verify category '{0}' shows title '{1}' with products")
    public void testProductCountPerCategory(String categoryUrl, String expectedTitle) {
        // Navigate directly to category URL
        homePage.get().navigate("https://automationexercise.com" + categoryUrl);

        // Verify category title
        homePage.get().verifyCategoryPageTitle(expectedTitle);

        // Verify at least 1 product is displayed
        assertThat(getPage().locator(".features_items")).isVisible();

        int productCount = getPage().locator(".single-products").count();
        assertTrue(productCount > 0,
                "Category '" + expectedTitle + "' should have at least 1 product, found: " + productCount);
    }
}
