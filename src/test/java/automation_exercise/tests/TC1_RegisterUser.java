package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

// Kế thừa từ BaseTest
public class TC1_RegisterUser extends BaseTest {

    @Test
    public void testRegisterUser() {
        // 1. Dùng homePage từ BaseTest
        homePage.navigate();
        homePage.clickSignupLogin();

        // 2. Dùng signupLoginPage từ BaseTest
        String timestamp = String.valueOf(System.currentTimeMillis());
        String name = "Nguyễn Đức Anh";
//        signupLoginPage.fillSignupForm(name, "auto_test" + timestamp + "@example.com");
        signupLoginPage.fillSignupForm(name, "ducanhdhtb"  + "@gmail.com");
        signupLoginPage.clickSignupButton();

        // 3. Dùng accountPage từ BaseTest
        accountPage.fillAccountDetails("ducanh123", "15", "2", "1993");
        accountPage.fillAddressDetails("Nguyễn Đức", "Anh", "Techcombank", "119 Trần Duy Hưng",
                "United States", "84", "Washington", "98", "0385672074");
        accountPage.clickCreateAccount();

        // Kiểm tra kết quả
        assertThat(page.getByText("Account Created!")).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();

        // Kiểm tra login thành công
        assertThat(page.locator("#header")).containsText("Logged in as " + name);

        // Xóa tài khoản để sạch data
//        homePage.deleteAccount();
//        assertThat(page.getByText("Account Deleted!")).isVisible();
//        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
    }
}