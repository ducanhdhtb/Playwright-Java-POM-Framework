package automation_exercise.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.Page.GetByRoleOptions; // Import này đã đúng

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CartPage {
    private final Page page;

    // ==========================================
    // 1. LOCATORS
    // ==========================================
    private final String FOOTER             = "#footer";
    private final String SUBSCRIPTION_TITLE = "h2:has-text('Subscription')";
    private final String SUBSCRIBE_EMAIL    = "#susbscribe_email";
    private final String SUBSCRIBE_BUTTON   = "#subscribe";
    private final String SUCCESS_MESSAGE    = ".alert-success";
    private final String CART_ROWS = "#cart_info_table tbody tr";

    public CartPage(Page page) {
        this.page = page;
    }



    // ==========================================
    // 2. ACTIONS
    // ==========================================

    /**
     * Scrolls down to the footer section of the Cart page.
     */
    public void scrollToFooter() {
        page.locator(FOOTER).scrollIntoViewIfNeeded();
    }

    /**
     * Fills the subscription email and clicks the subscribe button.
     * @param email The email address to use for subscription.
     */
    public void subscribe(String email) {
        page.locator(SUBSCRIBE_EMAIL).fill(email);
        page.locator(SUBSCRIBE_BUTTON).click();
    }

    public void proceedToCheckout() {
        page.getByText("Proceed To Checkout").click();
    }

    public void clickRegisterLoginOnModal() {
        // Sử dụng tên ngắn gọn nhờ vào các dòng import ở trên
        page.getByRole(AriaRole.LINK, new GetByRoleOptions().setName("Register / Login")).click();
    }

    // ==========================================
    // 3. VERIFICATIONS
    // ==========================================

    /**
     * Verifies that the 'SUBSCRIPTION' header is visible in the cart footer.
     */
    public void verifySubscriptionTitleIsVisible() {
        assertThat(page.locator(SUBSCRIPTION_TITLE)).isVisible();
    }

    /**
     * Verifies that the success message is displayed after a successful subscription.
     */
    public void verifySuccessMessage() {
        assertThat(page.locator(SUCCESS_MESSAGE)).isVisible();
        assertThat(page.locator(SUCCESS_MESSAGE)).hasText("You have been successfully subscribed!");
    }

    /**
     * Verifies the number of items present in the cart.
     * @param expectedCount The number of products expected.
     */
    public void verifyCartCount(int expectedCount) {
        assertThat(page.locator(CART_ROWS)).hasCount(expectedCount);
    }

    /**
     * Verifies details for a specific product row in the cart.
     * @param rowIndex The zero-based index of the row.
     * @param price Expected price string.
     * @param quantity Expected quantity string.
     * @param total Expected total price string.
     */
    public void verifyProductDetails(int rowIndex, String price, String quantity, String total) {
        Locator row = page.locator(CART_ROWS).nth(rowIndex);
        assertThat(row.locator(".cart_price")).hasText(price);
        assertThat(row.locator(".cart_quantity")).hasText(quantity);
        assertThat(row.locator(".cart_total")).hasText(total);
    }

}