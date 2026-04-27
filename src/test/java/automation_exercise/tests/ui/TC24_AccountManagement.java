package automation_exercise.tests.ui;

import api.ApiResponse;
import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * TC24: Account management scenarios.
 *
 * Covers:
 *   a) Logout redirects to login page
 *   b) Deleted account cannot login (API delete → UI verify)
 *   c) User details returned by API match registration data
 */
public class TC24_AccountManagement extends BaseTest {

    @Test(
            description = "TC24a: Logout redirects to Signup/Login page",
            groups = {"smoke", "regression"}
    )
    @Description("Logs in via UI, clicks logout, verifies redirect to login page")
    @Step("TC24a: Logout redirects to login page")
    public void testLogoutRedirectsToLoginPage() {
        String name = "LogoutUser";
        String password = "Password123";
        String email = userApi.get().setupUser(name, password);

        try {
            homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
            homePage.get().clickSignupLogin();
            signupLoginPage.get().fillLoginForm(email, password);
            signupLoginPage.get().clickLoginButton();

            homePage.get().verifyLoggedInAs(name);

            // Logout
            homePage.get().clickLogout();

            // Verify redirected to login page
            assertThat(getPage()).hasTitle("Automation Exercise - Signup / Login");
            assertThat(getPage().locator("#header")).not().containsText("Logged in as");
        } finally {
            userApi.get().teardownUser(email, password);
        }
    }

    @Test(
            description = "TC24b: API-deleted account cannot login via UI",
            groups = {"regression", "api-ui", "negative"}
    )
    @Description("Creates user via API, deletes via API, then verifies UI login fails")
    @Step("TC24b: Deleted account cannot login")
    public void testDeletedAccountCannotLogin() {
        String name = "DeletedUser";
        String password = "Password123";
        String email = userApi.get().setupUser(name, password);

        // Delete via API immediately
        ApiResponse deleteResp = userApi.get().deleteUser(email, password);
        assertEquals(deleteResp.responseCode(), 200, "API delete should succeed");

        // Attempt UI login with deleted account
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickSignupLogin();
        signupLoginPage.get().fillLoginForm(email, password);
        signupLoginPage.get().clickLoginButton();

        // Should show error — account no longer exists
        assertThat(getPage().getByText("Your email or password is incorrect!"))
                .isVisible();
    }

    @Test(
            description = "TC24c: API returns correct user details after registration",
            groups = {"regression", "api-ui"}
    )
    @Description("Creates user via API and verifies GET /api/getUserDetailByEmail returns matching data")
    @Step("TC24c: API user details match registration data")
    public void testApiUserDetailsMatchRegistration() {
        String name = "DetailCheck";
        String password = "Password123";
        String email = "api_detail_check_" + System.currentTimeMillis() + "@testmail.com";

        ApiResponse create = userApi.get().createUser(name, email, password);
        assertEquals(create.responseCode(), 201, "User creation should succeed");

        try {
            ApiResponse details = userApi.get().getUserByEmail(email);
            assertEquals(details.responseCode(), 200, "Should fetch user details");

            var user = details.json().path("user");
            assertEquals(user.path("email").asText(), email, "Email should match");
            assertEquals(user.path("name").asText(), name, "Name should match");
        } finally {
            userApi.get().teardownUser(email, password);
        }
    }
}
