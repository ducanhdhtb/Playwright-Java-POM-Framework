package automation_exercise.tests;

import automation_exercise.BaseTest;
import org.testng.annotations.Test;

public class TC19_ViewCartBrandProducts extends BaseTest {

    @Test(description = "Test Case 19: View & Cart Brand Products",priority = 19)
    public void viewCartBrandProducts() {

        // 1-2 Navigate to Home
        homePage.navigate();

        // 3 Click Products
        homePage.clickProducts();

        // 4 Verify Brands visible
        productsPage.verifyBrandsVisible();

        // 5 Click brand
        homePage.selectBrand("POLO");
        homePage.verifyCategoryPageTitle("Brand - Polo Products");

        homePage.selectBrand("H&M");
        homePage.verifyCategoryPageTitle("Brand - H&M Products");

        homePage.selectBrand("Madame");
        homePage.verifyCategoryPageTitle("Brand - Madame Products");

        homePage.selectBrand("Mast & Harbour");
        homePage.verifyCategoryPageTitle("Brand - Mast & Harbour Products");

        homePage.selectBrand("Babyhug");
        homePage.verifyCategoryPageTitle("Brand - Babyhug Products");

        homePage.selectBrand("Allen Solly Junior");
        homePage.verifyCategoryPageTitle("Brand - Allen Solly Junior Products");

        homePage.selectBrand("Kookie Kids");
        homePage.verifyCategoryPageTitle("Brand - Kookie Kids Products");

        homePage.selectBrand("Biba");
        homePage.verifyCategoryPageTitle("Brand - Biba Products");
    }
}
