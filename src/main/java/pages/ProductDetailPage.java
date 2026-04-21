package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ProductDetailPage {
    private final Page page;

    // Locators...
    private final String PRODUCT_INFO_CONTAINER = ".product-information";
    private final String QUANTITY_INPUT = "#quantity";
    private final String ADD_TO_CART_BTN = "button.cart";
    private final String PRODUCT_NAME = ".product-information h2";
    private final String PRODUCT_PRICE = ".product-information span span";
    private final String INFO_TEXT_LOCATOR = ".product-information p";

    public ProductDetailPage(Page page) {
        this.page = page;
    }

    // ELEMENT GETTERS
    public Locator productName() { return page.locator(PRODUCT_NAME); }
    public Locator price() { return page.locator(PRODUCT_PRICE); }
    public Locator category() { return page.locator(INFO_TEXT_LOCATOR).filter(new Locator.FilterOptions().setHasText("Category:")); }
    public Locator availability() { return page.locator(INFO_TEXT_LOCATOR).filter(new Locator.FilterOptions().setHasText("Availability:")); }
    public Locator condition() { return page.locator(INFO_TEXT_LOCATOR).filter(new Locator.FilterOptions().setHasText("Condition:")); }
    public Locator brand() { return page.locator(INFO_TEXT_LOCATOR).filter(new Locator.FilterOptions().setHasText("Brand:")); }

    // ACTIONS
    @Step("Setting product quantity to: {0}")
    public void setQuantity(String quantity) {
        page.locator(QUANTITY_INPUT).fill(quantity);
    }

    @Step("Clicking 'Add to cart' button")
    public void addToCart() {
        page.locator(ADD_TO_CART_BTN).click();
    }

    @Step("Getting review section locator")
    public Locator reviewSection() {
        return page.locator("#review-section, .write-review, #review-form");
    }

    @Step("Writing a review with name '{0}', email '{1}'")
    public void writeReview(String name, String email, String review) {
        page.locator("#name").fill(name);
        page.locator("#email").fill(email);
        page.locator("#review").fill(review);
    }

    @Step("Submitting the review")
    public void submitReview() {
        page.locator("#button-review").click();
    }

    @Step("Verifying review success message")
    public void verifyReviewSuccess() {
        assertThat(page.locator(".alert-success")).containsText("Thank you for your review.");
    }

    // VERIFICATIONS
    @Step("Verifying product details are visible")
    public void verifyProductDetailsVisible() {
        assertThat(page.locator(PRODUCT_INFO_CONTAINER)).isVisible();
    }
}