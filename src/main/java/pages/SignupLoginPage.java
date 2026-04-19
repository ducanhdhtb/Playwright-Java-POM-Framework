package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class SignupLoginPage extends BasePage {

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
        super(page);
    }

    @Step("Filling signup form with Name: {0} and Email: {1}")
    public void fillSignupForm(String name, String email) {
        locator(SIGNUP_NAME_INPUT).fill(name);
        locator(SIGNUP_EMAIL_INPUT).fill(email);
    }

    @Step("Clicking 'Signup' button")
    public void clickSignupButton() {
        locator("form[action='/signup']").evaluate("form => form.submit()");
    }

    @Step("Filling login form with Email: {0} and Password: [PROTECTED]")
    public void fillLoginForm(String email, String password) {
        locator("input[data-qa='login-email']").fill(email);
        locator("input[data-qa='login-password']").fill(password);
    }

    @Step("Clicking 'Login' button")
    public void clickLoginButton() {
        locator("form[action='/login']").evaluate("form => form.submit()");
    }

    @Step("Getting login error message")
    public String getErrorMessage() {
        return byText("Your email or password is incorrect!",
                new Page.GetByTextOptions().setExact(true)).innerText();
    }

    @Step("Getting signup error message")
    public String getSignupErrorMessage() {
        return byRole(AriaRole.HEADING, "Enter Account Information")
                .innerText();
    }

    @Step("Filling account information with Password: [PROTECTED], DoB: {1}-{2}-{3}")
    public void fillAccountInformation(String password, String day, String month, String year) {
        locator(PASSWORD_INPUT).fill(password);
        page.selectOption("#days", day);
        page.selectOption("#months", month);
        page.selectOption("#years", year);
    }
    
    @Step("Filling signup details with Name: {0} and Email: {1}")
    public void fillSignupDetails(String name, String email) {
        locator(SIGNUP_NAME_INPUT).fill(name);
        locator(SIGNUP_EMAIL_INPUT).fill(email);
        locator(SIGNUP_BUTTON).click();
    }

    @Step("Filling signup details with Name: {0} and a random email")
    public String fillSignupDetailsWithRandomEmail(String name) {
        String randomEmail = "tester" + System.currentTimeMillis() + "@gmail.com";
        locator(SIGNUP_NAME_INPUT).fill(name);
        locator(SIGNUP_EMAIL_INPUT).fill(randomEmail);
        locator(SIGNUP_BUTTON).click();
        System.out.println("Generated Random Email: " + randomEmail);
        return randomEmail;
    }

    @Step("Filling address details: {0} {1}, Address: {2}, City: {4}, State: {3}")
    public void fillAddressDetails(String firstName, String lastName, String address, String state, String city, String zipcode, String mobile) {
        locator(FIRST_NAME_INPUT).fill(firstName);
        locator(LAST_NAME_INPUT).fill(lastName);
        locator(ADDRESS_INPUT).fill(address);
        locator(STATE_INPUT).fill(state);
        locator(CITY_INPUT).fill(city);
        locator(ZIPCODE_INPUT).fill(zipcode);
        locator(MOBILE_INPUT).fill(mobile);
    }

    @Step("Clicking 'Create Account' button")
    public void clickCreateAccount() {
        locator(CREATE_ACCOUNT_BTN).click();
    }
}
