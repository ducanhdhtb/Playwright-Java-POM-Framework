package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import java.nio.file.Path;

public class ContactUsPage extends BasePage {

    private static final String SUCCESS_MESSAGE = "#contact-page .status.alert.alert-success";

    public ContactUsPage(Page page) {
        super(page);
    }

    @Step("Filling contact form for: {0}, Email: {1}, Subject: {2}")
    public void fillContactForm(String name, String email, String subject, String message) {
        byRole(AriaRole.TEXTBOX, "Name").fill(name);
        byExactRole(AriaRole.TEXTBOX, "Email").fill(email);
        byRole(AriaRole.TEXTBOX, "Subject").fill(subject);
        byRole(AriaRole.TEXTBOX, "Your Message Here").fill(message);
    }

    @Step("Uploading file: {0}")
    public void uploadFile(Path path) {
        page.setInputFiles("input[name='upload_file']", path);
    }

    @Step("Clicking 'Submit' button and accepting alert")
    public void clickSubmit() {
        page.locator("input[data-qa='submit-button']").click();
    }

    @Step("Waiting for contact form success message")
    public void waitForSuccessMessageVisible() {
        Locator msg = page.locator(SUCCESS_MESSAGE);
        msg.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15_000));
    }

    @Step("Clicking 'Home' button from contact page")
    public void clickHome() {
        locator("#contact-page a[href='/']").click();
    }
}
