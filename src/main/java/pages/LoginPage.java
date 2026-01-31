package pages;

import com.microsoft.playwright.Page;

public class LoginPage {
    private Page page;

    // 1. Locators
    private String emailInput = "#email";
    private String passwordInput = "#pass";
    private String loginBtn = "button[name='login']";

    // 2. Constructor
    public LoginPage(Page page) {
        this.page = page;
    }

    // 3. Actions
    public void navigateToLogin(String url) {
        page.navigate(url);
    }

    public void login(String email, String pass) {
        page.fill(emailInput, email);
        page.fill(passwordInput, pass);
        page.click(loginBtn);
    }
}