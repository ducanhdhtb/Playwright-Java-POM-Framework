package pages;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class LoginPage extends BasePage {
    // 1. Locators
    private final String emailInput = "#email";
    private final String passwordInput = "#pass";
    private final String loginBtn = "button[name='login']";

    // 2. Constructor
    public LoginPage(Page page) {
        super(page);
    }

    // 3. Actions
    @Step("Navigating to login URL: {0}")
    public void navigateToLogin(String url) {
        super.navigate(url);
    }

    @Step("Logging in with email: {0}")
    public void login(String email, String pass) {
        page.fill(emailInput, email);
        page.fill(passwordInput, pass);
        page.click(loginBtn);
    }
}
