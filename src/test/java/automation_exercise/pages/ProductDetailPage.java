package automation_exercise.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object Class for the Product Detail Page.
 * Handles quantity adjustments, product information retrieval, and cart actions.
 */
public class ProductDetailPage {
    private final Page page;

    // ==========================================
    // 1. LOCATORS - Centralized Element Selectors
    // ==========================================
    private final String PRODUCT_INFO_CONTAINER = ".product-information";
    private final String QUANTITY_INPUT          = "#quantity";
    private final String ADD_TO_CART_BTN         = "button.cart";

    // Detailed Information Selectors
    private final String PRODUCT_NAME            = ".product-information h2";
    private final String PRODUCT_PRICE           = ".product-information span span";
    private final String INFO_TEXT_LOCATOR       = ".product-information p";

    public ProductDetailPage(Page page) {
        this.page = page;
    }

    // ==========================================
    // 2. ELEMENT GETTERS - For Data Verification
    // ==========================================

    /** @return Locator for the product name header. */
    public Locator productName() { return page.locator(PRODUCT_NAME); }

    /** @return Locator for the price span. */
    public Locator price() { return page.locator(PRODUCT_PRICE); }

    /** @return Locator for Category information. */
    public Locator category() {
        return page.locator(INFO_TEXT_LOCATOR).filter(new Locator.FilterOptions().setHasText("Category:"));
    }

    /** @return Locator for Availability status. */
    public Locator availability() {
        return page.locator(INFO_TEXT_LOCATOR).filter(new Locator.FilterOptions().setHasText("Availability:"));
    }

    /** @return Locator for Product Condition. */
    public Locator condition() {
        return page.locator(INFO_TEXT_LOCATOR).filter(new Locator.FilterOptions().setHasText("Condition:"));
    }

    /** @return Locator for Product Brand. */
    public Locator brand() {
        return page.locator(INFO_TEXT_LOCATOR).filter(new Locator.FilterOptions().setHasText("Brand:"));
    }

    // ==========================================
    // 3. ACTIONS - Business Logic Methods
    // ==========================================

    /**
     * Sets the product quantity by filling the input field.
     * @param quantity The string value of the desired quantity (e.g., "4").
     */
    public void setQuantity(String quantity) {
        page.locator(QUANTITY_INPUT).fill(quantity);
    }

    /**
     * Clicks the 'Add to cart' button located within the product details.
     */
    public void addToCart() {
        page.locator(ADD_TO_CART_BTN).click();
    }

    // ==========================================
    // 4. VERIFICATIONS - Assertion Methods
    // ==========================================

    /**
     * Verifies that the product details container is visible to the user.
     */
    public void verifyProductDetailsVisible() {
        assertThat(page.locator(PRODUCT_INFO_CONTAINER)).isVisible();
    }
}