package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HomePage extends BasePage {

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
        super(page);
    }

    @Step("Navigating to URL: {0}")
    public void navigate(String url) {
        super.navigate(url);
        // Stabilize: ensure the app shell is ready before tests assert title or click header links.
        locator("#header").waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(30_000));
    }

    @Step("Clicking on 'Signup / Login' button")
    public void clickSignupLogin() {
        byRole(AriaRole.LINK, SIGNUP_LOGIN_LINK).click();
    }

    @Step("Clicking on 'Delete Account' button")
    public void deleteAccount() {
        byRole(AriaRole.LINK, DELETE_ACCOUNT_LINK).click();
    }

    @Step("Clicking on 'Logout' button")
    public void clickLogout() {
        byRole(AriaRole.LINK, LOGOUT_LINK).click();
    }

    @Step("Clicking on 'Contact Us' button")
    public void clickContactUs() {
        byRole(AriaRole.LINK, CONTACT_US_LINK).click();
    }

    @Step("Clicking on 'Test Cases' button")
    public void clickTestCases() {
        super.navigate("https://automationexercise.com/test_cases");
        waitForUrl("**/test_cases");
    }

    @Step("Clicking on 'Products' button")
    public void clickProducts() {
        super.navigate("https://automationexercise.com/products");
    }

    @Step("Scrolling down to the footer")
    public void scrollToFooter() {
        locator(FOOTER).scrollIntoViewIfNeeded();
    }

    @Step("Subscribing with email: {0}")
    public void subscribe(String email) {
        locator(SUBSCRIBE_EMAIL).fill(email);
        locator(SUBSCRIBE_BUTTON).click();
    }

    @Step("Clicking on 'Cart' button")
    public void clickCart() {
        byRole(AriaRole.LINK, CART_LINK).first().click();
    }
    
    @Step("Selecting category '{1}' under '{0}'")
    public void selectCategory(String mainCategory, String subCategory) {
        locator(mainCategory).click();
        String categoryHref = resolveCategoryHref(mainCategory, subCategory);
        if (categoryHref != null) {
            locator(categoryHref).click();
            return;
        }

        byExactRole(com.microsoft.playwright.options.AriaRole.LINK, subCategory).click();
    }

    private String resolveCategoryHref(String mainCategory, String subCategory) {
        if (mainCategory.contains("Women")) {
            return switch (subCategory) {
                case "Dress" -> "a[href='/category_products/1']";
                case "Tops" -> "a[href='/category_products/2']";
                case "Saree" -> "a[href='/category_products/7']";
                default -> null;
            };
        }
        if (mainCategory.contains("Men")) {
            return switch (subCategory) {
                case "Tshirts" -> "a[href='/category_products/3']";
                case "Jeans" -> "a[href='/category_products/6']";
                default -> null;
            };
        }
        if (mainCategory.contains("Kids")) {
            return switch (subCategory) {
                case "Dress" -> "a[href='/category_products/4']";
                case "Tops & Shirts" -> "a[href='/category_products/5']";
                default -> null;
            };
        }
        return null;
    }

    @Step("Selecting brand: {0}")
    public void selectBrand(String brandLink) {
        byRole(com.microsoft.playwright.options.AriaRole.LINK, brandLink).click();
    }

    // VERIFICATIONS
    @Step("Verifying 'SUBSCRIPTION' title is visible")
    public void verifySubscriptionTitleIsVisible() {
        assertThat(locator(SUBSCRIPTION_TITLE)).isVisible();
    }

    @Step("Verifying subscription success message is visible")
    public void verifySuccessMessage() {
        assertThat(locator(SUCCESS_MESSAGE)).isVisible();
        assertThat(locator(SUCCESS_MESSAGE)).hasText("You have been successfully subscribed!");
    }

    @Step("Verifying user is logged in as '{0}'")
    public void verifyLoggedInAs(String username) {
        assertThat(locator("#header")).containsText("Logged in as " + username);
    }

    @Step("Verifying categories are visible")
    public void verifyCategoriesVisible() {
        assertThat(locator(CATEGORY_SIDEBAR)).isVisible();
    }

    @Step("Verifying category page title is '{0}'")
    public void verifyCategoryPageTitle(String expectedTitle) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(expectedTitle, java.util.regex.Pattern.CASE_INSENSITIVE);
        assertThat(locator(CATEGORY_TITLE)).containsText(pattern);
    }
}
