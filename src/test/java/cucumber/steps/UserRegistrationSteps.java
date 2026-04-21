package cucumber.steps;

import cucumber.context.ScenarioContext;
import io.cucumber.java.en.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Step definitions for user_registration.feature
 */
public class UserRegistrationSteps {

    private final ScenarioContext ctx;

    // Holds the existing email created in TC5 setup
    private String existingEmail;

    public UserRegistrationSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ── Signup form ───────────────────────────────────────────────────────────

    @When("the user fills the signup form with name {string} and a random email")
    public void fillSignupFormWithRandomEmail(String name) {
        String email = "user_" + System.currentTimeMillis() + "@example.com";
        ctx.signupLoginPage.fillSignupForm(name, email);
    }

    @When("the user fills the signup form with name {string} and email {string}")
    public void fillSignupFormWithEmail(String name, String email) {
        ctx.signupLoginPage.fillSignupForm(name, email);
    }

    @When("the user fills the signup form with name {string} and the existing email")
    public void fillSignupFormWithExistingEmail(String name) {
        ctx.signupLoginPage.fillSignupForm(name, existingEmail);
    }

    @When("the user clicks the Signup button")
    public void clickSignupButton() {
        ctx.signupLoginPage.clickSignupButton();
    }

    // ── Account details ───────────────────────────────────────────────────────

    @When("the user fills account details with password {string}, day {string}, month {string}, year {string}")
    public void fillAccountDetails(String password, String day, String month, String year) {
        ctx.accountPage.fillAccountDetails(password, day, month, year);
    }

    // ── Address details ───────────────────────────────────────────────────────

    @When("the user fills address details with firstName {string}, lastName {string}, company {string}, address {string}, country {string}, state {string}, city {string}, zipcode {string}, mobile {string}")
    public void fillAddressDetailsFull(String firstName, String lastName, String company,
                                       String address, String country, String state,
                                       String city, String zipcode, String mobile) {
        ctx.accountPage.fillAddressDetails(firstName, lastName, company, address,
                country, state, city, zipcode, mobile);
    }

    @When("the user fills address details with firstName {string}, lastName {string}, address {string}, state {string}, city {string}, zipcode {string}, mobile {string}")
    public void fillAddressDetailsNoCompanyCountry(String firstName, String lastName,
                                                    String address, String state,
                                                    String city, String zipcode, String mobile) {
        ctx.accountPage.fillAddressDetails(firstName, lastName, "", address,
                "United States", state, city, zipcode, mobile);
    }

    @When("the user clicks {string} button")
    public void clickButton(String buttonLabel) {
        if ("Create Account".equals(buttonLabel)) {
            ctx.accountPage.clickCreateAccount();
        } else {
            ctx.page.getByText(buttonLabel).first().click();
        }
    }

    @When("the user clicks \"Create Account\"")
    public void clickCreateAccount() {
        ctx.accountPage.clickCreateAccount();
    }

    // ── TC5 setup: create existing user ──────────────────────────────────────

    @Given("a user account already exists with name {string} and password {string}")
    public void createExistingUser(String name, String password) {
        // Create via UI helper in BaseTest style — use API for speed
        existingEmail = ctx.userApi.setupUser(name, password);
    }

    @Given("the user is logged out")
    public void logOut() {
        ctx.homePage.clickLogout();
    }

    @When("the user logs out")
    public void userLogsOut() {
        ctx.homePage.clickLogout();
    }
}
