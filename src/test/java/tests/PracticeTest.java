package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.PracticePage;
import com.microsoft.playwright.options.LoadState; // Import thêm cái này

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PracticeTest extends BaseTest {

    @Test
    public void testFillPracticeForm() {
        PracticePage practicePage = new PracticePage(page);
        page.navigate("file:///Users/macos/Desktop/PlaywrightJavaFramework/src/test/java/tests/practice.html");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        practicePage.fillInformation("Nguyễn Đức Anh", "dev");
        practicePage.clickSubmit();

        assertThat(page.locator("#message")).isVisible();
    }
}