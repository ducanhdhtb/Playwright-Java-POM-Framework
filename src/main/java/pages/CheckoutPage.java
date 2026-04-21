package pages;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CheckoutPage extends BasePage {

    // Locators
    private final String COMMENT_AREA = "textarea[name='message']";
    private final String PLACE_ORDER_BTN = "a[href='/payment']";
    private final String NAME_ON_CARD = "input[data-qa='name-on-card']";
    private final String CARD_NUMBER = "input[data-qa='card-number']";
    private final String CVC = "input[data-qa='cvc']";
    private final String EXP_MONTH = "input[data-qa='expiry-month']";
    private final String EXP_YEAR = "input[data-qa='expiry-year']";
    private final String PAY_BUTTON = "button[data-qa='pay-button']";
    private final String SUCCESS_MESSAGE = "//p[normalize-space()='Congratulations! Your order has been confirmed!']";

    public CheckoutPage(Page page) {
        super(page);
    }

    @Step("Entering comment: '{0}'")
    public void enterComment(String comment) {
        locator(COMMENT_AREA).fill(comment);
    }

    @Step("Clicking 'Place Order' button")
    public void clickPlaceOrder() {
        locator(PLACE_ORDER_BTN).click();
        waitForUrl("**/payment");
    }

    @Step("Entering payment details for: {0}")
    public void enterPaymentDetails(String name, String card, String cvc, String month, String year) {
        locator(NAME_ON_CARD).fill(name);
        locator(CARD_NUMBER).fill(card);
        locator(CVC).fill(cvc);
        locator(EXP_MONTH).fill(month);
        locator(EXP_YEAR).fill(year);
    }

    @Step("Clicking 'Pay and Confirm Order' button")
    public void clickPayAndConfirm() {
        locator(PAY_BUTTON).click();
    }

    @Step("Verifying order success message is visible")
    public void verifyOrderSuccess() {
        assertThat(locator(SUCCESS_MESSAGE)).isVisible();
    }

    @Step("Verifying delivery address contains '{0}'")
    public void verifyDeliveryAddress(String expectedText) {
        assertThat(locator("#address_delivery")).containsText(expectedText);
    }

    @Step("Verifying billing address contains '{0}'")
    public void verifyBillingAddress(String expectedText) {
        assertThat(locator("#address_invoice")).containsText(expectedText);
    }

    @Step("Clicking 'Download Invoice' button")
    public void clickDownloadInvoice() {
        locator("a.btn.btn-default.check_out").click();
    }

    @Step("Verifying 'Download Invoice' button is visible")
    public void verifyDownloadInvoiceVisible() {
        assertThat(locator("a.btn.btn-default.check_out")).isVisible();
    }
}
