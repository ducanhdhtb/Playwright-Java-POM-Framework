package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC9_SearchProduct extends BaseTest {

    @Test(
            priority = 9,
            dataProvider = "productSearchDataProvider",
            dataProviderClass = TestData.class
    )
    public void searchProduct(String searchKey) {
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();

        productsPage.searchProduct(searchKey);

        // Verify the title
        assertThat(page.locator(".title.text-center")).hasText("Searched Products");

        // Verify that all results contain the search key
        productsPage.verifyAllProductNamesContain(searchKey);
    }
}