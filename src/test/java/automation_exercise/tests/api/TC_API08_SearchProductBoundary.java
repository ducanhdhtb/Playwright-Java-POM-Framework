package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * TC_API08: Search Product API — EP + BVA + Error Guessing
 *
 * POST /api/searchProduct
 *
 * Partitions:
 *   EP1 — Valid keyword with results: "top", "dress"
 *   EP2 — Single char keyword (BVA lower): "t"
 *   EP3 — Keyword with no results: "xyzzy_notexist"
 *   EP4 — Empty string keyword: ""
 *   EP5 — Special chars: "<script>", "' OR 1=1"
 *   EP6 — Very long keyword (BVA upper)
 *   EP7 — Numeric keyword: "123"
 */
public class TC_API08_SearchProductBoundary extends BaseApiTest {

    @DataProvider(name = "validKeywords")
    public Object[][] validKeywords() {
        return new Object[][]{
                {"top"},
                {"dress"},
                {"t"},       // BVA: single char
                {"jean"},
        };
    }

    @DataProvider(name = "noResultKeywords")
    public Object[][] noResultKeywords() {
        return new Object[][]{
                {"xyzzy_notexist_12345"},
                {"123"},
                {"@@@"},
        };
    }

    // ── EP1/BVA: Valid keywords ───────────────────────────────────────────────

    @Test(
            description = "API Search EP1: Valid keyword returns products array",
            dataProvider = "validKeywords",
            groups = {"api", "regression", "boundary"}
    )
    @Description("EP: Valid search keywords return non-empty products array with responseCode 200")
    @Step("API Search: keyword '{0}' returns products")
    public void testSearchWithValidKeyword(String keyword) {
        ApiResponse response = userApi.searchProduct(keyword);

        assertEquals(response.status(), 200);
        assertEquals(response.responseCode(), 200,
                "Search '" + keyword + "' should return 200. Body: " + response.text());

        JsonNode products = response.json().path("products");
        assertTrue(products.isArray(), "products should be array");
        assertTrue(products.size() > 0,
                "Search '" + keyword + "' should return at least 1 product");

        // Verify product structure
        JsonNode first = products.get(0);
        assertFalse(first.path("id").isMissingNode(), "Product should have id");
        assertFalse(first.path("name").isMissingNode(), "Product should have name");
        assertFalse(first.path("price").isMissingNode(), "Product should have price");
        assertFalse(first.path("category").isMissingNode(), "Product should have category");
    }

    // ── EP3: No results ───────────────────────────────────────────────────────

    @Test(
            description = "API Search EP3: Non-existent keyword returns empty products array",
            dataProvider = "noResultKeywords",
            groups = {"api", "regression", "boundary"}
    )
    @Description("EP: Non-existent keyword returns 200 with empty products array")
    @Step("API Search: keyword '{0}' returns empty results")
    public void testSearchWithNoResultKeyword(String keyword) {
        ApiResponse response = userApi.searchProduct(keyword);

        assertEquals(response.status(), 200);
        assertEquals(response.responseCode(), 200,
                "No-result search should still return 200. Body: " + response.text());

        JsonNode products = response.json().path("products");
        assertTrue(products.isArray(), "products should be array");
        assertEquals(products.size(), 0,
                "Search '" + keyword + "' should return 0 products");
    }

    // ── EP4: Empty string ─────────────────────────────────────────────────────

    @Test(
            description = "API Search EP4: Empty string keyword returns 400 or empty results",
            groups = {"api", "regression", "boundary", "negative"}
    )
    @Description("EP: Empty search_product value — expect 400 bad request or empty results")
    @Step("API Search: empty keyword")
    public void testSearchWithEmptyKeyword() {
        ApiResponse response = new ApiResponse(
                apiClient.postForm("/api/searchProduct", Map.of("search_product", "")));

        assertEquals(response.status(), 200);
        // Either 400 (bad request) or 200 with empty results
        assertTrue(response.responseCode() == 400 || response.responseCode() == 200,
                "Empty keyword should return 400 or 200. Got: " + response.responseCode());
    }

    // ── EP5: XSS / SQL injection ──────────────────────────────────────────────

    @Test(
            description = "API Search EP5: XSS payload in search_product is sanitized",
            groups = {"api", "regression", "negative"}
    )
    @Description("Error Guessing: XSS payload should return 200 with 0 products, not execute")
    @Step("API Search: XSS payload")
    public void testSearchWithXssPayload() {
        ApiResponse response = userApi.searchProduct("<script>alert('xss')</script>");

        assertEquals(response.status(), 200);
        assertEquals(response.responseCode(), 200);

        JsonNode products = response.json().path("products");
        assertEquals(products.size(), 0, "XSS payload should return 0 products");
    }

    @Test(
            description = "API Search EP5: SQL injection payload is handled safely",
            groups = {"api", "regression", "negative"}
    )
    @Description("Error Guessing: SQL injection should not return all products or crash")
    @Step("API Search: SQL injection payload")
    public void testSearchWithSqlInjection() {
        ApiResponse response = userApi.searchProduct("' OR '1'='1");

        assertEquals(response.status(), 200);
        assertEquals(response.responseCode(), 200);

        // Should NOT return all 34 products (SQL injection not working = good)
        JsonNode products = response.json().path("products");
        assertTrue(products.size() < 34,
                "SQL injection should not return all products. Got: " + products.size());
    }

    // ── EP6: Very long keyword (BVA upper) ────────────────────────────────────

    @Test(
            description = "API Search EP6: Very long keyword (500 chars) handled gracefully",
            groups = {"api", "regression", "boundary"}
    )
    @Description("BVA: 500-character keyword should return 200 with 0 results, not crash")
    @Step("API Search: 500-char keyword")
    public void testSearchWithVeryLongKeyword() {
        String longKeyword = "a".repeat(500);
        ApiResponse response = userApi.searchProduct(longKeyword);

        assertEquals(response.status(), 200);
        assertTrue(response.responseCode() == 200 || response.responseCode() == 400,
                "Long keyword should return 200 or 400. Got: " + response.responseCode());
    }
}
