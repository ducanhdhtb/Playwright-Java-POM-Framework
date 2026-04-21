package cucumber.steps;

import api.ApiResponse;
import cucumber.context.ScenarioContext;
import io.cucumber.java.en.*;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Step definitions for API scenarios in:
 *   - user_registration.feature (API-TC01)
 *   - user_login.feature        (API-TC02)
 *   - user_account.feature      (API-TC03, API-TC04)
 */
public class ApiSteps {

    private final ScenarioContext ctx;

    /** Holds the last API response for assertion steps. */
    private ApiResponse lastResponse;

    public ApiSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    @Given("a user account already exists via API with email {string} and password {string}")
    public void createUserViaApiWithEmail(String email, String password) {
        ctx.apiCreatedEmail = email;
        ctx.apiCreatedPassword = password;
        ApiResponse resp = ctx.userApi.createUser("ApiUser", email, password);
        // 201 = created, 400 = already exists — both are acceptable for "already exists" setup
        assertTrue(resp.responseCode() == 201 || resp.responseCode() == 400,
                "Expected 201 or 400 but got: " + resp.responseCode());
    }

    @Given("a user account exists via API with email {string} and password {string}")
    public void ensureUserExistsViaApi(String email, String password) {
        ctx.apiCreatedEmail = email;
        ctx.apiCreatedPassword = password;
        ApiResponse resp = ctx.userApi.createUser("ApiUser", email, password);
        assertEquals(resp.responseCode(), 201,
                "User creation failed: " + resp.text());
    }

    @Given("a user {string} is created via API with email {string} and password {string}")
    public void createNamedUserViaApi(String name, String email, String password) {
        ctx.apiCreatedEmail = email;
        ctx.apiCreatedPassword = password;
        ApiResponse resp = ctx.userApi.createUser(name, email, password);
        assertEquals(resp.responseCode(), 201,
                "User creation failed: " + resp.text());
    }

    // ── POST /api/createAccount ───────────────────────────────────────────────

    @When("a POST request is sent to {string} with valid user data")
    public void postCreateAccountWithValidData(String path) {
        String email = "api_" + System.currentTimeMillis() + "@testmail.com";
        ctx.apiCreatedEmail = email;
        ctx.apiCreatedPassword = "Password123";
        lastResponse = ctx.userApi.createUser("ApiTestUser", email, "Password123");
    }

    @When("a POST request is sent to {string} with the same email {string}")
    public void postCreateAccountWithDuplicateEmail(String path, String email) {
        lastResponse = ctx.userApi.createUser("DupUser2", email, "Password123");
    }

    // ── POST /api/verifyLogin ─────────────────────────────────────────────────

    @When("a POST request is sent to {string} with email {string} and password {string}")
    public void postVerifyLogin(String path, String email, String password) {
        lastResponse = ctx.userApi.verifyLogin(email, password);
    }

    @When("a POST request is sent to {string} with only password {string}")
    public void postVerifyLoginMissingEmail(String path, String password) {
        // Omit email to trigger 400 missing-parameter error
        lastResponse = new ApiResponse(ctx.apiClient.postForm(path,
                Map.of("password", password)));
    }

    // ── GET /api/getUserDetailByEmail ─────────────────────────────────────────

    @When("a GET request is sent to {string} with email {string}")
    public void getByEmail(String path, String email) {
        lastResponse = ctx.userApi.getUserByEmail(email);
    }

    @When("a GET request is sent to {string} with the created user email")
    public void getByCreatedUserEmail(String path) {
        lastResponse = ctx.userApi.getUserByEmail(ctx.apiCreatedEmail);
    }

    @When("a GET request is sent to {string} with the deleted user email")
    public void getByDeletedUserEmail(String path) {
        lastResponse = ctx.userApi.getUserByEmail(ctx.apiCreatedEmail);
    }

    // ── DELETE /api/deleteAccount ─────────────────────────────────────────────

    @When("a DELETE request is sent to {string} with the created user credentials")
    public void deleteCreatedUser(String path) {
        lastResponse = ctx.userApi.deleteUser(ctx.apiCreatedEmail, ctx.apiCreatedPassword);
    }

    @When("a DELETE request is sent to {string} with email {string} and password {string}")
    public void deleteUserByEmail(String path, String email, String password) {
        lastResponse = ctx.userApi.deleteUser(email, password);
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("the HTTP status code is {int}")
    public void verifyHttpStatus(int expectedStatus) {
        int actual = lastResponse.status();
        if (actual == 503 || actual == 429 || actual == 502) {
            throw new org.testng.SkipException(
                    "Skipped: server returned HTTP " + actual + " (overloaded). Retry later.");
        }
        assertEquals(actual, expectedStatus,
                "HTTP status mismatch. Body: " + lastResponse.text());
    }

    @Then("the API responseCode is {int}")
    public void verifyApiResponseCode(int expectedCode) {
        assertEquals(lastResponse.responseCode(), expectedCode,
                "API responseCode mismatch. Body: " + lastResponse.text());
    }

    @Then("the API message contains {string}")
    public void verifyApiMessageContains(String expected) {
        assertTrue(lastResponse.message().toLowerCase().contains(expected.toLowerCase()),
                "Expected message to contain '" + expected + "' but got: " + lastResponse.message());
    }

    @Then("the response contains a {string} object")
    public void verifyResponseContainsObject(String key) {
        assertFalse(lastResponse.json().path(key).isMissingNode(),
                "Response should contain '" + key + "' object. Body: " + lastResponse.text());
    }

    @Then("the response user email matches the created user email")
    public void verifyResponseUserEmailMatchesCreated() {
        String actual = lastResponse.json().path("user").path("email").asText();
        assertEquals(actual, ctx.apiCreatedEmail,
                "Response email should match created user email");
    }

    @Then("the response user email is {string}")
    public void verifyResponseUserEmail(String expectedEmail) {
        String actual = lastResponse.json().path("user").path("email").asText();
        assertEquals(actual, expectedEmail, "Response email mismatch");
    }

    @Then("the response user name is {string}")
    public void verifyResponseUserName(String expectedName) {
        String actual = lastResponse.json().path("user").path("name").asText();
        assertEquals(actual, expectedName, "Response name mismatch");
    }
}
