package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC7_VerifyTestCasesPage extends BaseTest {

    @Test(priority = 7, groups = {"regression"})
    @Step("TC7: Open the test cases page")
    public void testVerifyTestCasesPageNavigation() {
        // 1 & 2. Khởi tạo trình duyệt và điều hướng (Xử lý bởi BaseTest)
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));

        // 3. Verify that home page is visible successfully
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Click on 'Test Cases' button
        homePage.get().clickTestCases();

        // 5. Verify user is navigated to test cases page successfully
        // Kiểm tra URL chính xác của trang Test Cases
        assertThat(getPage()).hasURL("https://automationexercise.com/test_cases");

        // Kiểm tra tiêu đề trang hoặc một phần tử đặc trưng trên trang này
        assertThat(getPage().getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Test Cases").setExact(true))).isVisible();
    }
}
