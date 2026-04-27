package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC18_ViewCategoryProducts extends BaseTest {

    @Test(description = "Test Case 18: View Category Products",priority = 18, groups = {"regression"})
    @Step("TC18: View category products")
    public void viewCategoryProducts() {
        // 1-3. Launch and Verify Home Page & Categories
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().verifyCategoriesVisible();

        // 4-5. Click 'Women' -> 'Dress'
        // Lưu ý: Trong kịch bản của bạn ví dụ là Dress nhưng step 6 yêu cầu verify TOPS.
        // Mình sẽ làm theo step 5 là click Dress để khớp logic nhé.
        homePage.get().selectCategory("a[href='#Women']", "Dress");

        // 6. Verify category page and confirm text
        homePage.get().verifyCategoryPageTitle("WOMEN - DRESS PRODUCTS");

        // 7. On left side bar, click on any sub-category of 'Men'
        homePage.get().selectCategory("a[href='#Men']", "Tshirts");

        // 8. Verify that user is navigated to that category page
        homePage.get().verifyCategoryPageTitle("MEN - TSHIRTS PRODUCTS");
    }
}
