package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;
import utils.ExcelReader;

/**
 * TC_API11: Verify Login — Extended EP + BVA + Error Guessing
 *
 * POST /api/verifyLogin
 *
 * Covers all partitions:
 *   EP1  — Valid email + valid password → 200
 *   EP2  — Valid email + wrong password → 404
 *   EP3  — Non-existent email → 404
 *   EP4  — Missing email param → 400
 *   EP5  — Missing password param → 400
 *   EP6  — Both params missing → 400
 *   EP7  — Empty email string → 400
 *   EP8  — Empty password string → 404 or 400
 *   EP9  — Invalid email format → 404 or 400
 *   EP10 — Case sensitivity: EMAIL vs email
 *   EP11 — XSS in email → handled safely
 *   EP12 — SQL injection in password → handled safely
 *   EP13 — Very long email (BVA) → handled gracefully
 *   EP14 — Very long password (BVA) → handled gracefully
 *   EP15 — DELETE method → 405
 *   EP16 — GET method → 405
 */
public class TC_API11_VerifyLoginExtended extends BaseApiTest {

    // ── EP1: Valid credentials ────────────────────────────────────────────────

    @Test(description = "API11-EP1: Valid email+password returns 200 User exists",
            groups = {"api", "smoke"})
    @Description("EP1: Correct credentials → responseCode 200, message 'User exists'")
    @Step("API11-EP1: Valid login")
    public void testValidLogin() {
        String email = "api11_valid_" + System.currentTimeMillis() + "@test.com";
        String password = "Password123";
        userApi.createUser("EP1User", email, password);
        try {
            ApiResponse r = userApi.verifyLogin(email, password);
            assertEquals(r.responseCode(), 200, "Body: " + r.text());
            assertTrue(r.message().toLowerCase().contains("user exists"),
                    "Message: " + r.message());
        } finally {
            userApi.teardownUser(email, password);
        }
    }

    // ── EP2: Wrong password ───────────────────────────────────────────────────

    @Test(description = "API11-EP2: Valid email + wrong password returns 404",
            groups = {"api", "regression"})
    @Step("API11-EP2: Wrong password")
    public void testWrongPassword() {
        String email = "api11_wp_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("EP2User", email, "CorrectPass");
        try {
            ApiResponse r = userApi.verifyLogin(email, "WrongPass999");
            assertEquals(r.responseCode(), 404, "Body: " + r.text());
        } finally {
            userApi.teardownUser(email, "CorrectPass");
        }
    }

    // ── EP3: Non-existent email ───────────────────────────────────────────────

    @Test(description = "API11-EP3: Non-existent email returns 404",
            groups = {"api", "regression"})
    @Step("API11-EP3: Non-existent email")
    public void testNonExistentEmail() {
        ApiResponse r = userApi.verifyLogin(
                "ghost_" + System.currentTimeMillis() + "@nowhere.com", "Pass123");
        assertEquals(r.responseCode(), 404, "Body: " + r.text());
        assertTrue(r.message().toLowerCase().contains("not found") ||
                   r.message().toLowerCase().contains("user"),
                "Message: " + r.message());
    }

    // ── EP4: Missing email param ──────────────────────────────────────────────

    @Test(description = "API11-EP4: Missing email param returns 400",
            groups = {"api", "regression"})
    @Step("API11-EP4: Missing email param")
    public void testMissingEmailParam() {
        ApiResponse r = new ApiResponse(
                apiClient.postForm("/api/verifyLogin", Map.of("password", "Pass123")));
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
        assertTrue(r.message().toLowerCase().contains("missing") ||
                   r.message().toLowerCase().contains("bad request"),
                "Message: " + r.message());
    }

    // ── EP5: Missing password param ───────────────────────────────────────────

