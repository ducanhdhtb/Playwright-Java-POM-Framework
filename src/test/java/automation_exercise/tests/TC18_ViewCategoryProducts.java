package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC18_ViewCategoryProducts extends BaseTest {

    @Test(description = "Test Case 18: View Category Products",priority = 18)
    public void viewCategoryProducts() {
        // 1-3. Launch and Verify Home Page & Categories
        homePage.navigate();
        homePage.verifyCategoriesVisible();

        // 4-5. Click 'Women' -> 'Dress'
        // Lưu ý: Trong kịch bản của bạn ví dụ là Dress nhưng step 6 yêu cầu verify TOPS.
        // Mình sẽ làm theo step 5 là click Dress để khớp logic nhé.
        homePage.selectCategory("a[href='#Women']", "Dress");

        // 6. Verify category page and confirm text
        homePage.verifyCategoryPageTitle("WOMEN - DRESS PRODUCTS");

        // 7. On left side bar, click on any sub-category of 'Men'
        homePage.selectCategory("a[href='#Men']", "Tshirts");

        // 8. Verify that user is navigated to that category page
        homePage.verifyCategoryPageTitle("MEN - TSHIRTS PRODUCTS");
    }
}