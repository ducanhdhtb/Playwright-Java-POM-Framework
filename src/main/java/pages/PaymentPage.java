package pages;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class PaymentPage extends BasePage {

    public PaymentPage(Page page) {
        super(page);
    }

    @Step("Filling payment details for: {0}")
    public void fillPaymentDetails(String name, String cardNum, String cvc, String month, String year) {
        locator("input[data-qa='name-on-card']").fill(name);
        locator("input[data-qa='card-number']").fill(cardNum);
        locator("input[data-qa='cvc']").fill(cvc);
        locator("input[data-qa='expiry-month']").fill(month);
        locator("input[data-qa='expiry-year']").fill(year);
    }

    @Step("Clicking 'Pay and Confirm Order' button")
    public void clickPayAndConfirm() {
        locator("button[data-qa='pay-button']").click();
    }
}
