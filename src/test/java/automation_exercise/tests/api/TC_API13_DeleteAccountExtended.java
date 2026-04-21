package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * TC_API13: Delete Account — Extended EP + BVA + Error Guessing
 *
 * DELETE /api/deleteAccount
 *
 * EP1  — Valid email + correct password → 200
 * EP2  — Valid email + wrong password → 404
 * EP3  — Non-existent email → 404
 * EP4  — Missing email param → 400
 * EP5  — Missing password param → 400
 * EP6  — Both params missing → 400
 * EP7  — Empty email string → 400
 * EP8  — Empty password string → 400 or 404
 * EP9  — Delete already-deleted user → 404
 * EP10 — Verify user no longer accessible after delete
 * EP11 — POST method → 405
 * EP12 — GET method → 405
 * EP13 — XSS in email → safe
 * EP14 — SQL injection in email → safe
 */
public class TC_API13_DeleteAccountExtended extends BaseApiTest {

    // ── EP1: Valid delete ─────────────────────────────────────────────────────

    @Test(description = "API13-EP1: Valid credentials delete account and return 200",
            groups = {"api", "smoke"})
    @Step("API13-EP1: Valid delete")
    public void testValidDelete() {
        String email = "api13_ep1_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("DeleteEP1", email, "Password123");

        ApiResponse r = userApi.deleteUser(email, "Password123");
        assertEquals(r.responseCode(), 200, "Body: " + r.text());
        assertTrue(r.message().toLowerCase().contains("deleted"),
                "Message: " + r.message());
    }

    // ── EP2: Wrong password ───────────────────────────────────────────────────

    @Test(description = "API13-EP2: Wrong password returns 404",
            groups = {"api", "regression"})
    @Step("API13-EP2: Wrong password on delete")
    public void testDeleteWithWrongPassword() {
        String email = "api13_ep2_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("DeleteEP2", email, "CorrectPass");
        try {
            ApiResponse r = userApi.deleteUser(email, "WrongPass999");
            assertEquals(r.responseCode(), 404, "Body: " + r.text());
        } finally {
            userApi.teardownUser(email, "CorrectPass");
        }
    }

    // ── EP3: Non-existent email ───────────────────────────────────────────────

    @Test(description = "API13-EP3: Non-existent email returns 404",
            groups = {"api", "regression"})
    @Step("API13-EP3: Non-existent email delete")
    public void testDeleteNonExistentEmail() {
        ApiResponse r = userApi.deleteUser(
                "ghost_" + System.currentTimeMillis() + "@nowhere.com", "Pass123");
        assertEquals(r.responseCode(), 404, "Body: " + r.text());
    }

    // ── EP4: Missing email param ──────────────────────────────────────────────

