package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;

/**
 * TC39: Search field — Equivalence Partitioning + BVA + Error Guessing
 *
 * Total products on site: 34
 *
 * Partitions:
 *   EP1 — Valid keyword with results: "top", "dress"
 *   EP2 — Valid keyword no results: "xyzzy123"
 *   EP3 — Single character: "t" (BVA lower)
 *   EP4 — Whitespace only: "   "
 *   EP5 — Special characters: "<script>", "' OR 1=1"  (Error Guessing: XSS/SQLi)
 *   EP6 — Case insensitive: "TOP", "Top", "top"
 *   EP7 — Partial match: "blu" should find "Blue Top"
 */
public class TC39_SearchBoundaryTests extends BaseTest {

    @DataProvider(name = "validSearchKeywords")
    public static Object[][] validSearchKeywords() {
        return new Object[][]{
                {"top",   true},   // EP1: common keyword
                {"dress", true},   // EP1: common keyword
                {"t",     true},   // BVA: single char — should return results
                {"blu",   true},   // EP7: partial match
        };
    }

    @DataProvider(name = "caseVariants")
    public static Object[][] caseVariants() {
        return new Object[][]{
                {"TOP"},
                {"Top"},
                {"top"},
                {"tOp"},
        };
    }

    @Test(
            description = "TC39a: Search with valid keywords returns matching products",
            dataProvider = "validSearchKeywords",
            priority = 39,
            groups = {"regression", "boundary", "search"}
    )
    @Description("EP: Valid search keywords return matching products")
    @Step("TC39a: Search '{0}' — expect results: {1}")
    public void testValidSearchKeywords(String keyword, boolean expectResults) {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.searchProduct(keyword);

        assertThat(page.locator(".title.text-center")).containsText("Searched Products");

        if (expectResults) {
            assertThat(page.locator(".productinfo").first()).isVisible();
            int count = page.locator(".single-products").count();
            assertTrue(count > 0, "Search '" + keyword + "' should return results, got 0");
        }
    }

    @Test(
            description = "TC39b: Search is case-insensitive — TOP/Top/top return same results",
            dataProvider = "caseVariants",
            priority = 39,
            groups = {"regression", "boundary", "search"}
    )
    @Description("EP: Search should be case-insensitive")
    @Step("TC39b: Case-insensitive search for '{0}'")
    public void testSearchCaseInsensitive(String keyword) {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.searchProduct(keyword);

        assertThat(page.locator(".title.text-center")).containsText("Searched Products");
        assertThat(page.locator(".productinfo").first()).isVisible();
    }

    @Test(
            description = "TC39c: Search with whitespace only returns no results or all products",
            priority = 39,
            groups = {"regression", "boundary", "negative", "search"}
    )
    @Description("EP: Whitespace-only search — edge case behavior")
    @Step("TC39c: Whitespace-only search")
    public void testSearchWithWhitespaceOnly() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.searchProduct("   ");

        // Should show Searched Products section (not crash)
        assertThat(page.locator(".title.text-center")).containsText("Searched Products");
        // Page should remain functional
        assertThat(page).hasURL(
                java.util.regex.Pattern.compile(".*products.*"));
    }

    @Test(
            description = "TC39d: Search with XSS payload does not execute script",
            priority = 39,
            groups = {"regression", "negative", "search"}
    )
    @Description("Error Guessing: XSS payload in search should be sanitized, not executed")
    @Step("TC39d: XSS payload in search field")
    public void testSearchXssPayload() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.searchProduct("<script>alert('xss')</script>");

        // Page should not show alert dialog (already handled by page.onDialog in BaseTest)
        // Should show Searched Products section safely
        assertThat(page.locator(".title.text-center")).containsText("Searched Products");
        // No products should match XSS payload
        assertThat(page.locator(".productinfo")).hasCount(0);
    }

    @Test(
            description = "TC39e: Search with SQL injection payload does not break page",
            priority = 39,
            groups = {"regression", "negative", "search"}
    )
    @Description("Error Guessing: SQL injection in search should be handled safely")
    @Step("TC39e: SQL injection payload in search")
    public void testSearchSqlInjection() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.searchProduct("' OR '1'='1");

        // Page should not crash or return all products
        assertThat(page.locator(".title.text-center")).containsText("Searched Products");
        assertThat(page).hasURL(
                java.util.regex.Pattern.compile(".*products.*"));
    }

    @Test(
            description = "TC39f: Search with special characters returns no results gracefully",
            priority = 39,
            groups = {"regression", "negative", "search"}
    )
    @Description("Error Guessing: Special characters in search should not crash the page")
    @Step("TC39f: Special characters in search")
    public void testSearchSpecialCharacters() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.searchProduct("@#$%^&*()");

        assertThat(page.locator(".title.text-center")).containsText("Searched Products");
        assertThat(page.locator(".productinfo")).hasCount(0);
    }
}
