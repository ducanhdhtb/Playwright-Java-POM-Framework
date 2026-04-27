package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

public class TC19_ViewCartBrandProducts extends BaseTest {

    @Test(description = "Test Case 19: View & Cart Brand Products",priority = 19, groups = {"regression"})
    @Step("TC19: View brand products")
    public void viewCartBrandProducts() {

        // 1-2 Navigate to Home
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));

        // 3 Click Products
        homePage.get().clickProducts();

        // 4 Verify Brands visible
        productsPage.get().verifyBrandsVisible();

        // 5 Click brand
        homePage.get().selectBrand("POLO");
        homePage.get().verifyCategoryPageTitle("Brand - Polo Products");

        homePage.get().selectBrand("H&M");
        homePage.get().verifyCategoryPageTitle("Brand - H&M Products");

        homePage.get().selectBrand("Madame");
        homePage.get().verifyCategoryPageTitle("Brand - Madame Products");

        homePage.get().selectBrand("Mast & Harbour");
        homePage.get().verifyCategoryPageTitle("Brand - Mast & Harbour Products");

        homePage.get().selectBrand("Babyhug");
        homePage.get().verifyCategoryPageTitle("Brand - Babyhug Products");

        homePage.get().selectBrand("Allen Solly Junior");
        homePage.get().verifyCategoryPageTitle("Brand - Allen Solly Junior Products");

        homePage.get().selectBrand("Kookie Kids");
        homePage.get().verifyCategoryPageTitle("Brand - Kookie Kids Products");

        homePage.get().selectBrand("Biba");
        homePage.get().verifyCategoryPageTitle("Brand - Biba Products");
    }
}
