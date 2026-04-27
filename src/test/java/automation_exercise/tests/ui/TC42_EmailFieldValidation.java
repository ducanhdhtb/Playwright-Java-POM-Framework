package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.ExcelReader;

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
        // Data moved to Excel: sheet name should be 'invalidEmailsForLogin'
        return ExcelReader.getTestData("src/test/resources/AutomationTestData.xlsx", "invalidEmailsForLogin");
    }

    @DataProvider(name = "validSpecialEmails")
    public static Object[][] validSpecialEmails() {
        // Data moved to Excel: sheet name should be 'validSpecialEmails'
        return ExcelReader.getTestData("src/test/resources/AutomationTestData.xlsx", "validSpecialEmails");
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
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickSignupLogin();

        signupLoginPage.get().fillLoginForm(email, "Password123");
        signupLoginPage.get().clickLoginButton();

        // Should remain on login page — HTML5 validation blocks invalid email
        assertThat(getPage()).hasURL(
                java.util.regex.Pattern.compile(".*(login|signup).*"));
        // Should NOT show "logged in as"
        assertThat(getPage().locator("#header")).not().containsText("Logged in as");
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
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().scrollToFooter();
        homePage.get().subscribe(email);
        homePage.get().verifySuccessMessage();
    }

    @Test(
            description = "TC42c: XSS payload in email field is sanitized",
            priority = 42,
            groups = {"regression", "negative"}
    )
    @Description("Error Guessing: XSS in email field should not execute script")
    @Step("TC42c: XSS payload in email field")
    public void testXssInEmailField() {
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickSignupLogin();

        // Try XSS in email field
        signupLoginPage.get().fillLoginForm("<script>alert('xss')</script>@test.com", "Password123");
        signupLoginPage.get().clickLoginButton();

        // Page should not execute script (dialog handler in BaseTest catches it)
        // Should show error or stay on login page
        assertThat(getPage()).hasURL(
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

        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickSignupLogin();

        signupLoginPage.get().fillLoginForm(longEmail, "Password123");
        signupLoginPage.get().clickLoginButton();

        // Should show error (user not found) or stay on login — not crash
        assertThat(getPage()).hasURL(
                java.util.regex.Pattern.compile(".*(login|signup).*"));
    }
}
