package automation_exercise.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ProductsPage {
    private final Page page;

    public ProductsPage(Page page) {
        this.page = page;
    }

    public void searchProduct(String productName) {
        page.fill("#search_product", productName);
        page.click("#submit_search");
    }

    public void addFirstProductToCart() {
        // Hover vào sản phẩm đầu tiên và nhấn 'Add to cart'
        page.locator(".productinfo").first().hover();
        page.locator(".add-to-cart").first().click();
    }

    public void clickViewCart() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View Cart")).click();
    }
}