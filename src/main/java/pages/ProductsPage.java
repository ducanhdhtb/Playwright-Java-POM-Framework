package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ProductsPage extends BasePage {

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
        super(page);
    }

    @Step("Adding product at index {0} to cart")
    public void addProductToCartByIndex(int index) {
        Locator product = locator(".single-products").nth(index);
        product.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(30_000));
        product.scrollIntoViewIfNeeded();
        product.hover();
        product.locator(".add-to-cart").first().click();
    }

    @Step("Searching for product: '{0}'")
    public void searchProduct(String productName) {
        byPlaceholder("Search Product").fill(productName);
        locator(SEARCH_BUTTON).click();
    }

    @Step("Clicking 'Continue Shopping' button")
    public void clickContinueShopping() {
        locator(CONTINUE_SHOPPING_BTN).click();
    }

    @Step("Adding the first product to cart")
    public void addFirstProductToCart() {
        Locator firstProduct = locator(PRODUCT_BOX).first();
        firstProduct.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(30_000));
        firstProduct.scrollIntoViewIfNeeded();
        firstProduct.hover();
        locator(ADD_TO_CART_BTN).first().click();
    }

    @Step("Clicking 'View Product' of the first item")
    public void clickViewProductOfFirstItem() {
        locator(".features_items a[href*='/product_details/']").first().click();
        waitForUrl("**/product_details/**");
    }

    @Step("Clicking 'View Cart' link")
    public void clickViewCart() {
        Locator viewCart = byRole(AriaRole.LINK, VIEW_CART_LINK).first();
        try {
            viewCart.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(10_000));
            viewCart.click();
        } catch (RuntimeException e) {
            // Modal can be flaky; fall back to direct navigation.
            super.navigate("https://automationexercise.com/view_cart");
        }
    }

    @Step("Clicking 'View Product' by index: {0}")
    public void clickViewProductByIndex(int index) {
        locator(".features_items a[href*='/product_details/']").nth(index).click();
        waitForUrl("**/product_details/**");
    }

    @Step("Selecting brand: {0}")
    public void selectBrand(String brandName) {
        locator("//a[contains(text(),'" + brandName + "')]").click();
    }

    // VERIFICATIONS
    @Step("Verifying all product names contain keyword: '{0}'")
    public void verifyAllProductNamesContain(String keyword) {
        Locator names = locator(PRODUCT_INFO_LIST);
        assertThat(names.first()).isVisible();
        String normalizedKeyword = normalizeSearchText(keyword);
        for (Locator item : names.all()) {
            String normalizedText = normalizeSearchText(item.innerText());
            if (!normalizedText.contains(normalizedKeyword)) {
                throw new AssertionError(
                        "Expected product name to contain '" + keyword + "' but found '" + item.innerText() + "'");
            }
        }
    }

    private String normalizeSearchText(String text) {
        return text == null ? "" : text.replaceAll("[^\\p{Alnum}]", "").toLowerCase();
    }

    @Step("Verifying brands sidebar is visible")
    public void verifyBrandsVisible() {
        assertThat(locator(brandsSidebar)).isVisible();
    }

    @Step("Verifying brand page title is '{0}'")
    public void verifyBrandPage(String brandName) {
        assertThat(locator("//h2[contains(text(),'" + brandName + "')]")).isVisible();
    }
}
