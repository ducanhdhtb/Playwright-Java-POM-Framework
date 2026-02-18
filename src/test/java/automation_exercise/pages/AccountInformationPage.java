package automation_exercise.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class AccountInformationPage {
    private final Page page;

    public AccountInformationPage(Page page) {
        this.page = page;
    }

    public void fillAccountDetails(String password, String day, String month, String year) {
        page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Mr.")).check();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password *")).fill(password);
        page.locator("#days").selectOption(day);
        page.locator("#months").selectOption(month);
        page.locator("#years").selectOption(year);
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Sign up for our newsletter!")).check();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Receive special offers from")).check();
    }

    public void fillAddressDetails(String firstName, String lastName, String company, String address, String country, String state, String city, String zip, String mobile) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First name *")).fill(firstName);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last name *")).fill(lastName);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Company").setExact(true)).fill(company);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Address * (Street address, P.")).fill(address);
        page.getByLabel("Country *").selectOption(country);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("State *")).fill(state);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("City *")).fill(city);
        page.locator("#zipcode").fill(zip);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mobile Number *")).fill(mobile);
    }

    public void clickCreateAccount() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Account")).click();
    }
}