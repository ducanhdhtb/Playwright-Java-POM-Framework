package automation_exercise.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object Class for managing elements and interactions on the Products Page.
 * This class follows the POM design pattern to separate UI structure from test logic.
 */
public class ProductsPage {
    private final Page page;

    // ==========================================
    // 1. LOCATORS - Centralized Element Selectors
    // ==========================================
    private final String SEARCH_INPUT      = "input#search_product";
    private final String SEARCH_BUTTON     = "#submit_search";
    private final String PRODUCT_INFO_LIST = ".productinfo p";
    private final String PRODUCT_BOX       = ".productinfo";
    private final String ADD_TO_CART_BTN   = ".add-to-cart";
    private final String VIEW_PRODUCT_BTN  = ".choose a";
    private final String VIEW_CART_LINK    = "View Cart";
    private final String CONTINUE_SHOPPING_BTN = "button.close-modal";


    public ProductsPage(Page page) {
        this.page = page;
    }

    // ==========================================
    // 2. ACTIONS - Business Logic Methods
    // ==========================================

    /**
     * Searches for a product by typing the name into the search bar and clicking the search button.
     * @param productName The specific product name or keyword to search for.
     */

    /**
     * Adds a product to the cart by its index in the list.
     * @param index The zero-based index of the product.
     */
    public void addProductToCartByIndex(int index) {
        Locator product = page.locator(".single-products").nth(index);
        product.hover();
        product.locator(".add-to-cart").first().click();
    }

    public void searchProduct(String productName) {
        page.getByPlaceholder("Search Product").fill(productName);
        page.locator(SEARCH_BUTTON).click();
    }

    /**
     * Clicks the 'Continue Shopping' button on the success modal.
     */
    public void clickContinueShopping() {
        page.locator(CONTINUE_SHOPPING_BTN).click();
    }

    /**
     * Adds the first available product in the list to the shopping cart.
     * It performs a hover action first to ensure the 'Add to cart' button is interactable.
     */
    public void addFirstProductToCart() {
        page.locator(PRODUCT_BOX).first().hover();
        page.locator(ADD_TO_CART_BTN).first().click();
    }

    /**
     * Navigates to the product detail page by clicking the 'View Product' link of the first item.
     */
    public void clickViewProductOfFirstItem() {
        page.locator(VIEW_PRODUCT_BTN).first().click();
    }

    /**
     * Clicks the 'View Cart' link, typically found in the header or the post-addition modal.
     */
    public void clickViewCart() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(VIEW_CART_LINK)).first().click();
    }

    /**
     * Verifies that every product name in the search results contains the specified keyword.
     * Uses Regex with CASE_INSENSITIVE flag to prevent failures due to casing differences.
     * @param keyword The keyword expected to be found in each product name (e.g., "Blue").
     */
    public void verifyAllProductNamesContain(String keyword) {
        // Define the locator for all product name elements
        Locator names = page.locator(PRODUCT_INFO_LIST);

        // Ensure at least one product is visible before verification (Auto-wait)
        assertThat(names.first()).isVisible();

        // Compile a Case-Insensitive Regex Pattern
        Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);

        // Iterate through all locators returned and perform the assertion
        for (Locator item : names.all()) {
            // Playwright Assertion with built-in retry mechanism
            //System.out.println(pattern);
            assertThat(item).containsText(pattern);
        }
    }


//    Dễ hiểu nhất thì bạn hãy tưởng tượng cái Pattern này giống như một "Khuôn đúc" hoặc một "Bộ lọc thông minh" vậy.
//    1. Ví dụ đời thực
//    Giả sử bạn có một cái máy lọc đồ chơi.
//    Bạn bảo máy: "Hãy lọc tất cả những con gấu".
//    Nếu máy khó tính: Nó chỉ lấy gấu bông màu nâu đúng ý nó.
//    Nếu máy có Pattern.CASE_INSENSITIVE: Nó sẽ lấy cả gấu nâu, gấu trắng, gấu to, gấu nhỏ. Chỉ cần là "gấu" là nó cho qua hết.
//    2. Trong Code của bạnK
//    khi bạn viết:Pattern pattern = Pattern.compile("Blue", Pattern.CASE_INSENSITIVE);
//    Nó tạo ra một quy tắc tìm kiếm:Từ khóa: "Blue".
//    Chế độ: Không phân biệt chữ hoa hay chữ thường (A = a).
//    3. Kết quả khi so sánh
//    Khi Playwright cầm cái "quy tắc" này đi quét các sản phẩm trên web:


//    Tên sản phẩm trên Web	   Kết quả với "Khuôn" này	      Giải thích
//    Blue Top                 PASS	                          Khớp hoàn toàn.
//    BLUE SHIRT	           PASS	Khớp                      vì đã bật chế độ không phân biệt hoa thường.
//    dark blue jeans	       PASS	                          Khớp vì có chứa chữ "blue" bên trong.
//    Red Dress	               FAIL	                          Không có chữ "blue" nào cả.

    /**
     * Navigates to the product detail page by clicking the 'View Product' link of a specific item.
     * @param index The zero-based index of the product.
     */
    public void clickViewProductByIndex(int index) {
        page.locator(".choose a").nth(index).click();
    }

    private String brandsSidebar = "//div[@class='brands_products']";

    public void verifyBrandsVisible() {
        assertThat(page.locator(brandsSidebar)).isVisible();
    }

    public void selectBrand(String brandName) {
        page.locator("//a[contains(text(),'" + brandName + "')]").click();
    }

    public void verifyBrandPage(String brandName) {
        assertThat(page.locator("//h2[contains(text(),'" + brandName + "')]"))
                .isVisible();
    }
}