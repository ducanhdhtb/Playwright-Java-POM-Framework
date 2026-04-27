package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import utils.ExcelReader;

/**
 * TC37: Verify direct URL navigation to all category pages
 *
 * Discovered via MCP exploration: all 7 category_products URLs
 * are directly accessible. Tests deep-link navigation without
 * going through the sidebar UI.
 */
public class TC37_DirectCategoryUrlNavigation extends BaseTest {

    @DataProvider(name = "categoryUrls")
    public static Object[][] categoryUrls() {
        // Data moved to Excel: sheet name should be 'categoryUrls'
        return ExcelReader.getTestData("src/test/resources/AutomationTestData.xlsx", "categoryUrls");
    }

    @Test(
            description = "TC37: Direct URL navigation to category page loads correctly",
            dataProvider = "categoryUrls",
            priority = 37,
            groups = {"regression"}
    )
    @Description("Navigates directly to each category URL and verifies page loads with correct title and products")
    @Step("TC37: Direct navigation to '{0}' shows '{1}'")
    public void testDirectCategoryUrlNavigation(String path, String expectedTitle, String parentCategory) {
        String url = "https://automationexercise.com" + path;

        // Navigate directly via URL (no sidebar interaction)
        homePage.get().navigate(url);

        // Verify correct URL
        assertThat(getPage()).hasURL(url);

        // Verify category title
        homePage.get().verifyCategoryPageTitle(expectedTitle);

        // Verify products list is visible
        assertThat(getPage().locator(".features_items")).isVisible();

        // Verify sidebar still shows categories (navigation intact)
        homePage.get().verifyCategoriesVisible();

        // Verify parent category is highlighted/visible in sidebar
        assertThat(getPage().locator(".left-sidebar")).containsText(parentCategory);
    }
}
