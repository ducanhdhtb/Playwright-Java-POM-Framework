package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * API Test: Delete user account via DELETE /api/deleteAccount
 */
public class TC_API04_DeleteUserAccount extends BaseApiTest {

    @Test(
            description = "API: Delete an existing user account returns 200",
            groups = {"api", "smoke"}
    )
    @Description("DELETE /api/deleteAccount with valid credentials → responseCode 200")
    @Step("API TC04a: Delete existing user via API")
    public void testDeleteExistingUser() {
        String email = "api_del_" + System.currentTimeMillis() + "@testmail.com";
        String password = "Password123";

        // Setup: create user
        ApiResponse create = userApi.createUser("DeleteMe", email, password);
        assertEquals(create.responseCode(), 201, "Setup: user creation failed");

        // Delete user
        ApiResponse response = userApi.deleteUser(email, password);
        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 200,
                "Delete should return responseCode 200. Body: " + response.text());
        assertTrue(response.message().toLowerCase().contains("account deleted"),
                "Expected 'Account deleted' in message but got: " + response.message());

        // Verify user no longer exists
        ApiResponse verify = userApi.getUserByEmail(email);
        assertEquals(verify.responseCode(), 404,
                "Deleted user should return 404 on lookup. Body: " + verify.text());
    }

    @Test(
            description = "API: Delete non-existent user returns 404",
            groups = {"api", "regression"}
    )
    @Description("DELETE /api/deleteAccount with unknown email → responseCode 404")
    @Step("API TC04b: Delete non-existent user")
    public void testDeleteNonExistentUser() {
        ApiResponse response = userApi.deleteUser(
                "ghost_" + System.currentTimeMillis() + "@nowhere.com",
                "Password123"
        );
        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 404,
                "Non-existent user delete should return 404. Body: " + response.text());
    }
}
