package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC4_LogoutTest extends BaseTest {

    @Test(priority = 4, groups = {"regression"})
    @Step("TC4: Logout user")
    public void testLogoutUser() {
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));

        createLoggedInUser("\u004e\u0067\u0075\u0079\u1ec5\u006e \u0110\u1ee9c \u0041\u006e\u0068", "ducanh123");

        assertThat(getPage()).hasTitle("Automation Exercise");

        homePage.get().clickLogout();

        assertThat(getPage()).hasURL("https://automationexercise.com/login");
        assertThat(getPage().getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Login to your account"))).isVisible();
    }
}
