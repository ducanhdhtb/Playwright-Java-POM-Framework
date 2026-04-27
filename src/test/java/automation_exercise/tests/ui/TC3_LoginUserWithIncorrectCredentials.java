package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC3_LoginUserWithIncorrectCredentials extends BaseTest {

    @Test(
            priority = 3,
            dataProvider = "tc3DataProvider",
            dataProviderClass = TestData.class,
            groups = {"smoke"}
    )
    @Step("TC3: Login with invalid credentials")
    public void testLoginWithIncorrectCredentials(String email, String password, String expectedError) {
        // 1 & 2. Launch & Navigate
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));

        // 3. Verify home page is visible
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Click on 'Signup / Login' button
        homePage.get().clickSignupLogin();

        // 5. Verify 'Login to your account' is visible
        assertThat(getPage().getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Login to your account"))).isVisible();

        // 6. Enter incorrect email and password from the data provider
        signupLoginPage.get().fillLoginForm(email, password);

        // 7. Click 'login' button
        signupLoginPage.get().clickLoginButton();

        // 8. Verify the expected error message is visible
        assertThat(getPage().getByText(expectedError, new Page.GetByTextOptions().setExact(true))).isVisible();
    }
}
