package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC9_SearchProduct extends BaseTest {

    @Test(
            priority = 9,
            dataProvider = "tc9DataProvider",
            dataProviderClass = TestData.class,
            groups = {"smoke"}
    )
    @Step("TC9: Search for products")
    public void searchProduct(String searchKey) {
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickProducts();

        productsPage.get().searchProduct(searchKey);

        // Verify the title
        assertThat(getPage().locator(".title.text-center")).hasText("Searched Products");

        // Verify that all results contain the search key
        productsPage.get().verifyAllProductNamesContain(searchKey);
    }
}
