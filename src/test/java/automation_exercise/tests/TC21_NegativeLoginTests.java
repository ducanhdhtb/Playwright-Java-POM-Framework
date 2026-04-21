package automation_exercise.tests;

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
        String email = userApi.setupUser(name, correctPassword);

        try {
            homePage.navigate(ConfigReader.getProperty("baseUrl"));
            homePage.clickSignupLogin();
            signupLoginPage.fillLoginForm(email, wrongPassword);
            signupLoginPage.clickLoginButton();

            // Verify error message is shown
            assertThat(page.getByText("Your email or password is incorrect!"))
                    .isVisible();

            // Verify user is NOT logged in
            assertThat(page.locator("#header")).not().containsText("Logged in as");
        } finally {
            userApi.teardownUser(email, correctPassword);
        }
    }

    @Test(
            description = "TC21b: Login with non-existent email shows error",
            groups = {"regression", "negative"}
    )
    @Description("Attempts login with an email that was never registered")
    @Step("TC21b: Non-existent email shows error message")
    public void testLoginWithNonExistentEmail() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickSignupLogin();
        signupLoginPage.fillLoginForm(
                "ghost_" + System.currentTimeMillis() + "@nowhere.com",
                "Password123"
        );
        signupLoginPage.clickLoginButton();

        assertThat(page.getByText("Your email or password is incorrect!"))
                .isVisible();
    }

    @Test(
            description = "TC21c: Login with empty credentials shows error",
            groups = {"regression", "negative"}
    )
    @Description("Submits login form with empty email and password fields")
    @Step("TC21c: Empty credentials shows error or stays on login page")
    public void testLoginWithEmptyCredentials() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickSignupLogin();
        signupLoginPage.fillLoginForm("", "");
        signupLoginPage.clickLoginButton();

        // Should remain on login page (not navigate away)
        assertThat(page).hasURL(
                java.util.regex.Pattern.compile(".*(login|signup).*",
                        java.util.regex.Pattern.CASE_INSENSITIVE));
    }
}
