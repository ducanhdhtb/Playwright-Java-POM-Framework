package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC21: Negative login scenarios.
 *
 * Covers:
 *   a) Wrong password for existing user
 *   b) Non-existent email
 *   c) Empty credentials
 */
public class TC21_NegativeLoginTests extends BaseTest {

    @Test(
            description = "TC21a: Login with wrong password shows error",
            groups = {"regression", "negative"}
    )
    @Description("Creates a user via API, attempts login with wrong password, verifies error message")
    @Step("TC21a: Wrong password shows error message")
    public void testLoginWithWrongPassword() {
        String name = "NegUser";
        String correctPassword = "Password123";
        String wrongPassword = "WrongPass999";

        // Setup via API
        String email = userApi.get().setupUser(name, correctPassword);

        try {
            homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
            homePage.get().clickSignupLogin();
            signupLoginPage.get().fillLoginForm(email, wrongPassword);
            signupLoginPage.get().clickLoginButton();

            // Verify error message is shown
            assertThat(getPage().getByText("Your email or password is incorrect!"))
                    .isVisible();

            // Verify user is NOT logged in
            assertThat(getPage().locator("#header")).not().containsText("Logged in as");
        } finally {
            userApi.get().teardownUser(email, correctPassword);
        }
    }

    @Test(
            description = "TC21b: Login with non-existent email shows error",
            groups = {"regression", "negative"}
    )
    @Description("Attempts login with an email that was never registered")
    @Step("TC21b: Non-existent email shows error message")
    public void testLoginWithNonExistentEmail() {
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickSignupLogin();
        signupLoginPage.get().fillLoginForm(
                "ghost_" + System.currentTimeMillis() + "@nowhere.com",
                "Password123"
        );
        signupLoginPage.get().clickLoginButton();

        assertThat(getPage().getByText("Your email or password is incorrect!"))
                .isVisible();
    }

    @Test(
            description = "TC21c: Login with empty credentials shows error",
            groups = {"regression", "negative"}
    )
    @Description("Submits login form with empty email and password fields")
    @Step("TC21c: Empty credentials shows error or stays on login page")
    public void testLoginWithEmptyCredentials() {
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickSignupLogin();
        signupLoginPage.get().fillLoginForm("", "");
        signupLoginPage.get().clickLoginButton();

        // Should remain on login page (not navigate away)
        assertThat(getPage()).hasURL(
                java.util.regex.Pattern.compile(".*(login|signup).*",
                        java.util.regex.Pattern.CASE_INSENSITIVE));
    }
}
