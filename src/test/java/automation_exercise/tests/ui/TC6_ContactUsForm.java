package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import java.nio.file.Paths;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC6_ContactUsForm extends BaseTest {

	    @Test(
                priority = 6,
                dataProvider = "tc6DataProvider",
                dataProviderClass = TestData.class,
                groups = {"regression"}
        )
	    @Step("TC6: Submit the contact us form")
	    public void testContactUsForm(
                String name,
                String email,
                String subject,
                String message,
                String uploadFile,
                String expectedSuccessText
        ) {
	        // 1 & 2. Khởi tạo và điều hướng (Xử lý bởi BaseTest)
	        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));

        // 3. Verify that home page is visible successfully
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Click on 'Contact Us' button
        homePage.get().clickContactUs();

        // 5. Verify 'GET IN TOUCH' is visible
        assertThat(getPage().getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Get In Touch"))).isVisible();

	        // 6. Enter name, email, subject and message from Excel
	        contactPage.get().fillContactForm(name, email, subject, message);

	        // 7. Upload file (Đảm bảo đường dẫn file chính xác)
	        contactPage.get().uploadFile(Paths.get(uploadFile));

        // 8 & 9. Click 'Submit' và xử lý 'OK' button
        contactPage.get().clickSubmit();

	        // 10. Verify success message is visible
	        contactPage.get().waitForSuccessMessage(expectedSuccessText);

	        // 11. Click 'Home' button and verify landing
	        contactPage.get().clickHome();
	        assertThat(getPage()).hasURL("https://automationexercise.com/");
	    }
}

//3. Điểm nhấn kỹ thuật
// Xử lý Dialog (getPage().onceDialog): Trong Playwright, bạn phải thiết lập trình lắng nghe (listener) trước khi hành động gây ra dialog xuất hiện (nút Submit). Nếu bạn click trước rồi mới viết code xử lý dialog, bài test sẽ bị treo.
//
// Upload File: Playwright không cần tương tác với cửa sổ chọn file của hệ điều hành. Nó nạp trực tiếp file vào phần tử input[type='file'].
//
// Scoping Locator: Ở bước 11, tôi dùng getPage().locator("#contact-page").getByRole(...) để đảm bảo click đúng nút Home nằm trong vùng nội dung trang liên hệ, tránh nhầm lẫn với các nút Home khác nếu có.
