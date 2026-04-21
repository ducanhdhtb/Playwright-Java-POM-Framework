package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC42: Email field — Equivalence Partitioning + Error Guessing
 *
 * Applies to: Login form, Signup form, Subscription form
 *
 * Partitions:
 *   EP1 — Valid email: user@domain.com
 *   EP2 — Missing @: userdomain.com
 *   EP3 — Missing domain: user@
 *   EP4 — Missing local: @domain.com
 *   EP5 — Spaces in email: "user @domain.com"
 *   EP6 — Special chars: user+tag@domain.co.uk (valid)
 *   EP7 — Very long email (BVA)
 *   EP8 — XSS in email field (Error Guessing)
 */
public class TC42_EmailFieldValidation extends BaseTest {

    @DataProvider(name = "invalidEmailsForLogin")
    public static Object[][] invalidEmailsForLogin() {
        return new Object[][]{
                {"userdomain.com",       "missing @"},
                {"user@",                "missing domain"},
                {"@domain.com",          "missing local part"},
                {"user @domain.com",     "space in email"},
                {"user@@domain.com",     "double @"},
                {"plaintext",            "no @ or domain"},
        };
    }

    @DataProvider(name = "validSpecialEmails")
    public static Object[][] validSpecialEmails() {
        return new Object[][]{
                {"user+tag@domain.com"},          // plus addressing
                {"user.name@domain.co.uk"},       // subdomain + dot
                {"user123@test-domain.com"},      // hyphen in domain
        };
    }

    @Test(
            description = "TC42a: Invalid email formats in login form are blocked by HTML5 validation",
            dataProvider = "invalidEmailsForLogin",
            priority = 42,
            groups = {"regression", "boundary", "negative"}
    )
    @Description("EP: Invalid email formats should be blocked by browser HTML5 email validation")
    @Step("TC42a: Invalid email '{0}' ({1}) blocked in login form")
    public void testInvalidEmailInLoginForm(String email, String description) {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickSignupLogin();

        signupLoginPage.fillLoginForm(email, "Password123");
        signupLoginPage.clickLoginButton();

        // Should remain on login page — HTML5 validation blocks invalid email
        assertThat(page).hasURL(
                java.util.regex.Pattern.compile(".*(login|signup).*"));
        // Should NOT show "logged in as"
        assertThat(page.locator("#header")).not().containsText("Logged in as");
    }

    @Test(
            description = "TC42b: Valid special email formats are accepted in subscription",
            dataProvider = "validSpecialEmails",
            priority = 42,
            groups = {"regression", "boundary"}
    )
    @Description("EP: Valid special email formats (plus addressing, subdomains) should be accepted")
    @Step("TC42b: Valid special email '{0}' accepted in subscription")
    public void testValidSpecialEmailInSubscription(String email) {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.scrollToFooter();
        homePage.subscribe(email);
        homePage.verifySuccessMessage();
    }

    @Test(
            description = "TC42c: XSS payload in email field is sanitized",
            priority = 42,
            groups = {"regression", "negative"}
    )
    @Description("Error Guessing: XSS in email field should not execute script")
    @Step("TC42c: XSS payload in email field")
    public void testXssInEmailField() {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickSignupLogin();

        // Try XSS in email field
        signupLoginPage.fillLoginForm("<script>alert('xss')</script>@test.com", "Password123");
        signupLoginPage.clickLoginButton();

        // Page should not execute script (dialog handler in BaseTest catches it)
        // Should show error or stay on login page
        assertThat(page).hasURL(
                java.util.regex.Pattern.compile(".*(login|signup).*"));
    }

    @Test(
            description = "TC42d: Very long email (BVA) does not crash the page",
            priority = 42,
            groups = {"regression", "boundary"}
    )
    @Description("BVA: Very long email string should be handled gracefully")
    @Step("TC42d: Very long email in login form")
    public void testVeryLongEmailInLoginForm() {
        // Generate 200-char email
        String longLocal = "a".repeat(190);
        String longEmail = longLocal + "@test.com";

        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickSignupLogin();

        signupLoginPage.fillLoginForm(longEmail, "Password123");
        signupLoginPage.clickLoginButton();

        // Should show error (user not found) or stay on login — not crash
        assertThat(page).hasURL(
                java.util.regex.Pattern.compile(".*(login|signup).*"));
    }
}
