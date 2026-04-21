package cucumber.steps;

import cucumber.context.ScenarioContext;
import io.cucumber.java.en.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Step definitions for product_browsing.feature
 */
public class ProductSteps {

    private final ScenarioContext ctx;

    public ProductSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ── Products page navigation ──────────────────────────────────────────────

    @Then("the user is on the {string} page at URL {string}")
    public void verifyOnPageWithUrl(String pageName, String url) {
        assertThat(ctx.page).hasURL(url);
        assertThat(ctx.page.getByRole(
                com.microsoft.playwright.options.AriaRole.HEADING,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName(pageName).setExact(true)
        )).isVisible();
    }

    @Then("the products list is visible")
    public void verifyProductsListVisible() {
        assertThat(ctx.page.locator(".features_items")).isVisible();
    }

    @Then("the brands sidebar is visible")
    public void verifyBrandsSidebarVisible() {
        ctx.productsPage.verifyBrandsVisible();
    }

    @Then("the category sidebar is visible")
    public void verifyCategorySidebarVisible() {
        ctx.homePage.verifyCategoriesVisible();
    }

    // ── View product ──────────────────────────────────────────────────────────

    @When("the user clicks \"View Product\" for the first item")
    public void clickViewProductFirst() {
        ctx.productsPage.clickViewProductOfFirstItem();
    }

    @When("the user clicks \"View Product\" for product at index {int}")
    public void clickViewProductByIndex(int index) {
        ctx.productsPage.clickViewProductByIndex(index);
    }

    // ── Product detail assertions ─────────────────────────────────────────────

    @Then("the URL matches pattern {string}")
    public void verifyUrlPattern(String pattern) {
        assertThat(ctx.page).hasURL(java.util.regex.Pattern.compile(pattern));
    }

    @Then("the product name is visible")
    public void verifyProductNameVisible() {
        assertThat(ctx.productDetailPage.productName()).isVisible();
    }

    @Then("the product category is visible")
    public void verifyProductCategoryVisible() {
        assertThat(ctx.productDetailPage.category()).isVisible();
    }

    @Then("the product price is visible")
    public void verifyProductPriceVisible() {
        assertThat(ctx.productDetailPage.price()).isVisible();
    }

    @Then("the product availability is visible")
    public void verifyProductAvailabilityVisible() {
        assertThat(ctx.productDetailPage.availability()).isVisible();
    }

    @Then("the product condition is visible")
    public void verifyProductConditionVisible() {
        assertThat(ctx.productDetailPage.condition()).isVisible();
    }

    @Then("the product brand is visible")
    public void verifyProductBrandVisible() {
        assertThat(ctx.productDetailPage.brand()).isVisible();
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @When("the user searches for {string}")
    public void searchProduct(String keyword) {
        ctx.productsPage.searchProduct(keyword);
    }

    @Then("the section title shows {string}")
    public void verifySectionTitle(String title) {
        assertThat(ctx.page.locator(".title.text-center")).containsText(title);
    }

    @Then("all displayed product names contain {string}")
    public void verifyAllProductNamesContain(String keyword) {
        ctx.productsPage.verifyAllProductNamesContain(keyword);
    }

    @Then("at least one product is visible")
    public void verifyAtLeastOneProductVisible() {
        assertThat(ctx.page.locator(".productinfo").first()).isVisible();
    }

    @Then("no products are displayed")
    public void verifyNoProductsDisplayed() {
        assertThat(ctx.page.locator(".productinfo")).hasCount(0);
    }

    // ── Category ──────────────────────────────────────────────────────────────

    @When("the user selects category {string} and sub-category {string}")
    public void selectCategory(String mainCategory, String subCategory) {
        String selector = "a[href='#" + mainCategory + "']";
        ctx.homePage.selectCategory(selector, subCategory);
    }

    @Then("the category page title contains {string}")
    public void verifyCategoryPageTitle(String title) {
        ctx.homePage.verifyCategoryPageTitle(title);
    }

    // ── Brand ─────────────────────────────────────────────────────────────────

    @When("the user selects brand {string}")
    public void selectBrand(String brand) {
        ctx.homePage.selectBrand(brand);
    }

    @Then("the brand page title contains {string}")
    public void verifyBrandPageTitle(String title) {
        ctx.homePage.verifyCategoryPageTitle(title);
    }
}
