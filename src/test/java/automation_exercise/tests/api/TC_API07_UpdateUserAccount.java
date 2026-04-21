package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * API Tests: Update User Account + DELETE verifyLogin
 *
 * API 9:  DELETE /api/verifyLogin    → 405 method not supported
 * API 13: PUT /api/updateAccount     → 200 user updated
 */
public class TC_API07_UpdateUserAccount extends BaseApiTest {

    // ── API 9: DELETE /api/verifyLogin → 405 ─────────────────────────────────

    @Test(
            description = "API9: DELETE /api/verifyLogin returns 405 method not supported",
            groups = {"api", "regression"}
    )
    @Description("DELETE /api/verifyLogin → responseCode 405")
    @Step("API9: DELETE to verifyLogin expects 405")
    public void testDeleteVerifyLoginReturns405() {
        ApiResponse response = userApi.deleteVerifyLogin();

        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 405,
                "DELETE to verifyLogin should return 405. Body: " + response.text());
        assertTrue(response.message().toLowerCase().contains("not supported"),
                "Message should contain 'not supported'. Got: " + response.message());
    }

    // ── API 13: PUT /api/updateAccount → 200 ─────────────────────────────────

    @Test(
            description = "API13: PUT /api/updateAccount updates user and returns 200",
            groups = {"api", "smoke"}
    )
    @Description("PUT /api/updateAccount with valid data → responseCode 200, user updated")
    @Step("API13: Update user account via API")
    public void testUpdateUserAccount() {
        String email = "api_update_" + System.currentTimeMillis() + "@testmail.com";
        String password = "Password123";

        // Setup: create user first
        ApiResponse create = userApi.createUser("UpdateMe", email, password);
        assertEquals(create.responseCode(), 201, "Setup: user creation failed");

        try {
            // Update user
            ApiResponse response = userApi.updateUser(
                    "UpdatedName", email, password,
                    "Mrs", "15", "August", "1992",
                    "Updated", "User", "NewCorp",
                    "456 Updated Ave", "",
                    "Canada", "M5V 3A8", "Ontario", "Toronto", "9876543210"
            );

            assertEquals(response.status(), 200, "HTTP status should be 200");
            assertEquals(response.responseCode(), 200,
                    "Update should return 200. Body: " + response.text());
            assertTrue(response.message().toLowerCase().contains("updated"),
                    "Message should contain 'updated'. Got: " + response.message());

            // Verify updated data via GET
            ApiResponse details = userApi.getUserByEmail(email);
            assertEquals(details.responseCode(), 200, "Should fetch updated user");
            assertEquals(details.json().path("user").path("name").asText(), "UpdatedName",
                    "Name should be updated");

        } finally {
            userApi.teardownUser(email, password);
        }
    }

    // ── API 13b: Update non-existent user → 404 ──────────────────────────────

    @Test(
            description = "API13b: PUT /api/updateAccount for non-existent user returns 404",
            groups = {"api", "regression"}
    )
    @Description("PUT /api/updateAccount with unknown email → responseCode 404")
    @Step("API13b: Update non-existent user expects 404")
    public void testUpdateNonExistentUser() {
        ApiResponse response = userApi.updateUser(
                "Ghost", "ghost_" + System.currentTimeMillis() + "@nowhere.com", "Pass123",
                "Mr", "1", "January", "2000",
                "Ghost", "User", "",
                "123 Ghost St", "",
                "United States", "00000", "Unknown", "Unknown", "0000000000"
        );

        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 404,
                "Non-existent user update should return 404. Body: " + response.text());
    }
}
