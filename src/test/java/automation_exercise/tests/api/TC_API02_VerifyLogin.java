package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * API Test: Verify login credentials via POST /api/verifyLogin
 */
public class TC_API02_VerifyLogin extends BaseApiTest {

    @Test(
            description = "API: Verify login with valid credentials returns 200",
            groups = {"api", "smoke"}
    )
    @Description("POST /api/verifyLogin with correct credentials → responseCode 200")
    @Step("API TC02a: Valid login credentials")
    public void testVerifyLoginWithValidCredentials() {
        String email = "api_login_" + System.currentTimeMillis() + "@testmail.com";
        String password = "Password123";

        // Setup: create user via API
        ApiResponse create = userApi.createUser("LoginUser", email, password);
        assertEquals(create.responseCode(), 201, "Setup: user creation failed");

        try {
            // Verify login
            ApiResponse response = userApi.verifyLogin(email, password);
            assertEquals(response.status(), 200, "HTTP status should be 200");
            assertEquals(response.responseCode(), 200,
                    "Valid credentials should return responseCode 200. Body: " + response.text());
            assertTrue(response.message().toLowerCase().contains("user exists"),
                    "Expected 'User exists' in message but got: " + response.message());
        } finally {
            userApi.teardownUser(email, password);
        }
    }

    @Test(
            description = "API: Verify login with invalid credentials returns 404",
            groups = {"api", "regression"}
    )
    @Description("POST /api/verifyLogin with wrong password → responseCode 404")
    @Step("API TC02b: Invalid login credentials")
    public void testVerifyLoginWithInvalidCredentials() {
        ApiResponse response = userApi.verifyLogin(
                "nonexistent_" + System.currentTimeMillis() + "@nowhere.com",
                "WrongPassword!"
        );
        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 404,
                "Invalid credentials should return responseCode 404. Body: " + response.text());
    }

    @Test(
            description = "API: Verify login without email parameter returns 400",
            groups = {"api", "regression"}
    )
    @Description("POST /api/verifyLogin with missing email → responseCode 400")
    @Step("API TC02c: Missing email parameter")
    public void testVerifyLoginWithMissingEmail() {
        // Omit the 'email' parameter entirely to trigger missing-parameter error (responseCode 400).
        ApiResponse response = new ApiResponse(apiClient.postForm("/api/verifyLogin",
                Map.of("password", "Password123")));
        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 400,
                "Missing email should return responseCode 400. Body: " + response.text());
    }
}
