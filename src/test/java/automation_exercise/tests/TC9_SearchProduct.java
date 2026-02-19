package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC9_SearchProduct extends BaseTest {

    @Test
    public void searchProduct() {
        homePage.navigate();
        homePage.clickProducts();

        String searchKey = "Blue";
        productsPage.searchProduct(searchKey);

        // Verify tiêu đề
        assertThat(page.locator(".title.text-center")).hasText("Searched Products");

        // Gọi hàm verify tổng quát từ POM
        productsPage.verifyAllProductNamesContain(searchKey);
    }
}