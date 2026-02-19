package automation_exercise.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class SignupLoginPage {
    private final Page page;

    // ==========================================
    // 1. LOCATORS (Giữ nguyên toàn bộ biến cũ)
    // ==========================================
    private final String SIGNUP_NAME_INPUT  = "input[data-qa='signup-name']";
    private final String SIGNUP_EMAIL_INPUT = "input[data-qa='signup-email']";
    private final String SIGNUP_BUTTON      = "button[data-qa='signup-button']";

    private final String PASSWORD_INPUT     = "#password";
    private final String DAYS_SELECT        = "#days";
    private final String MONTHS_SELECT      = "#months";
    private final String YEARS_SELECT       = "#years";

    private final String FIRST_NAME_INPUT   = "#first_name";
    private final String LAST_NAME_INPUT    = "#last_name";
    private final String ADDRESS_INPUT      = "#address1";
    private final String STATE_INPUT        = "#state";
    private final String CITY_INPUT         = "#city";
    private final String ZIPCODE_INPUT      = "#zipcode";
    private final String MOBILE_INPUT       = "#mobile_number";
    private final String CREATE_ACCOUNT_BTN = "button[data-qa='create-account']";

    // Các biến phụ bạn đã khai báo
    private final String FIRST_NAME     = "#first_name";
    private final String LAST_NAME      = "#last_name";
    private final String ADDRESS        = "#address1";
    private final String STATE          = "#state";
    private final String CITY           = "#city";
    private final String ZIPCODE        = "#zipcode";
    private final String MOBILE_NUMBER  = "#mobile_number";

    public SignupLoginPage(Page page) {
        this.page = page;
    }

    // ==========================================
    // 2. ACTIONS (Giữ nguyên toàn bộ hàm cũ)
    // ==========================================

    public void fillSignupForm(String name, String email) {
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill(name);
        page.locator("form").filter(new Locator.FilterOptions().setHasText("Signup"))
                .getByPlaceholder("Email Address").fill(email);
    }

    public void clickSignupButton() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Signup")).click();
    }

    public void fillLoginForm(String email, String password) {
        page.locator("form").filter(new Locator.FilterOptions().setHasText("Login")).getByPlaceholder("Email Address").fill(email);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill(password);
    }

    public void clickLoginButton() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
    }

    public String getErrorMessage() {
        return page.locator("form[action='/login'] p").innerText();
    }

    public String getSignupErrorMessage() {
        return page.locator("form[action='/signup'] p").innerText();
    }

    public void fillAccountInformation(String password, String day, String month, String year) {
        page.locator(PASSWORD_INPUT).fill(password);
        page.selectOption("#days", day);
        page.selectOption("#months", month);
        page.selectOption("#years", year);
    }

    public void fillSignupDetails(String name, String email) {
        page.locator(SIGNUP_NAME_INPUT).fill(name);
        page.locator(SIGNUP_EMAIL_INPUT).fill(email);
        page.locator(SIGNUP_BUTTON).click();
    }

    public void fillSignupDetailsWithRandomEmail(String name) {
        String randomEmail = "tester" + System.currentTimeMillis() + "@gmail.com";
        page.locator(SIGNUP_NAME_INPUT).fill(name);
        page.locator(SIGNUP_EMAIL_INPUT).fill(randomEmail);
        page.locator(SIGNUP_BUTTON).click();
        System.out.println("Generated Random Email: " + randomEmail);
    }

    public void fillAddressDetails(String firstName, String lastName, String address, String state, String city, String zipcode, String mobile) {
        page.locator(FIRST_NAME_INPUT).fill(firstName);
        page.locator(LAST_NAME_INPUT).fill(lastName);
        page.locator(ADDRESS_INPUT).fill(address);
        page.locator(STATE_INPUT).fill(state);
        page.locator(CITY_INPUT).fill(city);
        page.locator(ZIPCODE_INPUT).fill(zipcode);
        page.locator(MOBILE_INPUT).fill(mobile);
    }

    public void clickCreateAccount() {
        page.locator(CREATE_ACCOUNT_BTN).click();
    }
}