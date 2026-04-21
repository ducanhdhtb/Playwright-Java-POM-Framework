package cucumber.steps;

import cucumber.context.ScenarioContext;
import io.cucumber.java.en.*;

/**
 * Step definitions for subscription.feature
 */
public class SubscriptionSteps {

    private final ScenarioContext ctx;

    public SubscriptionSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @When("the user scrolls down to the footer")
    public void scrollToFooter() {
        ctx.homePage.scrollToFooter();
    }

    @When("the user scrolls down to the footer on cart page")
    public void scrollToFooterOnCart() {
        ctx.cartPage.scrollToFooter();
    }

    @Then("the {string} section title is visible")
    public void verifySubscriptionTitle(String title) {
        if ("SUBSCRIPTION".equals(title)) {
            ctx.homePage.verifySubscriptionTitleIsVisible();
        }
    }

    @When("the user enters email {string} in the subscription field and clicks subscribe")
    public void subscribe(String email) {
        ctx.homePage.subscribe(email);
    }

    @Then("the subscription success message {string} is visible")
    public void verifySubscriptionSuccess(String message) {
        ctx.homePage.verifySuccessMessage();
    }
}
