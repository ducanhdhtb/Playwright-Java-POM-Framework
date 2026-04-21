package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * TC_API14: Update Account — Extended EP + BVA + Error Guessing
 *
 * PUT /api/updateAccount
 *
 * EP1  — All valid fields → 200
 * EP2  — Update name only → 200
 * EP3  — Update address fields → 200
 * EP4  — Non-existent email → 404
 * EP5  — Missing email param → 400
 * EP6  — Missing password param → 400
 * EP7  — Wrong password → 404
 * EP8  — Empty name → 400
 * EP9  — Invalid title → handled
 * EP10 — Verify updated data via GET
 * EP11 — POST method → 405
 * EP12 — DELETE method → 405
 * EP13 — XSS in name field → safe
 * EP14 — Update with same data (idempotent) → 200
 */
public class TC_API14_UpdateAccountExtended extends BaseApiTest {

    private ApiResponse updateWithOverrides(String email, String password,
                                             Map<String, String> overrides) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", "UpdatedUser");
        form.put("email", email);
        form.put("password", password);
        form.put("title", "Mr");
        form.put("birth_date", "10");
        form.put("birth_month", "July");
        form.put("birth_year", "1990");
        form.put("firstname", "Updated");
        form.put("lastname", "User");
        form.put("company", "UpdatedCorp");
        form.put("address1", "999 Updated St");
        form.put("address2", "");
        form.put("country", "United States");
        form.put("zipcode", "10001");
        form.put("state", "New York");
        form.put("city", "New York");
        form.put("mobile_number", "1112223333");
        form.putAll(overrides);
        return new ApiResponse(apiClient.putForm("/api/updateAccount", form));
    }

    // ── EP1: Full valid update ────────────────────────────────────────────────

    @Test(description = "API14-EP1: Full valid update returns 200",
            groups = {"api", "smoke"})
    @Step("API14-EP1: Full valid update")
    public void testFullValidUpdate() {
        String email = "api14_ep1_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("OriginalUser", email, "Password123");
        try {
            ApiResponse r = updateWithOverrides(email, "Password123",
                    Map.of("name", "UpdatedName"));
            assertEquals(r.responseCode(), 200, "Body: " + r.text());
            assertTrue(r.message().toLowerCase().contains("updated"),
                    "Message: " + r.message());
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP2: Update name only ─────────────────────────────────────────────────

    @Test(description = "API14-EP2: Update name field only returns 200",
            groups = {"api", "regression"})
    @Step("API14-EP2: Update name only")
    public void testUpdateNameOnly() {
        String email = "api14_ep2_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("OldName", email, "Password123");
        try {
            ApiResponse r = updateWithOverrides(email, "Password123",
                    Map.of("name", "NewName_" + System.currentTimeMillis()));
            assertEquals(r.responseCode(), 200, "Body: " + r.text());
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP3: Update address fields ────────────────────────────────────────────

    @Test(description = "API14-EP3: Update address fields returns 200",
            groups = {"api", "regression"})
    @Step("API14-EP3: Update address fields")
    public void testUpdateAddressFields() {
        String email = "api14_ep3_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("AddrUser", email, "Password123");
        try {
            ApiResponse r = updateWithOverrides(email, "Password123", Map.of(
                    "address1", "New Address 123",
                    "city", "Los Angeles",
                    "state", "California",
                    "zipcode", "90001"
            ));
            assertEquals(r.responseCode(), 200, "Body: " + r.text());
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP4: Non-existent email ───────────────────────────────────────────────

    @Test(description = "API14-EP4: Non-existent email returns 404",
            groups = {"api", "regression"})
    @Step("API14-EP4: Non-existent email update")
    public void testUpdateNonExistentEmail() {
        ApiResponse r = updateWithOverrides(
                "ghost_" + System.currentTimeMillis() + "@nowhere.com",
                "Password123", Map.of());
        assertEquals(r.responseCode(), 404, "Body: " + r.text());
    }

    // ── EP5: Missing email param ──────────────────────────────────────────────

    @Test(description = "API14-EP5: Missing email param returns 400",
            groups = {"api", "regression"})
    @Step("API14-EP5: Missing email param")
    public void testMissingEmailParam() {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", "Test");
        form.put("password", "Pass123");
        ApiResponse r = new ApiResponse(apiClient.putForm("/api/updateAccount", form));
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP6: Missing password param ───────────────────────────────────────────

    @Test(description = "API14-EP6: Missing password param returns 400",
            groups = {"api", "regression"})
    @Step("API14-EP6: Missing password param")
    public void testMissingPasswordParam() {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", "Test");
        form.put("email", "test@test.com");
        ApiResponse r = new ApiResponse(apiClient.putForm("/api/updateAccount", form));
        assertEquals(r.responseCode(), 400, "Body: " + r.text());
    }

    // ── EP7: Wrong password ───────────────────────────────────────────────────

    @Test(description = "API14-EP7: Wrong password returns 404",
            groups = {"api", "regression"})
    @Step("API14-EP7: Wrong password on update")
    public void testUpdateWithWrongPassword() {
        String email = "api14_ep7_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("WrongPassUser", email, "CorrectPass");
        try {
            ApiResponse r = updateWithOverrides(email, "WrongPass999", Map.of());
            assertEquals(r.responseCode(), 404, "Body: " + r.text());
        } finally {
            userApi.teardownUser(email, "CorrectPass");
        }
    }

    // ── EP8: Empty name ───────────────────────────────────────────────────────

    @Test(description = "API14-EP8: Empty name returns 400",
            groups = {"api", "regression", "boundary"})
    @Step("API14-EP8: Empty name field")
    public void testUpdateWithEmptyName() {
        String email = "api14_ep8_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("EmptyNameUser", email, "Password123");
        try {
            ApiResponse r = updateWithOverrides(email, "Password123",
                    Map.of("name", ""));
            assertEquals(r.responseCode(), 400, "Body: " + r.text());
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP10: Verify updated data via GET ─────────────────────────────────────

    @Test(description = "API14-EP10: Updated data is reflected in GET user detail",
            groups = {"api", "regression"})
    @Step("API14-EP10: Data integrity after update")
    public void testUpdatedDataReflectedInGet() {
        String email = "api14_ep10_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("BeforeUpdate", email, "Password123");
        try {
            String newName = "AfterUpdate_" + System.currentTimeMillis();
            String newCity = "Chicago";

            ApiResponse update = updateWithOverrides(email, "Password123", Map.of(
                    "name", newName,
                    "city", newCity
            ));
            assertEquals(update.responseCode(), 200);

            // Verify via GET
            ApiResponse get = userApi.getUserByEmail(email);
            assertEquals(get.responseCode(), 200);
            JsonNode user = get.json().path("user");
            assertEquals(user.path("name").asText(), newName,
                    "Name should be updated");
            assertEquals(user.path("city").asText(), newCity,
                    "City should be updated");
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP11: POST method → 405 ───────────────────────────────────────────────

    @Test(description = "API14-EP11: POST /api/updateAccount returns 405",
            groups = {"api", "regression"})
    @Step("API14-EP11: POST method not supported")
    public void testPostMethodNotSupported() {
        ApiResponse r = new ApiResponse(
                apiClient.postForm("/api/updateAccount",
                        Map.of("email", "t@t.com", "password", "p", "name", "n")));
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
    }

    // ── EP12: DELETE method → 405 ─────────────────────────────────────────────

    @Test(description = "API14-EP12: DELETE /api/updateAccount returns 405",
            groups = {"api", "regression"})
    @Step("API14-EP12: DELETE method not supported")
    public void testDeleteMethodNotSupported() {
        ApiResponse r = new ApiResponse(
                apiClient.deleteForm("/api/updateAccount",
                        Map.of("email", "t@t.com", "password", "p", "name", "n")));
        assertEquals(r.responseCode(), 405, "Body: " + r.text());
    }

    // ── EP13: XSS in name field ───────────────────────────────────────────────

    @Test(description = "API14-EP13: XSS in name field is sanitized",
            groups = {"api", "regression", "negative"})
    @Step("API14-EP13: XSS in name field")
    public void testXssInNameField() {
        String email = "api14_ep13_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("XssUser", email, "Password123");
        try {
            ApiResponse r = updateWithOverrides(email, "Password123",
                    Map.of("name", "<script>alert('xss')</script>"));
            assertEquals(r.status(), 200, "Server should not crash");
            // Either 200 (stored but sanitized) or 400 (rejected)
            assertTrue(r.responseCode() == 200 || r.responseCode() == 400,
                    "XSS name should return 200 or 400. Got: " + r.responseCode());

            if (r.responseCode() == 200) {
                // Verify XSS not stored as-is
                ApiResponse get = userApi.getUserByEmail(email);
                String storedName = get.json().path("user").path("name").asText();
                assertFalse(storedName.contains("<script>"),
                        "XSS should be sanitized in stored name");
            }
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }

    // ── EP14: Idempotent update ───────────────────────────────────────────────

    @Test(description = "API14-EP14: Updating with same data is idempotent (returns 200)",
            groups = {"api", "regression"})
    @Step("API14-EP14: Idempotent update")
    public void testIdempotentUpdate() {
        String email = "api14_ep14_" + System.currentTimeMillis() + "@test.com";
        userApi.createUser("IdempotentUser", email, "Password123");
        try {
            // Update twice with same data
            ApiResponse first = updateWithOverrides(email, "Password123",
                    Map.of("name", "SameName"));
            assertEquals(first.responseCode(), 200);

            ApiResponse second = updateWithOverrides(email, "Password123",
                    Map.of("name", "SameName"));
            assertEquals(second.responseCode(), 200,
                    "Second identical update should also return 200");
        } finally {
            userApi.teardownUser(email, "Password123");
        }
    }
}
