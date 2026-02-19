package automation_exercise.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.Page.GetByRoleOptions; // Import trực tiếp option

public class PaymentPage {
    private final Page page;

    public PaymentPage(Page page) {
        this.page = page;
    }

    public void fillPaymentDetails(String name, String cardNum, String cvc, String month, String year) {
        page.locator("input[name='name_on_card']").fill(name);
        page.locator("input[name='card_number']").fill(cardNum);
        page.locator("input[name='cvc']").fill(cvc);
        page.locator("input[name='expiry_month']").fill(month);
        page.locator("input[name='expiry_year']").fill(year);
    }

    public void clickPayAndConfirm() {
        page.getByRole(AriaRole.BUTTON, new GetByRoleOptions().setName("Pay and Confirm Order")).click();
    }
}