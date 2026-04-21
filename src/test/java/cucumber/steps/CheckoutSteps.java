package cucumber.steps;

import cucumber.context.ScenarioContext;
import io.cucumber.java.en.*;

/**
 * Step definitions for checkout_and_order.feature
 */
public class CheckoutSteps {

    private final ScenarioContext ctx;

    public CheckoutSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @When("the user enters comment {string}")
    public void enterComment(String comment) {
        ctx.checkoutPage.enterComment(comment);
    }

    @When("the user enters payment details with name {string}, card {string}, cvc {string}, month {string}, year {string}")
    public void enterPaymentDetails(String name, String card, String cvc, String month, String year) {
        ctx.checkoutPage.enterPaymentDetails(name, card, cvc, month, year);
    }

    @Then("the order success message is visible")
    public void verifyOrderSuccess() {
        ctx.checkoutPage.verifyOrderSuccess();
    }
}
