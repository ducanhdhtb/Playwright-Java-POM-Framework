package automation_exercise.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class SignupLoginPage {
    private final Page page;

    public SignupLoginPage(Page page) {
        this.page = page;
    }

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

    // Thêm hàm này vào SignupLoginPage.java
    public String getErrorMessage() {
        return page.locator("form[action='/login'] p").innerText();
    }

    // Thêm vào SignupLoginPage.java
    public String getSignupErrorMessage() {
        // Locator này nhắm vào thông báo lỗi riêng của form Signup
        return page.locator("form[action='/signup'] p").innerText();
    }
}