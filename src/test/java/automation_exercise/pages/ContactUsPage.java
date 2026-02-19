package automation_exercise.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.nio.file.Path;

public class ContactUsPage {
    private final Page page;

    public ContactUsPage(Page page) {
        this.page = page;
    }

    public void fillContactForm(String name, String email, String subject, String message) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill(name);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email").setExact(true)).fill(email);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Subject")).fill(subject);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your Message Here")).fill(message);
    }

    public void uploadFile(java.nio.file.Path path) {
        page.setInputFiles("input[name='upload_file']", path);
    }

    public void clickSubmit() {
        // Lắng nghe và tự động nhấn 'OK' khi hộp thoại xuất hiện
        page.onceDialog(dialog -> dialog.accept());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit")).click();
    }

    public void clickHome() {
        page.locator("#contact-page").getByRole(AriaRole.LINK).click();
    }
}