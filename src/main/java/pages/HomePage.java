package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HomePage {
    private final Page page;

    // Locators... (kept as is)
    private final String FOOTER             = "#footer";
    private final String SUBSCRIPTION_TITLE = "h2:has-text('Subscription')";
    private final String SUBSCRIBE_EMAIL    = "#susbscribe_email";
    private final String SUBSCRIBE_BUTTON   = "#subscribe";
    private final String SUCCESS_MESSAGE    = ".alert-success";
    private final String SIGNUP_LOGIN_LINK  = "Signup / Login";
    private final String DELETE_ACCOUNT_LINK = "Delete Account";
    private final String LOGOUT_LINK        = "Logout";
    private final String CONTACT_US_LINK    = "Contact us";
    private final String TEST_CASES_LINK    = "Test Cases";
    private final String PRODUCTS_LINK      = "Products";
    private final String CART_LINK          = "Cart";
    private final String CATEGORY_SIDEBAR = ".left-sidebar";
    private final String CATEGORY_TITLE   = ".title.text-center";


    public HomePage(Page page) {
        this.page = page;
    }

    @Step("Navigating to URL: {0}")
    public void navigate(String url) {
        page.navigate(url);
    }

    @Step("Clicking on 'Signup / Login' button")
    public void clickSignupLogin() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(SIGNUP_LOGIN_LINK)).click();
    }

    @Step("Clicking on 'Delete Account' button")
    public void deleteAccount() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(DELETE_ACCOUNT_LINK)).click();
    }

    @Step("Clicking on 'Logout' button")
    public void clickLogout() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(LOGOUT_LINK)).click();
    }

    @Step("Clicking on 'Contact Us' button")
    public void clickContactUs() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CONTACT_US_LINK)).click();
    }

    @Step("Clicking on 'Test Cases' button")
    public void clickTestCases() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(TEST_CASES_LINK).setExact(true)).click();
    }

    @Step("Clicking on 'Products' button")
    public void clickProducts() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(PRODUCTS_LINK)).click();
    }

    @Step("Scrolling down to the footer")
    public void scrollToFooter() {
        page.locator(FOOTER).scrollIntoViewIfNeeded();
    }

    @Step("Subscribing with email: {0}")
    public void subscribe(String email) {
        page.locator(SUBSCRIBE_EMAIL).fill(email);
        page.locator(SUBSCRIBE_BUTTON).click();
    }

    @Step("Clicking on 'Cart' button")
    public void clickCart() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CART_LINK)).first().click();
    }
    
    @Step("Selecting category '{1}' under '{0}'")
    public void selectCategory(String mainCategory, String subCategory) {
        page.locator(mainCategory).click();
        page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                new Page.GetByRoleOptions().setName(subCategory)).click();
    }

    @Step("Selecting brand: {0}")
    public void selectBrand(String brandLink) {
        page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                new Page.GetByRoleOptions().setName(brandLink)).click();
    }

    // VERIFICATIONS
    @Step("Verifying 'SUBSCRIPTION' title is visible")
    public void verifySubscriptionTitleIsVisible() {
        assertThat(page.locator(SUBSCRIPTION_TITLE)).isVisible();
    }

    @Step("Verifying subscription success message is visible")
    public void verifySuccessMessage() {
        assertThat(page.locator(SUCCESS_MESSAGE)).isVisible();
        assertThat(page.locator(SUCCESS_MESSAGE)).hasText("You have been successfully subscribed!");
    }

    @Step("Verifying user is logged in as '{0}'")
    public void verifyLoggedInAs(String username) {
        com.microsoft.playwright.Locator loginStatus = page.locator("text=Logged in as " + username);
        assertThat(loginStatus).isVisible();
    }

    @Step("Verifying categories are visible")
    public void verifyCategoriesVisible() {
        assertThat(page.locator(CATEGORY_SIDEBAR)).isVisible();
    }

    @Step("Verifying category page title is '{0}'")
    public void verifyCategoryPageTitle(String expectedTitle) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(expectedTitle, java.util.regex.Pattern.CASE_INSENSITIVE);
        assertThat(page.locator(CATEGORY_TITLE)).containsText(pattern);
    }
}