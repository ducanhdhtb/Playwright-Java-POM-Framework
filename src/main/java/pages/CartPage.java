package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CartPage extends BasePage {

    // Locators...
    private final String FOOTER = "#footer";
    private final String SUBSCRIPTION_TITLE = "h2:has-text('Subscription')";
    private final String SUBSCRIBE_EMAIL = "#susbscribe_email";
    private final String SUBSCRIBE_BUTTON = "#subscribe";
    private final String SUCCESS_MESSAGE = ".alert-success";
    private final String CART_ROWS = "#cart_info_table tbody tr";
    private final String PROCEED_TO_CHECKOUT_BTN = ".check_out";
    private final String REGISTER_LOGIN_MODAL_LINK = "u:has-text('Register / Login')";
    private final String DELETE_PRODUCT_BTN = ".cart_quantity_delete";
    private final String EMPTY_CART_MSG = "#empty_cart";

    public CartPage(Page page) {
        super(page);
    }

    @Step("Clicking 'Proceed To Checkout' button")
    public void clickProceedToCheckout() {
        locator(PROCEED_TO_CHECKOUT_BTN).click();
    }

    @Step("Clicking 'Register / Login' link on modal")
    public void clickRegisterLoginModal() {
        locator(REGISTER_LOGIN_MODAL_LINK).click();
    }

    @Step("Scrolling to footer on cart page")
    public void scrollToFooter() {
        locator(FOOTER).scrollIntoViewIfNeeded();
    }

    @Step("Subscribing with email: {0}")
    public void subscribe(String email) {
        locator(SUBSCRIBE_EMAIL).fill(email);
        locator(SUBSCRIBE_BUTTON).click();
    }

    @Step("Clicking 'Proceed To Checkout'")
    public void proceedToCheckout() {
        byText("Proceed To Checkout").click();
    }

    @Step("Clicking 'Register / Login' on checkout modal")
    public void clickRegisterLoginOnModal() {
        byRole(AriaRole.LINK, "Register / Login").click();
    }

    @Step("Removing product at index {0} from cart")
    public void removeProductByIndex(int index) {
        locator(DELETE_PRODUCT_BTN).nth(index).click();
    }

    // VERIFICATIONS
    @Step("Verifying 'SUBSCRIPTION' title is visible")
    public void verifySubscriptionTitleIsVisible() {
        assertThat(locator(SUBSCRIPTION_TITLE)).isVisible();
    }

    @Step("Verifying subscription success message")
    public void verifySuccessMessage() {
        assertThat(locator(SUCCESS_MESSAGE)).isVisible();
        assertThat(locator(SUCCESS_MESSAGE)).hasText("You have been successfully subscribed!");
    }

    @Step("Verifying cart contains {0} products")
    public void verifyCartCount(int expectedCount) {
        assertThat(locator(CART_ROWS)).hasCount(expectedCount);
    }

    @Step("Verifying product details in cart at row {0}: Price={1}, Quantity={2}, Total={3}")
    public void verifyProductDetails(int rowIndex, String price, String quantity, String total) {
        Locator row = locator(CART_ROWS).nth(rowIndex);
        assertThat(row.locator(".cart_price")).hasText(price);
        assertThat(row.locator(".cart_quantity")).hasText(quantity);
        assertThat(row.locator(".cart_total")).hasText(total);
    }

    @Step("Verifying product is removed from cart")
    public void verifyProductIsRemoved() {
        assertThat(locator(EMPTY_CART_MSG)).isVisible();
    }
}
