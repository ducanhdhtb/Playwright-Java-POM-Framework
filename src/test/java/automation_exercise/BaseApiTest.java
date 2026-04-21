package automation_exercise;

import api.ApiClient;
import api.UserApiHelper;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Base class for API-only tests.
 *
 * Keeps API tests fast and CI-friendly by avoiding browser launch.
 */
public class BaseApiTest {
    protected Playwright playwright;
    protected ApiClient apiClient;
    protected UserApiHelper userApi;

    @BeforeClass(alwaysRun = true)
    public void setupApi() {
        playwright = Playwright.create();
        apiClient = new ApiClient(playwright);
        userApi = new UserApiHelper(apiClient);
    }

    @AfterClass(alwaysRun = true)
    public void teardownApi() {
        if (apiClient != null) {
            apiClient.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}

