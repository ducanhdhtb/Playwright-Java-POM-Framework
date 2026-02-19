package automation_exercise.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

public class ProductDetailPage {
    private final Page page;

    public ProductDetailPage(Page page) {
        this.page = page;
    }

    // Các Locators cho thông tin chi tiết
    public Locator productName() { return page.locator(".product-information h2"); }
    public Locator category() { return page.locator(".product-information p").filter(new Locator.FilterOptions().setHasText("Category:")); }
    public Locator price() { return page.locator(".product-information span span"); }
    public Locator availability() { return page.locator(".product-information p").filter(new Locator.FilterOptions().setHasText("Availability:")); }
    public Locator condition() { return page.locator(".product-information p").filter(new Locator.FilterOptions().setHasText("Condition:")); }
    public Locator brand() { return page.locator(".product-information p").filter(new Locator.FilterOptions().setHasText("Brand:")); }
}