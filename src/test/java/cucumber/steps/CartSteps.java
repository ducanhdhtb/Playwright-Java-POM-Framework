package cucumber.steps;

import cucumber.context.ScenarioContext;
import io.cucumber.java.en.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Step definitions for cart_management.feature
 */
public class CartSteps {

    private final ScenarioContext ctx;

    public CartSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ── Add to cart ───────────────────────────────────────────────────────────

    @When("the user adds product at index {int} to cart")
    public void addProductByIndex(int index) {
        ctx.productsPage.addProductToCartByIndex(index);
    }

    @When("the user adds the first product to cart")
    public void addFirstProduct() {
        ctx.productsPage.addFirstProductToCart();
    }

    // ── Cart assertions ───────────────────────────────────────────────────────

    @Then("the cart contains {int} products")
    public void verifyCartCount(int count) {
        ctx.cartPage.verifyCartCount(count);
    }

    @Then("product at row {int} has price {string}, quantity {string}, total {string}")
    public void verifyProductDetails(int row, String price, String quantity, String total) {
        ctx.cartPage.verifyProductDetails(row, price, quantity, total);
    }

    @Then("the cart is empty with message visible")
    public void verifyCartEmpty() {
        ctx.cartPage.verifyProductIsRemoved();
    }

    // ── Remove from cart ──────────────────────────────────────────────────────

    @When("the user removes product at index {int} from cart")
    public void removeProductByIndex(int index) {
        ctx.cartPage.removeProductByIndex(index);
    }

    // ── Checkout ──────────────────────────────────────────────────────────────

    @When("the user proceeds to checkout")
    public void proceedToCheckout() {
        ctx.cartPage.proceedToCheckout();
    }

    @When("the user clicks {string} on the modal")
    public void clickOnModal(String label) {
        if (label.contains("Register") || label.contains("Login")) {
            ctx.cartPage.clickRegisterLoginOnModal();
        } else {
            ctx.page.getByText(label).first().click();
        }
    }

    // ── Product detail quantity ───────────────────────────────────────────────

    @Then("the product detail page is displayed")
    public void verifyProductDetailPage() {
        ctx.productDetailPage.verifyProductDetailsVisible();
    }

    @When("the user sets quantity to {string}")
    public void setQuantity(String qty) {
        ctx.productDetailPage.setQuantity(qty);
    }

    @When("the user clicks \"Add to cart\"")
    public void clickAddToCart() {
        ctx.productDetailPage.addToCart();
    }

    @Then("the cart quantity for the product is {string}")
    public void verifyCartQuantity(String qty) {
        assertThat(ctx.page.locator(".cart_quantity button")).hasText(qty);
    }
}
