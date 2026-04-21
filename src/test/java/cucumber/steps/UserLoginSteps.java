package cucumber.steps;

import cucumber.context.ScenarioContext;
import io.cucumber.java.en.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Step definitions for user_login.feature
 */
public class UserLoginSteps {

    private final ScenarioContext ctx;

    public UserLoginSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ── Login form ────────────────────────────────────────────────────────────

    @When("the user fills the login form with email {string} and password {string}")
    public void fillLoginForm(String email, String password) {
        ctx.signupLoginPage.fillLoginForm(email, password);
    }

    @When("the user fills the login form with the API-created user credentials")
    public void fillLoginFormWithApiUser() {
        ctx.signupLoginPage.fillLoginForm(ctx.apiCreatedEmail, ctx.apiCreatedPassword);
    }

    @When("the user fills the login form with the correct email and wrong password {string}")
    public void fillLoginFormWithWrongPassword(String wrongPassword) {
        ctx.signupLoginPage.fillLoginForm(ctx.apiCreatedEmail, wrongPassword);
    }

    @When("the user fills the login form with the deleted user credentials")
    public void fillLoginFormWithDeletedUser() {
        ctx.signupLoginPage.fillLoginForm(ctx.apiCreatedEmail, ctx.apiCreatedPassword);
    }

    @When("the user logs in with the API-created user credentials")
    public void loginWithApiUser() {
        ctx.signupLoginPage.fillLoginForm(ctx.apiCreatedEmail, ctx.apiCreatedPassword);
        ctx.signupLoginPage.clickLoginButton();
    }

    @When("the user logs in with email from API-created user and password {string}")
    public void loginWithApiUserEmail(String password) {
        ctx.signupLoginPage.fillLoginForm(ctx.apiCreatedEmail, password);
        ctx.signupLoginPage.clickLoginButton();
    }

    @When("the user clicks the Login button")
    public void clickLoginButton() {
        ctx.signupLoginPage.clickLoginButton();
    }

    // ── API user setup ────────────────────────────────────────────────────────

    @Given("a user account exists with username {string} and password {string}")
    public void createUserViaUi(String username, String password) {
        // Use API for speed — creates user and stores credentials in context
        ctx.apiCreatedPassword = password;
        ctx.apiCreatedEmail = ctx.userApi.setupUser(username, password);
    }

    @Given("a user {string} is created via API with password {string}")
    public void createUserViaApi(String name, String password) {
        ctx.apiCreatedPassword = password;
        ctx.apiCreatedEmail = ctx.userApi.setupUser(name, password);
    }

    @Given("a user is logged in with username {string} and password {string}")
    public void loginUser(String username, String password) {
        ctx.apiCreatedPassword = password;
        ctx.apiCreatedEmail = ctx.userApi.setupUser(username, password);
        ctx.homePage.clickSignupLogin();
        ctx.signupLoginPage.fillLoginForm(ctx.apiCreatedEmail, password);
        ctx.signupLoginPage.clickLoginButton();
    }

    @And("the user account is deleted via API")
    public void deleteUserViaApi() {
        ctx.userApi.teardownUser(ctx.apiCreatedEmail, ctx.apiCreatedPassword);
    }
}
