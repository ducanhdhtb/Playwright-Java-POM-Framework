package cucumber.steps;

import cucumber.context.ScenarioContext;
import io.cucumber.java.en.*;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Common / shared steps used across multiple feature files.
 * Navigation, page title, URL assertions, header checks.
 */
public class CommonSteps {

    private final ScenarioContext ctx;

    public CommonSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Given("the user navigates to {string}")
    public void navigateTo(String url) {
        ctx.homePage.navigate(url);
    }

    @Given("the home page is displayed with title {string}")
    public void verifyHomePageTitle(String title) {
        assertThat(ctx.page).hasTitle(title);
    }

    // ── Header / Login state ──────────────────────────────────────────────────

    @Then("the header shows {string}")
    public void verifyHeaderContains(String text) {
        assertThat(ctx.page.locator("#header")).containsText(text);
    }

    @Then("the header does not contain {string}")
    public void verifyHeaderNotContains(String text) {
        assertThat(ctx.page.locator("#header")).not().containsText(text);
    }

    // ── Page title / URL ──────────────────────────────────────────────────────

    @Then("the page title is {string}")
    public void verifyPageTitle(String title) {
        assertThat(ctx.page).hasTitle(title);
    }

    @Then("the user is redirected to {string}")
    public void verifyRedirectTo(String url) {
        assertThat(ctx.page).hasURL(url);
    }

    @Then("the current URL is {string}")
    public void verifyCurrentUrl(String url) {
        assertThat(ctx.page).hasURL(url);
    }

    @Then("the current URL matches pattern {string}")
    public void verifyUrlMatchesPattern(String pattern) {
        assertThat(ctx.page).hasURL(java.util.regex.Pattern.compile(pattern));
    }

    // ── Generic message / heading assertions ──────────────────────────────────

    @Then("the message {string} is visible")
    public void verifyMessageVisible(String message) {
        assertThat(ctx.page.getByText(message)).isVisible();
    }

    @Then("the error message {string} is visible")
    public void verifyErrorMessageVisible(String message) {
        assertThat(ctx.page.getByText(message)).isVisible();
    }

    @Then("the heading {string} is visible")
    public void verifyHeadingVisible(String heading) {
        assertThat(ctx.page.getByRole(
                com.microsoft.playwright.options.AriaRole.HEADING,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName(heading)
        )).isVisible();
    }

    // ── Common clicks ─────────────────────────────────────────────────────────

    @When("the user clicks {string}")
    public void clickByText(String label) {
        switch (label) {
            case "Signup / Login" -> ctx.homePage.clickSignupLogin();
            case "Logout"         -> ctx.homePage.clickLogout();
            case "Delete Account" -> ctx.homePage.deleteAccount();
            case "Contact Us"     -> ctx.homePage.clickContactUs();
            case "Test Cases"     -> ctx.homePage.clickTestCases();
            case "Products"       -> ctx.homePage.clickProducts();
            case "Cart"           -> ctx.homePage.clickCart();
            case "Continue Shopping" -> ctx.productsPage.clickContinueShopping();
            case "View Cart"      -> ctx.productsPage.clickViewCart();
            case "Continue"       -> ctx.page.click("a[data-qa='continue-button']");
            case "Place Order"    -> ctx.checkoutPage.clickPlaceOrder();
            case "Pay and Confirm Order" -> ctx.checkoutPage.clickPayAndConfirm();
            default -> ctx.page.getByText(label).first().click();
        }
    }

    @When("the user clicks on {string}")
    public void clickOn(String label) {
        clickByText(label);
    }
}
