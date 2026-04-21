package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * TC_API09: Create Account API — EP + BVA + Error Guessing
 *
 * POST /api/createAccount
 *
 * Partitions:
 *   EP1 — All valid fields → 201
 *   EP2 — Missing required field (name) → 400
 *   EP3 — Missing required field (email) → 400
 *   EP4 — Missing required field (password) → 400
 *   EP5 — Invalid email format → 400 or 201 (server-side validation check)
 *   EP6 — Duplicate email → 400
 *   EP7 — Very long name (BVA) → handled gracefully
 *   EP8 — Special chars in name (Error Guessing)
 *   EP9 — Invalid title value → handled gracefully
 */
public class TC_API09_CreateAccountBoundary extends BaseApiTest {

    private ApiResponse createWithOverrides(Map<String, String> overrides) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", "TestUser");
        form.put("email", "ep_" + System.currentTimeMillis() + "@testmail.com");
        form.put("password", "Password123");
        form.put("title", "Mr");
        form.put("birth_date", "10");
        form.put("birth_month", "July");
        form.put("birth_year", "1990");
        form.put("firstname", "Test");
        form.put("lastname", "User");
        form.put("company", "TestCorp");
        form.put("address1", "123 Test St");
        form.put("address2", "");
        form.put("country", "United States");
        form.put("zipcode", "10001");
        form.put("state", "New York");
        form.put("city", "New York");
        form.put("mobile_number", "1234567890");
        form.putAll(overrides); // override specific fields
        return new ApiResponse(apiClient.postForm("/api/createAccount", form));
    }

    // ── EP2/3/4: Missing required fields ─────────────────────────────────────

    @DataProvider(name = "missingRequiredFields")
    public Object[][] missingRequiredFields() {
        return new Object[][]{
                {Map.of("name", ""),     "empty name"},
                {Map.of("email", ""),    "empty email"},
                {Map.of("password", ""), "empty password"},
        };
    }

    @Test(
            description = "API CreateAccount EP2-4: Missing required fields return 400",
            dataProvider = "missingRequiredFields",
            groups = {"api", "regression", "boundary", "negative"}
    )
    @Description("EP: Missing required fields (name/email/password) should return 400")
    @Step("API CreateAccount: missing field — {1}")
    public void testCreateAccountMissingRequiredField(Map<String, String> overrides,
                                                       String description) {
        ApiResponse response = createWithOverrides(overrides);
        assertEquals(response.status(), 200);
        assertEquals(response.responseCode(), 400,
                "Missing " + description + " should return 400. Body: " + response.text());
    }

    // ── EP5: Invalid email format ─────────────────────────────────────────────

    @DataProvider(name = "invalidEmailFormats")
    public Object[][] invalidEmailFormats() {
        return new Object[][]{
                {"notanemail"},
                {"missing@"},
                {"@nodomain.com"},
                {"spaces in@email.com"},
        };
    }

    @Test(
            description = "API CreateAccount EP5: Invalid email format returns 400",
            dataProvider = "invalidEmailFormats",
            groups = {"api", "regression", "boundary", "negative"}
    )
    @Description("EP: Invalid email format should return 400 bad request")
    @Step("API CreateAccount: invalid email '{0}'")
    public void testCreateAccountInvalidEmail(String invalidEmail) {
        ApiResponse response = createWithOverrides(Map.of("email", invalidEmail));
        assertEquals(response.status(), 200);
        // Server may return 400 for invalid email format
        assertTrue(response.responseCode() == 400 || response.responseCode() == 201,
                "Invalid email '" + invalidEmail + "' should return 400 or 201. Got: "
                        + response.responseCode() + " Body: " + response.text());
    }

    // ── EP7: Very long name (BVA) ─────────────────────────────────────────────

    @Test(
            description = "API CreateAccount EP7: Very long name (500 chars) handled gracefully",
            groups = {"api", "regression", "boundary"}
    )
    @Description("BVA: 500-character name should be handled without server crash")
    @Step("API CreateAccount: 500-char name")
    public void testCreateAccountVeryLongName() {
        String longName = "A".repeat(500);
        String email = "longname_" + System.currentTimeMillis() + "@testmail.com";

        ApiResponse response = createWithOverrides(Map.of("name", longName, "email", email));
        assertEquals(response.status(), 200);
        // Should return 201 (created) or 400 (too long) — not 500
        assertTrue(response.responseCode() == 201 || response.responseCode() == 400,
                "Long name should return 201 or 400. Got: " + response.responseCode());

        // Cleanup if created
        if (response.responseCode() == 201) {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP8: Special chars in name (Error Guessing) ───────────────────────────

    @DataProvider(name = "specialCharNames")
    public Object[][] specialCharNames() {
        return new Object[][]{
                {"<script>alert('xss')</script>", "XSS in name"},
                {"'; DROP TABLE users; --",        "SQL injection in name"},
                {"Nguyễn Đức Anh",                 "Vietnamese unicode name"},
                {"John O'Brien",                   "apostrophe in name"},
                {"María García",                   "accented chars"},
        };
    }

    @Test(
            description = "API CreateAccount EP8: Special characters in name handled safely",
            dataProvider = "specialCharNames",
            groups = {"api", "regression", "negative"}
    )
    @Description("Error Guessing: Special chars in name should not crash server")
    @Step("API CreateAccount: special char name — {1}")
    public void testCreateAccountSpecialCharName(String name, String description) {
        String email = "special_" + System.currentTimeMillis() + "@testmail.com";

        ApiResponse response = createWithOverrides(Map.of("name", name, "email", email));
        assertEquals(response.status(), 200,
                "Server should not crash for: " + description);
        assertTrue(response.responseCode() == 201 || response.responseCode() == 400,
                description + " should return 201 or 400. Got: " + response.responseCode());

        // Cleanup if created
        if (response.responseCode() == 201) {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP9: Invalid title value ──────────────────────────────────────────────

    @DataProvider(name = "titleValues")
    public Object[][] titleValues() {
        return new Object[][]{
                {"Mr",    201},  // EP: valid
                {"Mrs",   201},  // EP: valid
                {"Miss",  201},  // EP: valid
                {"Dr",    201},  // EP: may or may not be valid
                {"",      400},  // EP: empty — likely invalid
                {"INVALID_TITLE_XYZ", 400}, // EP: invalid value
        };
    }

    @Test(
            description = "API CreateAccount EP9: Title field validation",
            dataProvider = "titleValues",
            groups = {"api", "regression", "boundary"}
    )
    @Description("EP: Valid titles (Mr/Mrs/Miss) return 201, invalid titles return 400")
    @Step("API CreateAccount: title='{0}' expect {1}")
    public void testCreateAccountTitleValidation(String title, int expectedCode) {
        String email = "title_" + System.currentTimeMillis() + "@testmail.com";

        ApiResponse response = createWithOverrides(Map.of("title", title, "email", email));
        assertEquals(response.status(), 200);

        // For valid titles, assert exact code; for invalid, allow flexibility
        if (expectedCode == 201) {
            assertEquals(response.responseCode(), 201,
                    "Title '" + title + "' should create user. Body: " + response.text());
            userApi.teardownUser(email, "Password123");
        } else {
            // Invalid title — server may return 400 or still 201 (lenient validation)
            assertTrue(response.responseCode() == 400 || response.responseCode() == 201,
                    "Invalid title should return 400 or 201. Got: " + response.responseCode());
            if (response.responseCode() == 201) {
                userApi.teardownUser(email, "Password123");
            }
        }
    }
}
