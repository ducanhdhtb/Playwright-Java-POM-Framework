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
 * TC_API15: Create Account — Extended EP + BVA + Error Guessing
 *
 * POST /api/createAccount
 *
 * EP1  — All valid fields → 201
 * EP2  — Duplicate email → 400
 * EP3  — Missing name → 400
 * EP4  — Missing email → 400
 * EP5  — Missing password → 400
 * EP6  — Invalid email formats → 400
 * EP7  — Valid title values (Mr/Mrs/Miss) → 201
 * EP8  — Invalid title → 400 or 201
 * EP9  — Birth date boundary: day 1 (min), day 31 (max), day 0, day 32
 * EP10 — Birth month boundary: valid months, invalid months
 * EP11 — Birth year boundary: past year, future year, current year
 * EP12 — Very long address (BVA)
 * EP13 — Special chars in address (Error Guessing)
 * EP14 — GET method → 405
 * EP15 — DELETE method → 405
 * EP16 — Concurrent creation with same email (race condition)
 */
public class TC_API15_CreateAccountExtended extends BaseApiTest {

    private ApiResponse createFull(String name, String email, String password,
                                    Map<String, String> overrides) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", name);
        form.put("email", email);
        form.put("password", password);
        form.put("title", "Mr");
        form.put("birth_date", "10");
        form.put("birth_month", "July");
        form.put("birth_year", "1990");
        form.put("firstname", name);
        form.put("lastname", "Tester");
        form.put("company", "TestCorp");
        form.put("address1", "123 Test St");
        form.put("address2", "");
        form.put("country", "United States");
        form.put("zipcode", "10001");
        form.put("state", "New York");
        form.put("city", "New York");
        form.put("mobile_number", "1234567890");
        form.putAll(overrides);
        return new ApiResponse(apiClient.postForm("/api/createAccount", form));
    }

    // ── EP1: Full valid creation ──────────────────────────────────────────────

    @Test(description = "API15-EP1: All valid fields creates account and returns 201",
            groups = {"api", "smoke"})
    @Step("API15-EP1: Full valid creation")
    public void testFullValidCreation() {
        String email = "api15_ep1_" + System.currentTimeMillis() + "@test.com";
        ApiResponse r = createFull("EP1User", email, "Password123", Map.of());
        assertEquals(r.responseCode(), 201, "Body: " + r.text());
        assertTrue(r.message().toLowerCase().contains("created"),
                "Message: " + r.message());
        userApi.teardownUser(email, "Password123");
    }

    // ── EP2: Duplicate email ──────────────────────────────────────────────────

    @Test(description = "API15-EP2: Duplicate email returns 400",
            groups = {"api", "regression"})
    @Step("API15-EP2: Duplicate email")
    public void testDuplicateEmail() {
        String email = "api15_ep2_" + System.currentTimeMillis() + "@test.com";
        createFull("DupUser1", email, "Password123", Map.of());
        try {
            ApiResponse r = createFull("DupUser2", email, "Password123", Map.of());
            assertEquals(r.responseCode(), 400, "Body: " + r.text());
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP7: Valid title values ───────────────────────────────────────────────

    @DataProvider(name = "validTitles")
    public Object[][] validTitles() {
        return new Object[][]{{"Mr"}, {"Mrs"}, {"Miss"}};
    }

    @Test(description = "API15-EP7: Valid title values (Mr/Mrs/Miss) create account",
            dataProvider = "validTitles",
            groups = {"api", "regression", "boundary"})
    @Step("API15-EP7: Valid title '{0}'")
    public void testValidTitles(String title) {
        String email = "api15_title_" + title + "_" + System.currentTimeMillis() + "@test.com";
        ApiResponse r = createFull("TitleUser", email, "Password123",
                Map.of("title", title));
        assertEquals(r.responseCode(), 201,
                "Title '" + title + "' should create account. Body: " + r.text());
        userApi.teardownUser(email, "Password123");
    }

    // ── EP9: Birth date boundary ──────────────────────────────────────────────

    @DataProvider(name = "birthDates")
    public Object[][] birthDates() {
        return new Object[][]{
                {"1",  "July", "1990", true},   // BVA: min day
                {"31", "July", "1990", true},   // BVA: max day for July (31 days)
                {"28", "February", "2000", true}, // BVA: valid Feb day
                {"0",  "July", "1990", false},  // BVA: below min
                {"32", "July", "1990", false},  // BVA: above max
        };
    }

    @Test(description = "API15-EP9: Birth date boundary values",
            dataProvider = "birthDates",
            groups = {"api", "regression", "boundary"})
    @Step("API15-EP9: Birth date {0}/{1}/{2} — valid={3}")
    public void testBirthDateBoundary(String day, String month, String year, boolean expectValid) {
        String email = "api15_bd_" + System.currentTimeMillis() + "@test.com";
        ApiResponse r = createFull("BDUser", email, "Password123", Map.of(
                "birth_date", day,
                "birth_month", month,
                "birth_year", year
        ));
        if (expectValid) {
            assertEquals(r.responseCode(), 201,
                    "Date " + day + "/" + month + "/" + year + " should be valid. Body: " + r.text());
            userApi.teardownUser(email, "Password123");
        } else {
            assertTrue(r.responseCode() == 400 || r.responseCode() == 201,
                    "Invalid date should return 400 or 201. Got: " + r.responseCode());
            if (r.responseCode() == 201) userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP10: Birth month boundary ────────────────────────────────────────────

    @DataProvider(name = "birthMonths")
    public Object[][] birthMonths() {
        return new Object[][]{
                {"January",   true},
                {"December",  true},
                {"July",      true},
                {"InvalidMonth", false},
                {"13",        false},
                {"0",         false},
        };
    }

    @Test(description = "API15-EP10: Birth month boundary values",
            dataProvider = "birthMonths",
            groups = {"api", "regression", "boundary"})
    @Step("API15-EP10: Birth month '{0}' — valid={1}")
    public void testBirthMonthBoundary(String month, boolean expectValid) {
        String email = "api15_bm_" + System.currentTimeMillis() + "@test.com";
        ApiResponse r = createFull("BMUser", email, "Password123",
                Map.of("birth_month", month));
        if (expectValid) {
            assertEquals(r.responseCode(), 201,
                    "Month '" + month + "' should be valid. Body: " + r.text());
            userApi.teardownUser(email, "Password123");
        } else {
            assertTrue(r.responseCode() == 400 || r.responseCode() == 201,
                    "Invalid month should return 400 or 201. Got: " + r.responseCode());
            if (r.responseCode() == 201) userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP11: Birth year boundary ─────────────────────────────────────────────

    @DataProvider(name = "birthYears")
    public Object[][] birthYears() {
        return new Object[][]{
                {"1900", true},   // BVA: very old
                {"2000", true},   // EP: typical
                {"2010", true},   // EP: recent
                {"2026", true},   // BVA: current year
                {"2027", false},  // BVA: future year
                {"9999", false},  // BVA: far future
                {"0",    false},  // BVA: zero year
                {"-1",   false},  // BVA: negative year
        };
    }

    @Test(description = "API15-EP11: Birth year boundary values",
            dataProvider = "birthYears",
            groups = {"api", "regression", "boundary"})
    @Step("API15-EP11: Birth year '{0}' — valid={1}")
    public void testBirthYearBoundary(String year, boolean expectValid) {
        String email = "api15_by_" + System.currentTimeMillis() + "@test.com";
        ApiResponse r = createFull("BYUser", email, "Password123",
                Map.of("birth_year", year));
        if (expectValid) {
            assertEquals(r.responseCode(), 201,
                    "Year '" + year + "' should be valid. Body: " + r.text());
            userApi.teardownUser(email, "Password123");
        } else {
            assertTrue(r.responseCode() == 400 || r.responseCode() == 201,
                    "Invalid year should return 400 or 201. Got: " + r.responseCode());
            if (r.responseCode() == 201) userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP12: Very long address (BVA) ─────────────────────────────────────────

    @Test(description = "API15-EP12: Very long address (500 chars) handled gracefully",
            groups = {"api", "regression", "boundary"})
    @Step("API15-EP12: 500-char address")
    public void testVeryLongAddress() {
        String email = "api15_ep12_" + System.currentTimeMillis() + "@test.com";
        String longAddress = "A".repeat(500);
        ApiResponse r = createFull("LongAddrUser", email, "Password123",
                Map.of("address1", longAddress));
        assertEquals(r.status(), 200, "Server should not crash");
        assertTrue(r.responseCode() == 201 || r.responseCode() == 400,
                "Long address should return 201 or 400. Got: " + r.responseCode());
        if (r.responseCode() == 201) userApi.teardownUser(email, "Password123");
    }

    // ── EP13: Special chars in address ───────────────────────────────────────

    @DataProvider(name = "specialAddresses")
    public Object[][] specialAddresses() {
        return new Object[][]{
                {"123 O'Brien St",          "apostrophe"},
                {"Apt #5, 123 Main St",     "hash and comma"},
                {"123 Müller Straße",       "German umlauts"},
                {"<script>alert(1)</script>","XSS in address"},
        };
    }

    @Test(description = "API15-EP13: Special characters in address handled safely",
            dataProvider = "specialAddresses",
            groups = {"api", "regression", "negative"})
    @Step("API15-EP13: Special address '{1}'")
    public void testSpecialCharsInAddress(String address, String description) {
        String email = "api15_ep13_" + System.currentTimeMillis() + "@test.com";
        ApiResponse r = createFull("SpecialAddrUser", email, "Password123",
                Map.of("address1", address));
        assertEquals(r.status(), 200, "Server should not crash for: " + description);
        assertTrue(r.responseCode() == 201 || r.responseCode() == 400,
                description + " should return 201 or 400. Got: " + r.responseCode());
        if (r.responseCode() == 201) userApi.teardownUser(email, "Password123");
    }

    // ── EP14: GET method → 405 ───────────────────────────────────────────────

    @Test(description = "API15-EP14: GET /api/createAccount returns 405",
            groups = {"api", "regression"})
    @Step("API15-EP14: GET method not supported")
    public void testGetMethodNotSupported() {
        ApiResponse r = new ApiResponse(apiClient.get("/api/createAccount"));
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
    }

    // ── EP15: DELETE method → 405 ────────────────────────────────────────────

    @Test(description = "API15-EP15: DELETE /api/createAccount returns 405",
            groups = {"api", "regression"})
    @Step("API15-EP15: DELETE method not supported")
    public void testDeleteMethodNotSupported() {
        ApiResponse r = new ApiResponse(
                apiClient.deleteForm("/api/createAccount", Map.of("email", "t@t.com")));
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
    }
}
