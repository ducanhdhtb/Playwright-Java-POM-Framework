package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC2_LoginUserWithCorrectCredentials extends BaseTest {

    @Test(priority = 2)
    public void testLoginUserWithCorrectCredentials() {
        // 1. Launch browser & 2. Navigate to url (Đã xử lý trong BaseTest và navigate)
        homePage.navigate();

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click on 'Signup / Login' button
        homePage.clickSignupLogin();

        // 5. Verify 'Login to your account' is visible
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Login to your account"))).isVisible();

        // 6. Enter correct email address and password
        signupLoginPage.fillLoginForm("ducanhdhtb@gmail.com", "ducanh123");

        // 7. Click 'login' button
        signupLoginPage.clickLoginButton();

        // 8. Verify that 'Logged in as username' is visible
        assertThat(page.locator("#header")).containsText("Logged in as Nguyễn Đức Anh");

        // 9. Click 'Delete Account' button
        homePage.deleteAccount();

        // 10. Verify that 'ACCOUNT DELETED!' is visible
        assertThat(page.getByText("ACCOUNT DELETED!")).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
    }
}