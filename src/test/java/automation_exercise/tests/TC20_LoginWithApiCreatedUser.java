package automation_exercise.tests;

import automation_exercise.BaseTest;
import api.UserApiHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC20: Login via UI using a user created through the API.
 *
 * Demonstrates the API-backed setup pattern:
 *   - User is created via API (fast, no UI overhead)
 *   - Login flow is tested via UI
 *   - User is deleted via API (fast teardown)
 */
public class TC20_LoginWithApiCreatedUser extends BaseTest {

    @Test(
            description = "TC20: Login via UI with API-created user",
            groups = {"smoke", "api-ui"}
    )
    @Description("Creates a user via API, logs in via UI, verifies session, then deletes via API")
    @Step("TC20: API-backed login test")
    public void testLoginWithApiCreatedUser() {
        String name = "ApiUiUser";
        String password = UserApiHelper.defaultPassword();

        // 1. Create user via API — no UI signup flow needed
        String email = userApi.setupUser(name, password);

        try {
            // 2. Navigate and login via UI
            homePage.navigate(ConfigReader.getProperty("baseUrl"));
            homePage.clickSignupLogin();
            signupLoginPage.fillLoginForm(email, password);
            signupLoginPage.clickLoginButton();

            // 3. Verify logged in
            homePage.verifyLoggedInAs(name);

            // 4. Verify header shows correct username
            assertThat(page.locator("#header")).containsText("Logged in as " + name);

        } finally {
            // 5. Teardown via API — no UI delete flow needed
            userApi.teardownUser(email, password);
        }
    }
}
