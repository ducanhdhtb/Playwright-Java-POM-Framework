package automation_exercise;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class E2EPurchaseTest extends BaseTest {
    @Test
    public void testOrderProduct() {
        homePage.navigate();
        //homePage.clickProductsMenu(); // Bạn thêm hàm này vào HomePage nhé

        productsPage.searchProduct("Blue Top");
        productsPage.addFirstProductToCart();
        productsPage.clickViewCart();

        cartPage.proceedToCheckout();
        cartPage.clickRegisterLoginOnModal();

        // Sau đó thực hiện luồng Register mà bạn đã viết ở RegisterTestPOM...
    }
}