    @Test(description = "API13-EP4: Missing email param returns 400",
            groups = {"api", "regression"})
    @Step("API13-EP4: Missing email param")
    public void testMissingEmailParam() {
        ApiResponse r = new ApiResponse(
                apiClient.deleteForm("/api/deleteAccount", Map.of("password", "Pass123")));
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP5: Missing password param ───────────────────────────────────────────

    @Test(description = "API13-EP5: Missing password param returns 400",
            groups = {"api", "regression"})
    @Step("API13-EP5: Missing password param")
    public void testMissingPasswordParam() {
        ApiResponse r = new ApiResponse(
                apiClient.deleteForm("/api/deleteAccount",
                        Map.of("email", "test@test.com")));
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP6: Both params missing ──────────────────────────────────────────────

    @Test(description = "API13-EP6: Both params missing returns 400",
            groups = {"api", "regression"})
    @Step("API13-EP6: Both params missing")
    public void testBothParamsMissing() {
        ApiResponse r = new ApiResponse(
                apiClient.deleteForm("/api/deleteAccount", Map.of()));
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP7: Empty email string ───────────────────────────────────────────────

    @Test(description = "API13-EP7: Empty email string returns 400",
            groups = {"api", "regression", "boundary"})
    @Step("API13-EP7: Empty email string")
    public void testEmptyEmailString() {
        ApiResponse r = userApi.deleteUser("", "Password123");
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP8: Empty password string ────────────────────────────────────────────

    @Test(description = "API13-EP8: Empty password string returns 400 or 404",
            groups = {"api", "regression", "boundary"})
    @Step("API13-EP8: Empty password string")
    public void testEmptyPasswordString() {
        ApiResponse r = userApi.deleteUser("test@test.com", "");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "Empty password should return 400 or 404. Got: " + r.responseCode());
    }

    // ── EP9: Delete already-deleted user ─────────────────────────────────────

    @Test(description = "API13-EP9: Deleting already-deleted user returns 404",
            groups = {"api", "regression"})
    @Step("API13-EP9: Double delete returns 404")
    public void testDoubleDelete() {
        String email = "api13_ep9_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("DoubleDelete", email, "Password123");

        // First delete
        ApiResponse first = userApi.deleteUser(email, "Password123");
        assertEquals(first.responseCode(), 200, "First delete should succeed");

        // Second delete — user no longer exists
        ApiResponse second = userApi.deleteUser(email, "Password123");
        assertEquals(second.responseCode(), 404, "Second delete should return 404");
    }

    // ── EP10: Verify user inaccessible after delete ───────────────────────────

    @Test(description = "API13-EP10: Deleted user cannot be retrieved via getUserDetailByEmail",
            groups = {"api", "regression"})
    @Step("API13-EP10: User inaccessible after delete")
    public void testDeletedUserInaccessible() {
        String email = "api13_ep10_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("InaccessUser", email, "Password123");

        // Delete
        userApi.deleteUser(email, "Password123");

        // Verify cannot retrieve
        ApiResponse r = userApi.getUserByEmail(email);
        assertEquals(r.responseCode(), 404,
                "Deleted user should return 404 on GET. Body: " + r.text());

        // Verify cannot login
        ApiResponse login = userApi.verifyLogin(email, "Password123");
        assertEquals(login.responseCode(), 404,
                "Deleted user should not be able to login");
    }

    // ── EP11: POST method → 405 ───────────────────────────────────────────────

    @Test(description = "API13-EP11: POST /api/deleteAccount returns 405",
            groups = {"api", "regression"})
    @Step("API13-EP11: POST method not supported")
    public void testPostMethodNotSupported() {
        ApiResponse r = new ApiResponse(
                apiClient.postForm("/api/deleteAccount",
                        Map.of("email", "test@test.com", "password", "pass")));
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
    }

    // ── EP12: GET method → 405 ───────────────────────────────────────────────

    @Test(description = "API13-EP12: GET /api/deleteAccount returns 405",
            groups = {"api", "regression"})
    @Step("API13-EP12: GET method not supported")
    public void testGetMethodNotSupported() {
        ApiResponse r = new ApiResponse(
                apiClient.get("/api/deleteAccount",
                        Map.of("email", "test@test.com", "password", "pass")));
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
    }

    // ── EP13: XSS in email ────────────────────────────────────────────────────

    @Test(description = "API13-EP13: XSS in email param handled safely",
            groups = {"api", "regression", "negative"})
    @Step("API13-EP13: XSS in email")
    public void testXssInEmail() {
        ApiResponse r = userApi.deleteUser("<script>alert('xss')</script>@test.com", "Pass");
        assertEquals(r.status(), 200, "Server should not crash");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "XSS email should return 400 or 404. Got: " + r.responseCode());
    }

    // ── EP14: SQL injection in email ──────────────────────────────────────────

    @Test(description = "API13-EP14: SQL injection in email handled safely",
            groups = {"api", "regression", "negative"})
    @Step("API13-EP14: SQL injection in email")
    public void testSqlInjectionInEmail() {
        ApiResponse r = userApi.deleteUser("' OR '1'='1' --", "' OR '1'='1'");
        assertEquals(r.status(), 200, "Server should not crash");
        // Should NOT return 200 (mass delete via SQL injection)
        assertNotEquals(r.responseCode(), 200,
                "SQL injection should not delete accounts. Got: " + r.responseCode());
    }
}
