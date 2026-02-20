package automation_exercise.pages;

import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object Class for the Checkout and Payment process.
 */
public class CheckoutPage {
    private final Page page;

    // Locators
    private final String COMMENT_AREA = "textarea[name='message']";
    private final String PLACE_ORDER_BTN = "a[href='/payment']";
    private final String NAME_ON_CARD = "input[name='name_on_card']";
    private final String CARD_NUMBER = "input[name='card_number']";
    private final String CVC = "input[name='cvc']";
    private final String EXP_MONTH = "input[name='expiry_month']";
    private final String EXP_YEAR = "input[name='expiry_year']";
    private final String PAY_BUTTON = "#submit";
    private final String SUCCESS_MESSAGE = "//p[normalize-space()='Congratulations! Your order has been confirmed!']";

    public CheckoutPage(Page page) {
        this.page = page;
    }

    public void enterComment(String comment) {
        page.locator(COMMENT_AREA).fill(comment);
    }

    public void clickPlaceOrder() {
        page.locator(PLACE_ORDER_BTN).click();
    }

    public void enterPaymentDetails(String name, String card, String cvc, String month, String year) {
        page.locator(NAME_ON_CARD).fill(name);
        page.locator(CARD_NUMBER).fill(card);
        page.locator(CVC).fill(cvc);
        page.locator(EXP_MONTH).fill(month);
        page.locator(EXP_YEAR).fill(year);
    }

    public void clickPayAndConfirm() {
        page.locator(PAY_BUTTON).click();
    }

    public void verifyOrderSuccess() {

        assertThat(page.locator(SUCCESS_MESSAGE)).isVisible();
    }
}