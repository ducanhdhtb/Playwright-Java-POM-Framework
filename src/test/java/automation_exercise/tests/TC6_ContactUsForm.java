package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import java.nio.file.Paths;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC6_ContactUsForm extends BaseTest {

    @Test(priority = 6)
    public void testContactUsForm() {
        // 1 & 2. Khởi tạo và điều hướng (Xử lý bởi BaseTest)
        homePage.navigate();

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click on 'Contact Us' button
        homePage.clickContactUs();

        // 5. Verify 'GET IN TOUCH' is visible
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Get In Touch"))).isVisible();

        // 6. Enter name, email, subject and message
        contactPage.fillContactForm("Đức Anh", "ducanh@test.com", "Hỗ trợ kỹ thuật", "Playwright thật tuyệt vời!");

        // 7. Upload file (Đảm bảo đường dẫn file chính xác)
        contactPage.uploadFile(Paths.get("src/test/java/automation_exercise/resources/upload-sample.txt"));

        // 8 & 9. Click 'Submit' và xử lý 'OK' button
        contactPage.clickSubmit();

        // 10. Verify success message is visible
        assertThat(page.locator(".status.alert.alert-success"))
                .hasText("Success! Your details have been submitted successfully.");

        // 11. Click 'Home' button and verify landing
        contactPage.clickHome();
        assertThat(page).hasURL("https://automationexercise.com/");
    }
}

//3. Điểm nhấn kỹ thuật
// Xử lý Dialog (page.onceDialog): Trong Playwright, bạn phải thiết lập trình lắng nghe (listener) trước khi hành động gây ra dialog xuất hiện (nút Submit). Nếu bạn click trước rồi mới viết code xử lý dialog, bài test sẽ bị treo.
//
// Upload File: Playwright không cần tương tác với cửa sổ chọn file của hệ điều hành. Nó nạp trực tiếp file vào phần tử input[type='file'].
//
// Scoping Locator: Ở bước 11, tôi dùng page.locator("#contact-page").getByRole(...) để đảm bảo click đúng nút Home nằm trong vùng nội dung trang liên hệ, tránh nhầm lẫn với các nút Home khác nếu có.