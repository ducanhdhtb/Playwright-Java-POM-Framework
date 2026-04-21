package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * API Test: Fetch user details via GET /api/getUserDetailByEmail
 */
public class TC_API03_GetUserDetails extends BaseApiTest {

    @Test(
            description = "API: Get user details by email returns correct user data",
            groups = {"api", "smoke"}
    )
    @Description("GET /api/getUserDetailByEmail with valid email → responseCode 200 + user fields present")
    @Step("API TC03a: Get user details for existing user")
    public void testGetUserDetailsByEmail() {
        String name = "DetailUser";
        String email = "api_detail_" + System.currentTimeMillis() + "@testmail.com";
        String password = "Password123";

        // Setup
        ApiResponse create = userApi.createUser(name, email, password);
        assertEquals(create.responseCode(), 201, "Setup: user creation failed");

        try {
            // Fetch user details
            ApiResponse response = userApi.getUserByEmail(email);
            assertEquals(response.status(), 200, "HTTP status should be 200");
            assertEquals(response.responseCode(), 200,
                    "Should return responseCode 200. Body: " + response.text());

            // Verify user fields
            JsonNode user = response.json().path("user");
            assertFalse(user.isMissingNode(), "Response should contain 'user' object");
            assertEquals(user.path("email").asText(), email,
                    "Returned email should match the created user");
            assertEquals(user.path("name").asText(), name,
                    "Returned name should match the created user");
        } finally {
            userApi.teardownUser(email, password);
        }
    }

    @Test(
            description = "API: Get user details for non-existent email returns 404",
            groups = {"api", "regression"}
    )
    @Description("GET /api/getUserDetailByEmail with unknown email → responseCode 404")
    @Step("API TC03b: Get user details for non-existent email")
    public void testGetUserDetailsForNonExistentEmail() {
        ApiResponse response = userApi.getUserByEmail(
                "ghost_" + System.currentTimeMillis() + "@nowhere.com");
        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 404,
                "Non-existent email should return responseCode 404. Body: " + response.text());
    }
}
