package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ProductsPage {
    private final Page page;

    // Locators...
    private final String SEARCH_INPUT      = "input#search_product";
    private final String SEARCH_BUTTON     = "#submit_search";
    private final String PRODUCT_INFO_LIST = ".productinfo p";
    private final String PRODUCT_BOX       = ".productinfo";
    private final String ADD_TO_CART_BTN   = ".add-to-cart";
    private final String VIEW_PRODUCT_BTN  = ".choose a";
    private final String VIEW_CART_LINK    = "View Cart";
    private final String CONTINUE_SHOPPING_BTN = "button.close-modal";
    private final String brandsSidebar = "//div[@class='brands_products']";

    public ProductsPage(Page page) {
        this.page = page;
    }

    @Step("Adding product at index {0} to cart")
    public void addProductToCartByIndex(int index) {
        Locator product = page.locator(".single-products").nth(index);
        product.hover();
        product.locator(".add-to-cart").first().click();
    }

    @Step("Searching for product: '{0}'")
    public void searchProduct(String productName) {
        page.getByPlaceholder("Search Product").fill(productName);
        page.locator(SEARCH_BUTTON).click();
    }

    @Step("Clicking 'Continue Shopping' button")
    public void clickContinueShopping() {
        page.locator(CONTINUE_SHOPPING_BTN).click();
    }

    @Step("Adding the first product to cart")
    public void addFirstProductToCart() {
        page.locator(PRODUCT_BOX).first().hover();
        page.locator(ADD_TO_CART_BTN).first().click();
    }

    @Step("Clicking 'View Product' of the first item")
    public void clickViewProductOfFirstItem() {
        page.locator(VIEW_PRODUCT_BTN).first().click();
    }

    @Step("Clicking 'View Cart' link")
    public void clickViewCart() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(VIEW_CART_LINK)).first().click();
    }

    @Step("Clicking 'View Product' by index: {0}")
    public void clickViewProductByIndex(int index) {
        page.locator(".choose a").nth(index).click();
    }

    @Step("Selecting brand: {0}")
    public void selectBrand(String brandName) {
        page.locator("//a[contains(text(),'" + brandName + "')]").click();
    }

    // VERIFICATIONS
    @Step("Verifying all product names contain keyword: '{0}'")
    public void verifyAllProductNamesContain(String keyword) {
        Locator names = page.locator(PRODUCT_INFO_LIST);
        assertThat(names.first()).isVisible();
        Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
        for (Locator item : names.all()) {
            assertThat(item).containsText(pattern);
        }
    }

    @Step("Verifying brands sidebar is visible")
    public void verifyBrandsVisible() {
        assertThat(page.locator(brandsSidebar)).isVisible();
    }

    @Step("Verifying brand page title is '{0}'")
    public void verifyBrandPage(String brandName) {
        assertThat(page.locator("//h2[contains(text(),'" + brandName + "')]")).isVisible();
    }
}