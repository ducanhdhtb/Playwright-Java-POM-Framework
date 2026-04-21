package automation_exercise.tests;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC1_RegisterUser extends BaseTest {

    @Test(
            priority = 1,
            dataProvider = "tc1DataProvider",
            dataProviderClass = TestData.class
    )
    @Step("TC1: Register a new user")
    public void testRegisterUser(
            String name, String password, String day, String month, String year,
            String firstName, String lastName, String company, String address, String country,
            String state, String city, String zipcode, String mobile
    ) {
        // 1. Navigate to URL and click Signup / Login
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickSignupLogin();

        // 2. Fill signup form with a random email
        String email = "user_" + System.currentTimeMillis() + "@example.com";
        signupLoginPage.fillSignupForm(name, email);
        signupLoginPage.clickSignupButton();

        // 3. Fill account and address details from data provider
        accountPage.fillAccountDetails(password, day, month, year);
        accountPage.fillAddressDetails(firstName, lastName, company, address, country, state, city, zipcode, mobile);
        accountPage.clickCreateAccount();

        // 4. Verify account creation and continue
        assertThat(page.getByText("Account Created!")).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();

        // 5. Verify successful login
        assertThat(page.locator("#header")).containsText("Logged in as " + name);

        // 6. Cleanup: Delete the account to keep the environment clean
        homePage.deleteAccount();
        assertThat(page.getByText("Account Deleted!")).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
    }
}
