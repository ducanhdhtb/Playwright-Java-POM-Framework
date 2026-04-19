package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

public class AccountInformationPage extends BasePage {

    public AccountInformationPage(Page page) {
        super(page);
    }

    @Step("Filling account details with Password: [PROTECTED], DoB: {1}-{2}-{3}")
    public void fillAccountDetails(String password, String day, String month, String year) {
        byRole(AriaRole.RADIO, "Mr.").check();
        byRole(AriaRole.TEXTBOX, "Password *").fill(password);
        locator("#days").selectOption(day);
        locator("#months").selectOption(month);
        locator("#years").selectOption(year);
        byRole(AriaRole.CHECKBOX, "Sign up for our newsletter!").check();
        byRole(AriaRole.CHECKBOX, "Receive special offers from").check();
    }

    @Step("Filling address details for {0} {1}")
    public void fillAddressDetails(String firstName, String lastName, String company, String address, String country, String state, String city, String zip, String mobile) {
        byRole(AriaRole.TEXTBOX, "First name *").fill(firstName);
        byRole(AriaRole.TEXTBOX, "Last name *").fill(lastName);
        byExactRole(AriaRole.TEXTBOX, "Company").fill(company);
        byRole(AriaRole.TEXTBOX, "Address * (Street address, P.").fill(address);
        byLabel("Country *").selectOption(country);
        byRole(AriaRole.TEXTBOX, "State *").fill(state);
        byRole(AriaRole.TEXTBOX, "City *").fill(city);
        locator("#zipcode").fill(zip);
        byRole(AriaRole.TEXTBOX, "Mobile Number *").fill(mobile);
    }

    @Step("Clicking 'Create Account' button")
    public void clickCreateAccount() {
        byRole(AriaRole.BUTTON, "Create Account").click();
    }
}
