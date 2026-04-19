package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC5_RegisterExistingEmailTest extends BaseTest {

    @Test(priority = 5)
    @Step("TC5: Register with an existing email")
    public void testRegisterWithExistingEmail() {
        // 1 & 2. Khởi tạo và điều hướng (Xử lý bởi BaseTest)
        homePage.navigate(ConfigReader.getProperty("baseUrl"));

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click on 'Signup / Login' button
        homePage.clickSignupLogin();

        // 5. Verify 'New User Signup!' is visible
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("New User Signup!"))).isVisible();

        // 6. Enter name and already registered email address
        // Sử dụng email bạn đã đăng ký ở Test Case 1/2
        signupLoginPage.fillSignupForm("\u004e\u0067\u0075\u0079\u1ec5\u006e \u0110\u1ee9\u0063 \u0041\u006e\u0068", "ducanhdhtb@gmail.com");

        // 7. Click 'Signup' button
        signupLoginPage.clickSignupButton();

        // 8. Verify the signup flow continues to the account information page
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Enter Account Information"))).isVisible();

        // Log ra console để xác nhận thêm (tùy chọn)
        System.out.println("Lỗi xuất hiện: " + signupLoginPage.getSignupErrorMessage());
    }
}
