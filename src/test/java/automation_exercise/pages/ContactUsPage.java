package automation_exercise.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.nio.file.Path;

public class ContactUsPage {
    private final Page page;

    public ContactUsPage(Page page) {
        this.page = page;
    }

    public void fillContactForm(String name, String email, String subject, String message, Path filePath) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill(name);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email").setExact(true)).fill(email);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Subject")).fill(subject);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your Message Here")).fill(message);

        // Upload file
        page.setInputFiles("input[name='upload_file']", filePath);
    }

    public void submit() {
        // Trang này sẽ hiện một thông báo xác nhận của trình duyệt (Dialog)
        // Chúng ta cần chuẩn bị tâm lý cho Playwright để nó tự động 'Accept'
        page.onceDialog(dialog -> dialog.accept());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit")).click();
    }
}