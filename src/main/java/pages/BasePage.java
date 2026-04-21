package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;

public abstract class BasePage {
    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return page;
    }

    protected Locator locator(String selector) {
        return page.locator(selector);
    }

    protected Locator byText(String text) {
        return page.getByText(text);
    }

    protected Locator byText(String text, Page.GetByTextOptions options) {
        return page.getByText(text, options);
    }

    protected Locator byRole(AriaRole role, String name) {
        return page.getByRole(role, new Page.GetByRoleOptions().setName(name));
    }

    protected Locator byExactRole(AriaRole role, String name) {
        return page.getByRole(role, new Page.GetByRoleOptions().setName(name).setExact(true));
    }

    protected Locator byLabel(String label) {
        return page.getByLabel(label);
    }

    protected Locator byPlaceholder(String placeholder) {
        return page.getByPlaceholder(placeholder);
    }

    protected void navigate(String url) {
        // The demo site occasionally never fires the full "load" event in CI/headless.
        // DOMCONTENTLOADED is usually sufficient and makes navigation less flaky.
        page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }

    protected void waitForUrl(String urlPattern) {
        page.waitForURL(urlPattern, new Page.WaitForURLOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }
}
