package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC8_VerifyProductDetailTest extends BaseTest {

    @Test(priority = 8, groups = {"regression"})
    @Step("TC8: Verify product detail page")
    public void testVerifyAllProductsAndDetail() {
        // 1 & 2. Khởi tạo và điều hướng (Xử lý bởi BaseTest)
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));

        // 3. Verify that home page is visible successfully
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Click on 'Products' button
        homePage.get().clickProducts();

        // 5. Verify user is navigated to ALL PRODUCTS page successfully
        assertThat(getPage()).hasURL("https://automationexercise.com/products");
        assertThat(getPage().getByRole(AriaRole.HEADING,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("All Products").setExact(true))).isVisible();

        // 6. The products list is visible
        assertThat(getPage().locator(".features_items")).isVisible();

        // 7. Click on 'View Product' of first product
        productsPage.get().clickViewProductOfFirstItem();

        // 8. User is landed to product detail page
        assertThat(getPage()).hasURL(java.util.regex.Pattern.compile(".*/product_details/.*"));

        // 9. Verify that detail is visible
        assertThat(productDetailPage.get().productName()).isVisible();
        assertThat(productDetailPage.get().category()).isVisible();
        assertThat(productDetailPage.get().price()).isVisible();
        assertThat(productDetailPage.get().availability()).isVisible();
        assertThat(productDetailPage.get().condition()).isVisible();
        assertThat(productDetailPage.get().brand()).isVisible();
    }
}
