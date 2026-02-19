package automation_exercise.tests;
import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC3_LoginUserWithIncorrectCredentials extends BaseTest {

    @Test(priority = 3)
    public void testLoginWithIncorrectCredentials() {
        // 1 & 2. Launch & Navigate (Đã có trong BaseTest)
        homePage.navigate();

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click on 'Signup / Login' button
        homePage.clickSignupLogin();

        // 5. Verify 'Login to your account' is visible
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Login to your account"))).isVisible();

        // 6. Enter incorrect email address and password
        signupLoginPage.fillLoginForm("wrong_email@gmail.com", "wrong_password");

        // 7. Click 'login' button
        signupLoginPage.clickLoginButton();

        // 8. Verify error 'Your email or password is incorrect!' is visible
        // Cách 1: Dùng Assertion của Playwright (Khuyên dùng)
        assertThat(page.locator("form[action='/login'] p"))
                .hasText("Your email or password is incorrect!");

        // Cách 2: Kiểm tra trực tiếp nội dung nếu cần log ra console
        String error = signupLoginPage.getErrorMessage();
        System.out.println("Thông báo lỗi hiển thị: " + error);
    }
}