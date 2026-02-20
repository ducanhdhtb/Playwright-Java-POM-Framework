package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC7_VerifyTestCasesPage extends BaseTest {

    @Test(priority = 7)
    public void testVerifyTestCasesPageNavigation() {
        // 1 & 2. Khởi tạo trình duyệt và điều hướng (Xử lý bởi BaseTest)
        homePage.navigate();

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click on 'Test Cases' button
        homePage.clickTestCases();

        // 5. Verify user is navigated to test cases page successfully
        // Kiểm tra URL chính xác của trang Test Cases
        assertThat(page).hasURL("https://automationexercise.com/test_cases");

        // Kiểm tra tiêu đề trang hoặc một phần tử đặc trưng trên trang này
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Test Cases").setExact(true))).isVisible();
    }
}