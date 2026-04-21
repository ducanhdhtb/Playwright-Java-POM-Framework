package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC5_RegisterExistingEmailTest extends BaseTest {

    @Test(
            priority = 5,
            dataProvider = "existingEmailRegistrationDataProvider",
            dataProviderClass = TestData.class
    )
    @Step("TC5: Register with an existing email")
    public void testRegisterWithExistingEmail(String name, String email, String expectedError) {
        // 1 & 2. Khởi tạo và điều hướng (Xử lý bởi BaseTest)
        homePage.navigate(ConfigReader.getProperty("baseUrl"));

        // Ensure the email is actually "existing" by creating a fresh user first.
        String existingEmail = createLoggedInUser(name, ConfigReader.getProperty("test.defaultPassword", "Password123"));
        homePage.clickLogout();

        // After logout the site lands on the Signup/Login page; go back to home for this TC's flow.
        homePage.navigate(ConfigReader.getProperty("baseUrl"));

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click on 'Signup / Login' button
        homePage.clickSignupLogin();

        // 5. Verify 'New User Signup!' is visible
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("New User Signup!"))).isVisible();

        // 6. Enter name and already registered email address
        signupLoginPage.fillSignupForm(name, existingEmail);

        // 7. Click 'Signup' button
        signupLoginPage.clickSignupButton();

        // 8. Verify the expected error is visible for existing email.
        assertThat(page.getByText(expectedError, new Page.GetByTextOptions().setExact(true))).isVisible();
    }
}
