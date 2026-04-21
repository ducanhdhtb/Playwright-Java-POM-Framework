package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * TC_API12: GET User Detail By Email — Extended EP + BVA + Error Guessing
 *
 * GET /api/getUserDetailByEmail?email=xxx
 *
 * EP1  — Existing user → 200 + full user object
 * EP2  — Non-existent email → 404
 * EP3  — Missing email param → 400
 * EP4  — Empty email string → 400
 * EP5  — Invalid email format → 400 or 404
 * EP6  — User detail fields completeness check
 * EP7  — XSS in email param → sanitized
 * EP8  — SQL injection in email param → safe
 * EP9  — Very long email (BVA) → graceful
 * EP10 — POST method → 405
 * EP11 — DELETE method → 405
 * EP12 — Verify all user fields match registration data
 */
public class TC_API12_GetUserDetailExtended extends BaseApiTest {

    // ── EP1: Existing user ────────────────────────────────────────────────────

    @Test(description = "API12-EP1: Existing user returns 200 with full user object",
            groups = {"api", "smoke"})
    @Step("API12-EP1: Get existing user details")
    public void testGetExistingUserDetails() {
        String email = "api12_ep1_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("DetailUser", email, "Password123");
        try {
            ApiResponse r = userApi.getUserByEmail(email);
            assertEquals(r.responseCode(), 200, "Body: " + r.text());
            assertFalse(r.json().path("user").isMissingNode(), "Should have 'user' object");
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP2: Non-existent email ───────────────────────────────────────────────

    @Test(description = "API12-EP2: Non-existent email returns 404",
            groups = {"api", "regression"})
    @Step("API12-EP2: Non-existent email")
    public void testNonExistentEmail() {
        ApiResponse r = userApi.getUserByEmail("ghost_" + System.currentTimeMillis() + "@nowhere.com");
        assertEquals(r.responseCode(), 404, "Body: " + r.text());
    }

    // ── EP3: Missing email param ──────────────────────────────────────────────

    @Test(description = "API12-EP3: Missing email param returns 400",
            groups = {"api", "regression"})
    @Step("API12-EP3: Missing email param")
    public void testMissingEmailParam() {
        ApiResponse r = new ApiResponse(apiClient.get("/api/getUserDetailByEmail"));
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP4: Empty email string ───────────────────────────────────────────────

    @Test(description = "API12-EP4: Empty email string returns 400",
            groups = {"api", "regression", "boundary"})
    @Step("API12-EP4: Empty email string")
    public void testEmptyEmailString() {
        ApiResponse r = userApi.getUserByEmail("");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "Empty email should return 400 or 404. Got: " + r.responseCode());
    }

    // ── EP5: Invalid email format ─────────────────────────────────────────────

    @Test(description = "API12-EP5: Invalid email format returns 400 or 404",
            groups = {"api", "regression", "boundary"})
    @Step("API12-EP5: Invalid email format")
    public void testInvalidEmailFormat() {
        ApiResponse r = userApi.getUserByEmail("notanemail");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "Invalid email should return 400 or 404. Got: " + r.responseCode());
    }

    // ── EP6: User detail fields completeness ─────────────────────────────────

    @Test(description = "API12-EP6: User detail response contains all expected fields",
            groups = {"api", "regression"})
    @Step("API12-EP6: Verify all user fields present")
    public void testUserDetailFieldsCompleteness() {
        String email = "api12_ep6_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("FieldUser", email, "Password123",
                "Mr", "15", "August", "1990",
                "Field", "User", "FieldCorp",
                "456 Field Ave", "Suite 100",
                "United States", "10001", "New York", "New York", "9876543210");
        try {
            ApiResponse r = userApi.getUserByEmail(email);
            assertEquals(r.responseCode(), 200);

            JsonNode user = r.json().path("user");
            // Verify all registration fields are returned
            String[] requiredFields = {"id", "name", "email", "title",
                    "birth_day", "birth_month", "birth_year",
                    "first_name", "last_name", "company",
                    "address1", "address2", "country",
                    "state", "city", "zipcode", "mobile_number"};
            for (String field : requiredFields) {
                assertFalse(user.path(field).isMissingNode(),
                        "User object missing field: " + field);
            }
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP7: XSS in email param ───────────────────────────────────────────────

    @Test(description = "API12-EP7: XSS in email param is sanitized",
            groups = {"api", "regression", "negative"})
    @Step("API12-EP7: XSS in email param")
    public void testXssInEmailParam() {
        ApiResponse r = userApi.getUserByEmail("<script>alert('xss')</script>@test.com");
        assertEquals(r.status(), 200, "Server should not crash");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "XSS email should return 400 or 404. Got: " + r.responseCode());
        assertFalse(r.text().contains("<script>alert"),
                "Response should not echo unescaped XSS");
    }

    // ── EP8: SQL injection in email param ─────────────────────────────────────

    @Test(description = "API12-EP8: SQL injection in email param handled safely",
            groups = {"api", "regression", "negative"})
    @Step("API12-EP8: SQL injection in email param")
    public void testSqlInjectionInEmailParam() {
        ApiResponse r = userApi.getUserByEmail("' OR '1'='1' --");
        assertEquals(r.status(), 200, "Server should not crash");
        // Should NOT return a user (SQL injection not working = good)
        assertNotEquals(r.responseCode(), 200,
                "SQL injection should not return user data");
    }

    // ── EP9: Very long email (BVA) ────────────────────────────────────────────

    @Test(description = "API12-EP9: Very long email (300 chars) handled gracefully",
            groups = {"api", "regression", "boundary"})
    @Step("API12-EP9: 300-char email param")
    public void testVeryLongEmailParam() {
        String longEmail = "a".repeat(290) + "@test.com";
        ApiResponse r = userApi.getUserByEmail(longEmail);
        assertEquals(r.status(), 200, "Server should not crash for long email");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "Long email should return 400 or 404. Got: " + r.responseCode());
    }

