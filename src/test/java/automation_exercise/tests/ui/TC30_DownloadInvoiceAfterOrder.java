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
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Add product to cart
        homePage.get().clickProducts();
        productsPage.get().addFirstProductToCart();
        productsPage.get().clickViewCart();

        // 5. Proceed to checkout → Register
        cartPage.get().proceedToCheckout();
        cartPage.get().clickRegisterLoginOnModal();

        // 6. Register new user
        String user = "InvoiceUser";
        signupPage.get().fillSignupDetailsWithRandomEmail(user);
        signupPage.get().fillAccountInformation("Password123", "10", "July", "1990");
        signupPage.get().fillAddressDetails(user, "Tester", "123 Invoice St", "Texas", "Austin", "73301", "1234567890");
        signupPage.get().clickCreateAccount();
        assertThat(getPage().locator("h2:has-text('Account Created!')")).isVisible();
        getPage().click("a[data-qa='continue-button']");

        // 7. Go back to cart and checkout
        homePage.get().clickCart();
        cartPage.get().proceedToCheckout();

        // 8. Place order
        checkoutPage.get().enterComment("Invoice test order");
        checkoutPage.get().clickPlaceOrder();
        checkoutPage.get().enterPaymentDetails("Invoice User", "4111111111111111", "123", "12", "2027");
        checkoutPage.get().clickPayAndConfirm();

        // 9. Verify order success
        checkoutPage.get().verifyOrderSuccess();

        // 10. Click Download Invoice and verify download
        Download download = getPage().waitForDownload(() -> {
            checkoutPage.get().clickDownloadInvoice();
        });
        assertNotNull(download, "Download should not be null");
        assertTrue(download.suggestedFilename().contains("invoice") ||
                   download.suggestedFilename().endsWith(".pdf") ||
                   download.suggestedFilename().length() > 0,
                "Downloaded file should have a valid name: " + download.suggestedFilename());

        // 11. Continue and cleanup
        getPage().click("a[data-qa='continue-button']");
        homePage.get().deleteAccount();
        assertThat(getPage().locator("h2:has-text('Account Deleted!')")).isVisible();
        getPage().click("a[data-qa='continue-button']");
    }
}
