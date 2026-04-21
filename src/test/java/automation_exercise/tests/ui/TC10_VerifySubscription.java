package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.TestData;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TC10_VerifySubscription extends BaseTest {

    @Test(
            priority = 10,
            dataProvider = "tc10DataProvider",
            dataProviderClass = TestData.class,
            groups = {"smoke"}
    )
    @Step("TC10: Verify subscription on home page")
    public void verifySubscription(String email) {
        // 1 & 2. Launch browser and Navigate to URL
        homePage.navigate(ConfigReader.getProperty("baseUrl"));

        // 3. Verify that home page is visible successfully
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Scroll down to footer
        homePage.scrollToFooter();

        // 5. Verify text 'SUBSCRIPTION'
        homePage.verifySubscriptionTitleIsVisible();

        // 6. Enter email address and click arrow button
        homePage.subscribe(email);

        // 7. Verify success message is visible
        homePage.verifySuccessMessage();
    }
}