    // ── EP10: POST method → 405 ───────────────────────────────────────────────

    @Test(description = "API12-EP10: POST /api/getUserDetailByEmail returns 405",
            groups = {"api", "regression"})
    @Step("API12-EP10: POST method not supported")
    public void testPostMethodNotSupported() {
        ApiResponse r = new ApiResponse(
                apiClient.postForm("/api/getUserDetailByEmail",
                        Map.of("email", "test@test.com")));
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
    }

    // ── EP11: DELETE method → 405 ─────────────────────────────────────────────

    @Test(description = "API12-EP11: DELETE /api/getUserDetailByEmail returns 405",
            groups = {"api", "regression"})
    @Step("API12-EP11: DELETE method not supported")
    public void testDeleteMethodNotSupported() {
        ApiResponse r = new ApiResponse(
                apiClient.deleteForm("/api/getUserDetailByEmail",
                        Map.of("email", "test@test.com")));
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
    }

    // ── EP12: Verify data matches registration ────────────────────────────────

    @Test(description = "API12-EP12: User detail data matches registration input exactly",
            groups = {"api", "regression"})
    @Step("API12-EP12: Data integrity — detail matches registration")
    public void testUserDetailMatchesRegistration() {
        String email = "api12_ep12_" + System.currentTimeMillis() + "@test.com";
        String name = "IntegrityUser";
        String firstName = "Integrity";
        String lastName = "Tester";
        String mobile = "5551234567";

        userApi.createUser(name, email, "Password123",
                "Mrs", "20", "December", "1985",
                firstName, lastName, "IntegrityCorp",
                "789 Integrity Blvd", "",
                "Canada", "M5V 3A8", "Ontario", "Toronto", mobile);
        try {
            ApiResponse r = userApi.getUserByEmail(email);
            assertEquals(r.responseCode(), 200);

            JsonNode user = r.json().path("user");
            assertEquals(user.path("name").asText(), name);
            assertEquals(user.path("email").asText(), email);
            assertEquals(user.path("first_name").asText(), firstName);
            assertEquals(user.path("last_name").asText(), lastName);
            assertEquals(user.path("mobile_number").asText(), mobile);
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }
}
