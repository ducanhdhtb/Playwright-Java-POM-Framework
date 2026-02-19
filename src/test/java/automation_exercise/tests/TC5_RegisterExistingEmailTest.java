package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC5_RegisterExistingEmailTest extends BaseTest {

    @Test
    public void testRegisterWithExistingEmail() {
        // 1 & 2. Khởi tạo và điều hướng (Xử lý bởi BaseTest)
        homePage.navigate();

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click on 'Signup / Login' button
        homePage.clickSignupLogin();

        // 5. Verify 'New User Signup!' is visible
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("New User Signup!"))).isVisible();

        // 6. Enter name and already registered email address
        // Sử dụng email bạn đã đăng ký ở Test Case 1/2
        signupLoginPage.fillSignupForm("Nguyễn Đức Anh", "ducanhdhtb@gmail.com");

        // 7. Click 'Signup' button
        signupLoginPage.clickSignupButton();

        // 8. Verify error 'Email Address already exist!' is visible
        // Sử dụng Assertion để kiểm tra text hiển thị trực tiếp trên form Signup
        assertThat(page.locator("form[action='/signup'] p"))
                .hasText("Email Address already exist!");

        // Log ra console để xác nhận thêm (tùy chọn)
        System.out.println("Lỗi xuất hiện: " + signupLoginPage.getSignupErrorMessage());
    }
}