package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC21 (site): Add review on product
 */
public class TC27_AddReviewOnProduct extends BaseTest {

    @Test(
            description = "TC27: Add a review on a product and verify success message",
            priority = 27,
            groups = {"regression"}
    )
    @Description("Navigates to product detail, writes a review, submits and verifies success")
    @Step("TC27: Add review on product")
    public void testAddReviewOnProduct() {
        // 1-3. Navigate and verify home page
        homePage.get().navigate(ConfigReader.getProperty("baseUrl"));
        assertThat(getPage()).hasTitle("Automation Exercise");

        // 4. Click Products
        homePage.get().clickProducts();

        // 5. Click View Product for first item
        productsPage.get().clickViewProductOfFirstItem();

        // 6. Verify 'Write Your Review' is visible
        assertThat(getPage().locator("#review-form")).isVisible();
        assertThat(getPage().getByText("Write Your Review")).isVisible();

        // 7. Enter name, email and review
        productDetailPage.get().writeReview(
                "TestReviewer",
                "reviewer_" + System.currentTimeMillis() + "@test.com",
                "Great product! Highly recommended for automation testing."
        );

        // 8. Click Submit button
        productDetailPage.get().submitReview();

        // 9. Verify success message
        productDetailPage.get().verifyReviewSuccess();
    }
}
