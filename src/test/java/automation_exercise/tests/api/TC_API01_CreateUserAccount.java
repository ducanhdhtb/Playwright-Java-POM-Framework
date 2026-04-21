package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * API Test: Create a new user account via POST /api/createAccount
 * and verify the response code + message.
 */
public class TC_API01_CreateUserAccount extends BaseApiTest {

    @Test(
            description = "API: Create a new user account and verify response",
            groups = {"api", "smoke"}
    )
    @Description("Calls POST /api/createAccount with valid data and asserts responseCode=201")
    @Step("API TC01: Create user account via API")
    public void testCreateUserAccountViaApi() {
        String email = "api_create_" + System.currentTimeMillis() + "@testmail.com";
        String password = "Password123";
        String name = "ApiTestUser";

        // 1. Create user via API
        ApiResponse response = userApi.createUser(name, email, password);

        // 2. Verify HTTP status is 200 (API wraps result in body)
        assertEquals(response.status(), 200, "HTTP status should be 200");

        // 3. Verify API response code is 201 (User created)
        assertEquals(response.responseCode(), 201,
                "API responseCode should be 201 (User created). Body: " + response.text());

        // 4. Verify message
        assertTrue(response.message().toLowerCase().contains("user created"),
                "Expected 'User created' in message but got: " + response.message());

        // 5. Cleanup
        userApi.teardownUser(email, password);
    }

    @Test(
            description = "API: Create user with already-registered email returns 400",
            groups = {"api", "regression"}
    )
    @Description("Calls POST /api/createAccount with a duplicate email and asserts responseCode=400")
    @Step("API TC01b: Duplicate email returns 400")
    public void testCreateUserWithDuplicateEmailReturns400() {
        String email = "api_dup_" + System.currentTimeMillis() + "@testmail.com";
        String password = "Password123";

        // 1. Create user first time
        ApiResponse first = userApi.createUser("DupUser", email, password);
        assertEquals(first.responseCode(), 201, "First creation should succeed");

        try {
            // 2. Attempt to create again with same email
            ApiResponse second = userApi.createUser("DupUser2", email, password);
            assertEquals(second.responseCode(), 400,
                    "Duplicate email should return 400. Body: " + second.text());
        } finally {
            // 3. Cleanup regardless
            userApi.teardownUser(email, password);
        }
    }
}
