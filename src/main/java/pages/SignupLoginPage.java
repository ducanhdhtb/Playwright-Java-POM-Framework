package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class SignupLoginPage {
    private final Page page;

    // Locators...
    private final String SIGNUP_NAME_INPUT  = "input[data-qa='signup-name']";
    private final String SIGNUP_EMAIL_INPUT = "input[data-qa='signup-email']";
    private final String SIGNUP_BUTTON      = "button[data-qa='signup-button']";
    private final String PASSWORD_INPUT     = "#password";
    private final String FIRST_NAME_INPUT   = "#first_name";
    private final String LAST_NAME_INPUT    = "#last_name";
    private final String ADDRESS_INPUT      = "#address1";
    private final String STATE_INPUT        = "#state";
    private final String CITY_INPUT         = "#city";
    private final String ZIPCODE_INPUT      = "#zipcode";
    private final String MOBILE_INPUT       = "#mobile_number";
    private final String CREATE_ACCOUNT_BTN = "button[data-qa='create-account']";

    public SignupLoginPage(Page page) {
        this.page = page;
    }

    @Step("Filling signup form with Name: {0} and Email: {1}")
    public void fillSignupForm(String name, String email) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill(name);
        page.locator("form").filter(new Locator.FilterOptions().setHasText("Signup"))
                .getByPlaceholder("Email Address").fill(email);
    }

    @Step("Clicking 'Signup' button")
    public void clickSignupButton() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Signup")).click();
    }

    @Step("Filling login form with Email: {0} and Password: [PROTECTED]")
    public void fillLoginForm(String email, String password) {
        page.locator("form").filter(new Locator.FilterOptions().setHasText("Login")).getByPlaceholder("Email Address").fill(email);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill(password);
    }

    @Step("Clicking 'Login' button")
    public void clickLoginButton() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
    }

    @Step("Getting login error message")
    public String getErrorMessage() {
        return page.locator("form[action='/login'] p").innerText();
    }

    @Step("Getting signup error message")
    public String getSignupErrorMessage() {
        return page.locator("form[action='/signup'] p").innerText();
    }

    @Step("Filling account information with Password: [PROTECTED], DoB: {1}-{2}-{3}")
    public void fillAccountInformation(String password, String day, String month, String year) {
        page.locator(PASSWORD_INPUT).fill(password);
        page.selectOption("#days", day);
        page.selectOption("#months", month);
        page.selectOption("#years", year);
    }
    
    @Step("Filling signup details with Name: {0} and Email: {1}")
    public void fillSignupDetails(String name, String email) {
        page.locator(SIGNUP_NAME_INPUT).fill(name);
        page.locator(SIGNUP_EMAIL_INPUT).fill(email);
        page.locator(SIGNUP_BUTTON).click();
    }

    @Step("Filling signup details with Name: {0} and a random email")
    public void fillSignupDetailsWithRandomEmail(String name) {
        String randomEmail = "tester" + System.currentTimeMillis() + "@gmail.com";
        page.locator(SIGNUP_NAME_INPUT).fill(name);
        page.locator(SIGNUP_EMAIL_INPUT).fill(randomEmail);
        page.locator(SIGNUP_BUTTON).click();
        System.out.println("Generated Random Email: " + randomEmail);
    }

    @Step("Filling address details: {0} {1}, Address: {2}, City: {4}, State: {3}")
    public void fillAddressDetails(String firstName, String lastName, String address, String state, String city, String zipcode, String mobile) {
        page.locator(FIRST_NAME_INPUT).fill(firstName);
        page.locator(LAST_NAME_INPUT).fill(lastName);
        page.locator(ADDRESS_INPUT).fill(address);
        page.locator(STATE_INPUT).fill(state);
        page.locator(CITY_INPUT).fill(city);
        page.locator(ZIPCODE_INPUT).fill(zipcode);
        page.locator(MOBILE_INPUT).fill(mobile);
    }

    @Step("Clicking 'Create Account' button")
    public void clickCreateAccount() {
        page.locator(CREATE_ACCOUNT_BTN).click();
    }
}