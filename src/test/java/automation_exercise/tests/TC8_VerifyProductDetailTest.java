package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC8_VerifyProductDetailTest extends BaseTest {

    @Test(priority = 8)
    public void testVerifyAllProductsAndDetail() {
        // 1 & 2. Khởi tạo và điều hướng (Xử lý bởi BaseTest)
        homePage.navigate();

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Click on 'Products' button
        homePage.clickProducts();

        // 5. Verify user is navigated to ALL PRODUCTS page successfully
        assertThat(page).hasURL("https://automationexercise.com/products");
        assertThat(page.getByRole(AriaRole.HEADING,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("All Products").setExact(true))).isVisible();

        // 6. The products list is visible
        assertThat(page.locator(".features_items")).isVisible();

        // 7. Click on 'View Product' of first product
        productsPage.clickViewProductOfFirstItem();

        // 8. User is landed to product detail page
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/product_details/.*"));

        // 9. Verify that detail is visible
        assertThat(productDetailPage.productName()).isVisible();
        assertThat(productDetailPage.category()).isVisible();
        assertThat(productDetailPage.price()).isVisible();
        assertThat(productDetailPage.availability()).isVisible();
        assertThat(productDetailPage.condition()).isVisible();
        assertThat(productDetailPage.brand()).isVisible();
    }
}