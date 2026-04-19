package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import java.nio.file.Path;

public class ContactUsPage {
    private final Page page;

    public ContactUsPage(Page page) {
        this.page = page;
    }

    @Step("Filling contact form for: {0}, Email: {1}, Subject: {2}")
    public void fillContactForm(String name, String email, String subject, String message) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill(name);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email").setExact(true)).fill(email);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Subject")).fill(subject);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your Message Here")).fill(message);
    }

    @Step("Uploading file: {0}")
    public void uploadFile(Path path) {
        page.setInputFiles("input[name='upload_file']", path);
    }

    @Step("Clicking 'Submit' button and accepting alert")
    public void clickSubmit() {
        page.onceDialog(dialog -> dialog.accept());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit")).click();
    }

    @Step("Clicking 'Home' button from contact page")
    public void clickHome() {
        page.locator("#contact-page").getByRole(AriaRole.LINK).click();
    }
}