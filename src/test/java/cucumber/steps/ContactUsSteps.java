package cucumber.steps;

import cucumber.context.ScenarioContext;
import io.cucumber.java.en.*;

import java.nio.file.Paths;

/**
 * Step definitions for contact_us.feature
 */
public class ContactUsSteps {

    private final ScenarioContext ctx;

    public ContactUsSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @When("the user fills the contact form with name {string}, email {string}, subject {string}, message {string}")
    public void fillContactForm(String name, String email, String subject, String message) {
        ctx.contactPage.fillContactForm(name, email, subject, message);
    }

    @When("the user uploads file {string}")
    public void uploadFile(String filePath) {
        ctx.contactPage.uploadFile(Paths.get(filePath));
    }

    @When("the user submits the contact form")
    public void submitContactForm() {
        ctx.contactPage.clickSubmit();
    }

    @Then("the success message {string} is visible")
    public void verifySuccessMessage(String message) {
        ctx.contactPage.waitForSuccessMessage(message);
    }

    @When("the user clicks \"Home\" on the contact page")
    public void clickHomeOnContactPage() {
        ctx.contactPage.clickHome();
    }
}
