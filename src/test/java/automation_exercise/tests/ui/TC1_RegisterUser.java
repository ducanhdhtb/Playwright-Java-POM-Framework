package automation_exercise.tests.ui;

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
            dataProviderClass = TestData.class,
            groups = {"smoke"}
    )
    @Step("TC1: Register a new user")
    public void testRegisterUser(
            String name, String password, String day, String month, String year,
            String firstName, String lastName, String company, String address, String country,
            String state, String city, String zipcode, String mobile
    ) {
        // 1. Navigate to URL and click Signup / Login
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        homePage.get().clickSignupLogin();

        // 2. Fill signup form with a random email
        String email = "user_" + System.currentTimeMillis() + "@example.com";
        signupLoginPage.get().fillSignupForm(name, email);
        signupLoginPage.get().clickSignupButton();

        // 3. Fill account and address details from data provider
        accountPage.get().fillAccountDetails(password, day, month, year);
        accountPage.get().fillAddressDetails(firstName, lastName, company, address, country, state, city, zipcode, mobile);
        accountPage.get().clickCreateAccount();

        // 4. Verify account creation and continue
        assertThat(getPage().getByText("Account Created!")).isVisible();
        getPage().getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();

        // 5. Verify successful login
        assertThat(getPage().locator("#header")).containsText("Logged in as " + name);

        // 6. Cleanup: Delete the account to keep the environment clean
        homePage.get().deleteAccount();
        assertThat(getPage().getByText("Account Deleted!")).isVisible();
        getPage().getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
    }
}
