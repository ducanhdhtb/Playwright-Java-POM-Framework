package automation_exercise.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class HomePage {
    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public void navigate() {
        page.navigate("https://automationexercise.com/");
    }

    public void clickSignupLogin() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Signup / Login")).click();
    }

    public void deleteAccount() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(" Delete Account")).click();
    }
}