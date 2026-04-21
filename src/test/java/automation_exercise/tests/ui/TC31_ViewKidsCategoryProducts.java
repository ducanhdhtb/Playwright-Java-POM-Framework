package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC31: Verify Kids category products — Dress and Tops & Shirts
 *
 * Discovered via MCP exploration: TC18 only covers Women + Men,
 * Kids category (Dress/Tops & Shirts) was never tested.
 */
public class TC31_ViewKidsCategoryProducts extends BaseTest {

    @Test(
            description = "TC31: View Kids > Dress and Kids > Tops & Shirts category products",
            priority = 31,
            groups = {"regression"}
    )
    @Description("Clicks Kids > Dress then Kids > Tops & Shirts, verifies category pages load correctly")
    @Step("TC31: View Kids category products")
    public void testViewKidsCategoryProducts() {
        // 1-3. Navigate and verify home page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.verifyCategoriesVisible();

        // 4. Click Kids > Dress
        homePage.selectCategory("a[href='#Kids']", "Dress");
        homePage.verifyCategoryPageTitle("KIDS - DRESS PRODUCTS");
        assertThat(page.locator(".features_items")).isVisible();
        assertThat(page.locator(".single-products").first()).isVisible();

        // 5. Navigate back and click Kids > Tops & Shirts
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.selectCategory("a[href='#Kids']", "Tops & Shirts");
        homePage.verifyCategoryPageTitle("KIDS - TOPS & SHIRTS PRODUCTS");
        assertThat(page.locator(".features_items")).isVisible();
        assertThat(page.locator(".single-products").first()).isVisible();
    }
}
