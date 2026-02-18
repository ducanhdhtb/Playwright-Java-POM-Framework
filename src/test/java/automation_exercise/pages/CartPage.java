package automation_exercise.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class CartPage {
    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public void proceedToCheckout() {
        page.getByText("Proceed To Checkout").click();
    }

    public boolean isProductInCart(String productName) {
        return page.locator(".cart_description").getByText(productName).isVisible();
    }

    public void deleteProduct() {
        page.locator(".cart_quantity_delete").first().click();
    }
}