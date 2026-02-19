package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC4_LogoutTest extends BaseTest {

    @Test(priority = 4)
    public void testLogoutUser() {
        // 1 & 2. Khởi tạo và điều hướng (Xử lý bởi BaseTest)
        homePage.navigate();

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click on 'Signup / Login' button
        homePage.clickSignupLogin();

        // 5. Verify 'Login to your account' is visible
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Login to your account"))).isVisible();

        // 6. Enter correct email address and password
        // Dùng tài khoản bạn đã đăng ký thành công
        signupLoginPage.fillLoginForm("ducanhdhtb@gmail.com", "ducanh123");

        // 7. Click 'login' button
        signupLoginPage.clickLoginButton();

        // 8. Verify that 'Logged in as username' is visible
        assertThat(page.locator("#header")).containsText("Logged in as Nguyễn Đức Anh");

        // 9. Click 'Logout' button
        homePage.clickLogout();

        // 10. Verify that user is navigated to login page
        // Kiểm tra URL hoặc tiêu đề form Login
        assertThat(page).hasURL("https://automationexercise.com/login");
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Login to your account"))).isVisible();
    }
}