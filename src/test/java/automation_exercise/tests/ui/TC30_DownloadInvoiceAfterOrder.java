package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Download;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * TC24 (site): Download Invoice after purchase order
 */
public class TC30_DownloadInvoiceAfterOrder extends BaseTest {

    @Test(
            description = "TC30: Download invoice after placing an order",
            priority = 30,
            groups = {"regression", "e2e"}
    )
    @Description("Places order, clicks Download Invoice, verifies file is downloaded")
    @Step("TC30: Download invoice after purchase")
    public void testDownloadInvoiceAfterOrder() {
        // 1-3. Navigate and verify home page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(page).hasTitle("Automation Exercise");

        // 4. Add product to cart
        homePage.clickProducts();
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        // 5. Proceed to checkout → Register
        cartPage.proceedToCheckout();
        cartPage.clickRegisterLoginOnModal();

        // 6. Register new user
        String user = "InvoiceUser";
        signupPage.fillSignupDetailsWithRandomEmail(user);
        signupPage.fillAccountInformation("Password123", "10", "July", "1990");
        signupPage.fillAddressDetails(user, "Tester", "123 Invoice St", "Texas", "Austin", "73301", "1234567890");
        signupPage.clickCreateAccount();
        assertThat(page.locator("h2:has-text('Account Created!')")).isVisible();
        page.click("a[data-qa='continue-button']");

        // 7. Go back to cart and checkout
        homePage.clickCart();
        cartPage.proceedToCheckout();

        // 8. Place order
        checkoutPage.enterComment("Invoice test order");
        checkoutPage.clickPlaceOrder();
        checkoutPage.enterPaymentDetails("Invoice User", "4111111111111111", "123", "12", "2027");
        checkoutPage.clickPayAndConfirm();

        // 9. Verify order success
        checkoutPage.verifyOrderSuccess();

        // 10. Click Download Invoice and verify download
        Download download = page.waitForDownload(() -> {
            checkoutPage.clickDownloadInvoice();
        });
        assertNotNull(download, "Download should not be null");
        assertTrue(download.suggestedFilename().contains("invoice") ||
                   download.suggestedFilename().endsWith(".pdf") ||
                   download.suggestedFilename().length() > 0,
                "Downloaded file should have a valid name: " + download.suggestedFilename());

        // 11. Continue and cleanup
        page.click("a[data-qa='continue-button']");
        homePage.deleteAccount();
        assertThat(page.locator("h2:has-text('Account Deleted!')")).isVisible();
        page.click("a[data-qa='continue-button']");
    }
}
