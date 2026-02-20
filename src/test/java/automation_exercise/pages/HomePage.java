package automation_exercise.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object Class for the Home Page.
 * Manages navigation, menu interactions, and footer subscription logic.
 */
public class HomePage {
    private final Page page;

    // ==========================================
    // 1. LOCATORS
    // ==========================================
    private final String FOOTER             = "#footer";
    private final String SUBSCRIPTION_TITLE = "h2:has-text('Subscription')";
    private final String SUBSCRIBE_EMAIL    = "#susbscribe_email";
    private final String SUBSCRIBE_BUTTON   = "#subscribe";
    private final String SUCCESS_MESSAGE    = ".alert-success";

    // Menu Locators
    private final String SIGNUP_LOGIN_LINK  = "Signup / Login";
    private final String DELETE_ACCOUNT_LINK = "Delete Account";
    private final String LOGOUT_LINK        = "Logout";
    private final String CONTACT_US_LINK    = "Contact us";
    private final String TEST_CASES_LINK    = "Test Cases";
    private final String PRODUCTS_LINK      = "Products";
    private final String CART_LINK          = "Cart";

    // Locators cho Category Sidebar
    private final String CATEGORY_SIDEBAR = ".left-sidebar";
    private final String WOMEN_CATEGORY   = "a[href='#Women']";
    private final String MEN_CATEGORY     = "a[href='#Men']";
    private final String CATEGORY_TITLE   = ".title.text-center";

    public HomePage(Page page) {
        this.page = page;
    }

    // ==========================================
    // 2. ACTIONS - Navigation & Menu
    // ==========================================

    /**
     * Navigates to the Automation Exercise home page.
     */
    public void navigate() {
        page.navigate("https://automationexercise.com/");
    }

    public void clickSignupLogin() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(SIGNUP_LOGIN_LINK)).click();
    }

    public void deleteAccount() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(DELETE_ACCOUNT_LINK)).click();
    }

    public void clickLogout() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(LOGOUT_LINK)).click();
    }

    public void clickContactUs() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CONTACT_US_LINK)).click();
    }

    public void clickTestCases() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(TEST_CASES_LINK).setExact(true)).click();
    }

    public void clickProducts() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(PRODUCTS_LINK)).click();
    }

    // ==========================================
    // 3. ACTIONS - Footer & Subscription
    // ==========================================

    /**
     * Scrolls the page down until the footer is visible.
     */
    public void scrollToFooter() {
        page.locator(FOOTER).scrollIntoViewIfNeeded();
    }

    /**
     * Fills the subscription email and clicks the subscribe button.
     * @param email The email address to use for subscription.
     */
    public void subscribe(String email) {
        page.locator(SUBSCRIBE_EMAIL).fill(email);
        page.locator(SUBSCRIBE_BUTTON).click();
    }

    // ==========================================
    // 4. VERIFICATIONS
    // ==========================================

    /**
     * Verifies that the 'SUBSCRIPTION' header is visible in the footer section.
     */
    public void verifySubscriptionTitleIsVisible() {
        assertThat(page.locator(SUBSCRIPTION_TITLE)).isVisible();
    }

    /**
     * Verifies that the success message is displayed after a successful subscription.
     */
    public void verifySuccessMessage() {
        assertThat(page.locator(SUCCESS_MESSAGE)).isVisible();
        assertThat(page.locator(SUCCESS_MESSAGE)).hasText("You have been successfully subscribed!");
    }

    /**
     * Verifies that the 'Logged in as [username]' text is visible in the header.
     * @param username The expected username to be displayed.
     */
    public void verifyLoggedInAs(String username) {
        // Playwright locator with text-based matching
        com.microsoft.playwright.Locator loginStatus = page.locator("text=Logged in as " + username);
        assertThat(loginStatus).isVisible();
    }

    public void clickCart() {
        // Fix lỗi gạch đỏ ở bước 12 trong TC14
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CART_LINK)).first().click();
    }

    /**
     * Verifies that the categories sidebar is visible.
     */
    public void verifyCategoriesVisible() {
        assertThat(page.locator(CATEGORY_SIDEBAR)).isVisible();
    }

    /**
     * Clicks on a main category and then a specific sub-category.
     * @param mainCategory Locator string for the main category (e.g., Women, Men).
     * @param subCategory Text of the sub-category (e.g., Dress, Tshirts).
     */
    public void selectCategory(String mainCategory, String subCategory) {
        page.locator(mainCategory).click();
        page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                new Page.GetByRoleOptions().setName(subCategory)).click();
    }

    public void selectBrand(String brandLink) {
        page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                new Page.GetByRoleOptions().setName(brandLink)).click();
    }

    /**
     * Verifies the text title of the displayed category page.
     * @param expectedTitle The expected header text (e.g., "WOMEN - DRESS PRODUCTS").
     */
    public void verifyCategoryPageTitle(String expectedTitle) {
        // Sử dụng Pattern CASE_INSENSITIVE để tránh lỗi hoa thường như đã thảo luận
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(expectedTitle, java.util.regex.Pattern.CASE_INSENSITIVE);
        assertThat(page.locator(CATEGORY_TITLE)).containsText(pattern);
    }

}