package automation_exercise.tests;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC22: Product search and filter scenarios.
 *
 * Covers:
 *   a) Search returns relevant results
 *   b) Search with no results shows empty state
 *   c) Category filter shows correct products
 */
public class TC22_ProductSearchAndFilter extends BaseTest {

    @DataProvider(name = "searchKeywords")
    public static Object[][] searchKeywords() {
        return new Object[][]{
                {"top"},
                {"dress"},
                {"jeans"},
        };
    }

    @Test(
            description = "TC22a: Search product returns matching results",
            dataProvider = "searchKeywords",
            groups = {"regression", "search"}
    )
    @Description("Searches for a keyword and verifies all results contain that keyword")
    @Step("TC22a: Search for '{0}' returns matching products")
    public void testSearchReturnsMatchingResults(String keyword) {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();

        productsPage.searchProduct(keyword);

        // Verify search results heading
        assertThat(page.locator("h2.title.text-center"))
                .containsText("Searched Products");

        // Verify at least one product is shown
        assertThat(page.locator(".productinfo").first()).isVisible();

        // Verify all product names contain the keyword
        productsPage.verifyAllProductNamesContain(keyword);
    }

    @Test(
            description = "TC22b: Search with gibberish returns no products",
            groups = {"regression", "negative", "search"}
    )
    @Description("Searches for a nonsense keyword and verifies no products are shown")
    @Step("TC22b: Search with no results shows empty state")
    public void testSearchWithNoResults() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();

        productsPage.searchProduct("xyzzy_no_such_product_12345");

        // Verify searched products section is shown
        assertThat(page.locator("h2.title.text-center"))
                .containsText("Searched Products");

        // Verify no product cards are visible
        assertThat(page.locator(".productinfo")).hasCount(0);
    }

    @Test(
            description = "TC22c: Filter by Women > Dress category shows correct products",
            groups = {"regression", "search"}
    )
    @Description("Clicks Women > Dress category and verifies the category page loads")
    @Step("TC22c: Category filter Women > Dress")
    public void testCategoryFilterWomenDress() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));

        homePage.verifyCategoriesVisible();
        homePage.selectCategory("a[href='#Women']", "Dress");

        // Verify category page title
        homePage.verifyCategoryPageTitle("Women - Dress Products");
        assertThat(page.locator(".features_items").first()).isVisible();
    }
}
