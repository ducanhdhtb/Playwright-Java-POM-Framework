package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC2_LoginUserWithCorrectCredentials extends BaseTest {

    @Test(
            priority = 2,
            dataProvider = "tc2DataProvider",
            dataProviderClass = TestData.class,
            groups = {"smoke"}
    )
    @Step("TC2: Login with valid credentials")
    public void testLoginUserWithCorrectCredentials(String email, String password, String expectedUsername) {
        // 1. Launch browser & 2. Navigate to url
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));

        // 3. Create a fresh account so the login step has valid credentials
        String loginEmail = createLoggedInUser(expectedUsername, password);

        // 4. Log out before testing the login form itself
        homePage.get().clickLogout();

        // 5. Verify that home page is visible successfully
        assertThat(getPage()).hasTitle("Automation Exercise - Signup / Login");

        // 6. Click on 'Signup / Login' button
        homePage.get().clickSignupLogin();

        // 7. Verify 'Login to your account' is visible
        assertThat(getPage().getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Login to your account"))).isVisible();

        // 8. Enter correct email address and password
        signupLoginPage.get().fillLoginForm(loginEmail, password);

        // 9. Click 'login' button
        signupLoginPage.get().clickLoginButton();

        // 10. Verify that 'Logged in as username' is visible
        assertThat(getPage().locator("#header")).containsText("Logged in as " + expectedUsername);

        // 11. Click 'Delete Account' button
        homePage.get().deleteAccount();

        // 12. Verify that 'ACCOUNT DELETED!' is visible
        assertThat(getPage().getByText("ACCOUNT DELETED!")).isVisible();
        getPage().getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
    }
}
