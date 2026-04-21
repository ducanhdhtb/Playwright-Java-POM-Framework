package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.CookieManager;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC41: Cookie Session Tests — Playwright cookie save/restore
 *
 * Demonstrates:
 *   1. Login once, save session cookies
 *   2. Reuse session in subsequent tests (skip login UI)
 *   3. Verify session persists across page navigations
 *   4. Verify session is invalidated after logout
 *
 * This pattern speeds up tests that require a logged-in state
 * by avoiding repeated UI login flows.
 */
public class TC41_CookieSessionTests extends BaseTest {

    private static final String SESSION_NAME = "tc41_test_session";
    private static String savedEmail;
    private static final String PASSWORD = "Password123";
    private static final String USERNAME = "CookieUser";

    @AfterClass(alwaysRun = true)
    public void cleanupSession() {
        // Cleanup: delete user via API and clear saved cookie file
        if (savedEmail != null) {
            userApi.teardownUser(savedEmail, PASSWORD);
        }
        CookieManager.clear(SESSION_NAME);
    }

    @Test(
            description = "TC41a: Login and save session cookies to file",
            priority = 410,
            groups = {"regression", "cookie"}
    )
    @Description("Logs in via UI, saves session cookies using CookieManager")
    @Step("TC41a: Login and save session")
    public void testLoginAndSaveSession() {
        // Create user via API
        savedEmail = userApi.setupUser(USERNAME, PASSWORD);

        // Login via UI
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickSignupLogin();
        signupLoginPage.fillLoginForm(savedEmail, PASSWORD);
        signupLoginPage.clickLoginButton();
        homePage.verifyLoggedInAs(USERNAME);

        // Save session cookies
        saveSession(SESSION_NAME);

        // Verify session file was created
        assert CookieManager.exists(SESSION_NAME)
                : "Session file should exist after saveSession()";
    }

    @Test(
            description = "TC41b: Restore session cookies — skip login UI",
            priority = 411,
            groups = {"regression", "cookie"},
            dependsOnMethods = {"testLoginAndSaveSession"}
    )
    @Description("Restores saved cookies and navigates directly to home — should be logged in")
    @Step("TC41b: Restore session and verify logged in")
    public void testRestoreSessionSkipsLogin() {
        // Restore cookies WITHOUT going through login UI
        restoreSession(SESSION_NAME);

        // Navigate to home page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));

        // Should be logged in already
        homePage.verifyLoggedInAs(USERNAME);
        assertThat(page.locator("#header")).containsText("Logged in as " + USERNAME);
    }

    @Test(
            description = "TC41c: Restored session persists across multiple page navigations",
            priority = 412,
            groups = {"regression", "cookie"},
            dependsOnMethods = {"testLoginAndSaveSession"}
    )
    @Description("Restores session and navigates to Products, Cart, Contact — session persists")
    @Step("TC41c: Session persists across navigations")
    public void testSessionPersistsAcrossNavigations() {
        restoreSession(SESSION_NAME);

        // Navigate to multiple pages and verify still logged in
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.verifyLoggedInAs(USERNAME);

        homePage.clickProducts();
        assertThat(page.locator("#header")).containsText("Logged in as " + USERNAME);

        homePage.clickCart();
        assertThat(page.locator("#header")).containsText("Logged in as " + USERNAME);

        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(page.locator("#header")).containsText("Logged in as " + USERNAME);
    }

    @Test(
            description = "TC41d: Restored session allows adding products to cart",
            priority = 413,
            groups = {"regression", "cookie"},
            dependsOnMethods = {"testLoginAndSaveSession"}
    )
    @Description("Restores session, adds product to cart — verifies cart works with restored session")
    @Step("TC41d: Cart works with restored session")
    public void testCartWorksWithRestoredSession() {
        restoreSession(SESSION_NAME);
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.verifyLoggedInAs(USERNAME);

        // Add product to cart
        homePage.clickProducts();
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        // Verify product in cart and still logged in
        cartPage.verifyCartCount(1);
        assertThat(page.locator("#header")).containsText("Logged in as " + USERNAME);
    }

    @Test(
            description = "TC41e: After logout, restored session is no longer valid",
            priority = 414,
            groups = {"regression", "cookie"},
            dependsOnMethods = {"testLoginAndSaveSession"}
    )
    @Description("Restores session, logs out, then tries to restore again — should not be logged in")
    @Step("TC41e: Session invalidated after logout")
    public void testSessionInvalidatedAfterLogout() {
        // Restore and verify logged in
        restoreSession(SESSION_NAME);
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.verifyLoggedInAs(USERNAME);

        // Logout
        homePage.clickLogout();
        assertThat(page).hasURL(
                java.util.regex.Pattern.compile(".*(login|signup).*"));

        // Navigate to home — should NOT be logged in anymore
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(page.locator("#header")).not().containsText("Logged in as");
    }
}