    @Test(description = "API11-EP5: Missing password param returns 400",
            groups = {"api", "regression"})
    @Step("API11-EP5: Missing password param")
    public void testMissingPasswordParam() {
        ApiResponse r = new ApiResponse(
                apiClient.postForm("/api/verifyLogin",
                        Map.of("email", "test@test.com")));
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP6: Both params missing ──────────────────────────────────────────────

    @Test(description = "API11-EP6: Both params missing returns 400",
            groups = {"api", "regression"})
    @Step("API11-EP6: Both params missing")
    public void testBothParamsMissing() {
        ApiResponse r = new ApiResponse(
                apiClient.postForm("/api/verifyLogin", Map.of()));
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP7: Empty email string ───────────────────────────────────────────────

    @Test(description = "API11-EP7: Empty email string returns 400",
            groups = {"api", "regression", "boundary"})
    @Step("API11-EP7: Empty email string")
    public void testEmptyEmailString() {
        ApiResponse r = userApi.verifyLogin("", "Password123");
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP8: Empty password string ────────────────────────────────────────────

    @Test(description = "API11-EP8: Empty password string returns 400 or 404",
            groups = {"api", "regression", "boundary"})
    @Step("API11-EP8: Empty password string")
    public void testEmptyPasswordString() {
        ApiResponse r = userApi.verifyLogin("test@test.com", "");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "Empty password should return 400 or 404. Got: " + r.responseCode());
    }

    // ── EP9: Invalid email format ─────────────────────────────────────────────

    @DataProvider(name = "invalidEmailFormats")
    public Object[][] invalidEmailFormats() {
        return ExcelReader.getTestData("src/test/resources/AutomationTestData.xlsx", "invalidEmailFormats");
    }

    @Test(description = "API11-EP9: Invalid email format returns 400 or 404",
            dataProvider = "invalidEmailFormats",
            groups = {"api", "regression", "boundary"})
    @Step("API11-EP9: Invalid email format '{0}'")
    public void testInvalidEmailFormat(String email) {
        ApiResponse r = userApi.verifyLogin(email, "Password123");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "Invalid email '" + email + "' should return 400 or 404. Got: " + r.responseCode());
    }

    // ── EP10: Case sensitivity ────────────────────────────────────────────────

    @Test(description = "API11-EP10: Email case sensitivity check",
            groups = {"api", "regression"})
    @Step("API11-EP10: Email case sensitivity")
    public void testEmailCaseSensitivity() {
        String email = "case_" + System.currentTimeMillis() + "@test.com";
        String password = "Password123";
        userApi.createUser("CaseUser", email, password);
        try {
            // Try uppercase email
            ApiResponse r = userApi.verifyLogin(email.toUpperCase(), password);
            // Server may or may not be case-sensitive — just verify no crash
            assertTrue(r.responseCode() == 200 || r.responseCode() == 404,
                    "Case variant should return 200 or 404. Got: " + r.responseCode());
        } finally {
            userApi.teardownUser(email, password);
        }
    }

    // ── EP11: XSS in email ────────────────────────────────────────────────────

    @Test(description = "API11-EP11: XSS payload in email handled safely",
            groups = {"api", "regression", "negative"})
    @Step("API11-EP11: XSS in email")
    public void testXssInEmail() {
        ApiResponse r = userApi.verifyLogin("<script>alert('xss')</script>@test.com", "Pass");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "XSS email should return 400 or 404. Got: " + r.responseCode());
        // Response body should not contain unescaped script tag
        assertFalse(r.text().contains("<script>alert"),
                "Response should not echo back unescaped XSS payload");
    }

    // ── EP12: SQL injection in password ──────────────────────────────────────

    @Test(description = "API11-EP12: SQL injection in password handled safely",
            groups = {"api", "regression", "negative"})
    @Step("API11-EP12: SQL injection in password")
    public void testSqlInjectionInPassword() {
        ApiResponse r = userApi.verifyLogin("test@test.com", "' OR '1'='1");
        // Should return 404 (user not found) not 200 (SQL injection bypassed auth)
        assertNotEquals(r.responseCode(), 200,
                "SQL injection should NOT return 200 (auth bypass). Got: " + r.responseCode());
    }

    // ── EP13: Very long email (BVA) ───────────────────────────────────────────

    @Test(description = "API11-EP13: Very long email (300 chars) handled gracefully",
            groups = {"api", "regression", "boundary"})
    @Step("API11-EP13: 300-char email")
    public void testVeryLongEmail() {
        String longEmail = "a".repeat(290) + "@test.com";
        ApiResponse r = userApi.verifyLogin(longEmail, "Password123");
        assertEquals(r.status(), 200, "Server should not crash for long email");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "Long email should return 400 or 404. Got: " + r.responseCode());
    }

    // ── EP14: Very long password (BVA) ────────────────────────────────────────

    @Test(description = "API11-EP14: Very long password (500 chars) handled gracefully",
            groups = {"api", "regression", "boundary"})
    @Step("API11-EP14: 500-char password")
    public void testVeryLongPassword() {
        ApiResponse r = userApi.verifyLogin("test@test.com", "P".repeat(500));
        assertEquals(r.status(), 200, "Server should not crash for long password");
        assertTrue(r.responseCode() == 400 || r.responseCode() == 404,
                "Long password should return 400 or 404. Got: " + r.responseCode());
    }

    // ── EP15: DELETE method → 405 ────────────────────────────────────────────

    @Test(description = "API11-EP15: DELETE /api/verifyLogin returns 405",
            groups = {"api", "regression"})
    @Step("API11-EP15: DELETE method not supported")
    public void testDeleteMethodNotSupported() {
        ApiResponse r = userApi.deleteVerifyLogin();
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
        assertTrue(r.message().toLowerCase().contains("not supported"),
                "Message: " + r.message());
    }

    // ── EP16: GET method → 405 ───────────────────────────────────────────────

    @Test(description = "API11-EP16: GET /api/verifyLogin returns 405",
            groups = {"api", "regression"})
    @Step("API11-EP16: GET method not supported")
    public void testGetMethodNotSupported() {
        ApiResponse r = new ApiResponse(apiClient.get("/api/verifyLogin"));
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
    }
}
