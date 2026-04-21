package automation_exercise.tests.ui;

import automation_exercise.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * TC35: Verify review form validation (negative tests)
 *
 * Discovered via MCP exploration: review form has required attributes
 * on name, email, review fields. Browser-level HTML5 validation should
 * prevent submission with empty/invalid fields.
 */
public class TC35_ReviewFormValidation extends BaseTest {

    @Test(
            description = "TC35a: Submit review with empty fields stays on product page",
            priority = 35,
            groups = {"regression", "negative"}
    )
    @Description("Attempts to submit review form with all empty fields — should not navigate away")
    @Step("TC35a: Empty review form submission blocked by validation")
    public void testSubmitEmptyReviewForm() {
        // Navigate to product detail page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.clickViewProductOfFirstItem();

        // Verify review form is visible
        assertThat(page.locator("#review-form")).isVisible();

        // Attempt submit with empty fields (all fields are required)
        page.locator("#button-review").click();

        // Should remain on product detail page (HTML5 validation blocks submit)
        assertThat(page).hasURL(
                java.util.regex.Pattern.compile(".*/product_details/.*"));

        // Review success message should NOT be visible
        assertThat(page.locator("#review-section")).not().isVisible();
    }

    @Test(
            description = "TC35b: Submit review with invalid email format stays on product page",
            priority = 35,
            groups = {"regression", "negative"}
    )
    @Description("Attempts to submit review with invalid email — HTML5 email validation blocks it")
    @Step("TC35b: Invalid email in review form blocked by validation")
    public void testSubmitReviewWithInvalidEmail() {
        // Navigate to product detail page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.clickViewProductOfFirstItem();

        // Fill name and review but use invalid email
        page.locator("#name").fill("TestUser");
        page.locator("#email").fill("not-a-valid-email");
        page.locator("#review").fill("This is a test review");

        // Attempt submit
        page.locator("#button-review").click();

        // Should remain on product detail page
        assertThat(page).hasURL(
                java.util.regex.Pattern.compile(".*/product_details/.*"));

        // Success message should NOT be visible
        assertThat(page.locator("#review-section")).not().isVisible();
    }

    @Test(
            description = "TC35c: Submit valid review shows success message",
            priority = 35,
            groups = {"regression"}
    )
    @Description("Submits a complete valid review and verifies success message appears")
    @Step("TC35c: Valid review submission shows success")
    public void testSubmitValidReview() {
        // Navigate to product detail page
        homePage.navigate(ConfigReader.getProperty("baseUrl"));
        homePage.clickProducts();
        productsPage.clickViewProductOfFirstItem();

        // Fill all required fields with valid data
        productDetailPage.writeReview(
                "ValidReviewer",
                "valid_" + System.currentTimeMillis() + "@test.com",
                "Excellent product! Very good quality and fast delivery."
        );

        // Submit
        productDetailPage.submitReview();

        // Verify success message
        productDetailPage.verifyReviewSuccess();
    }
}
