package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * API Tests: Products List and Brands List
 *
 * API 1: GET /api/productsList       → 200 + products array
 * API 2: POST /api/productsList      → 405 method not supported
 * API 3: GET /api/brandsList         → 200 + brands array
 * API 4: PUT /api/brandsList         → 405 method not supported
 */
public class TC_API05_ProductsAndBrands extends BaseApiTest {

    // ── API 1: GET All Products ───────────────────────────────────────────────

    @Test(
            description = "API1: GET /api/productsList returns 200 with products array",
            groups = {"api", "smoke"}
    )
    @Description("GET /api/productsList → responseCode 200, products list not empty")
    @Step("API1: Get all products list")
    public void testGetAllProductsList() {
        ApiResponse response = userApi.getAllProducts();

        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 200,
                "API responseCode should be 200. Body: " + response.text());

        JsonNode products = response.json().path("products");
        assertFalse(products.isMissingNode(), "Response should contain 'products' array");
        assertTrue(products.isArray(), "'products' should be an array");
        assertTrue(products.size() > 0, "Products list should not be empty");

        // Verify first product has required fields
        JsonNode first = products.get(0);
        assertFalse(first.path("id").isMissingNode(), "Product should have 'id'");
        assertFalse(first.path("name").isMissingNode(), "Product should have 'name'");
        assertFalse(first.path("price").isMissingNode(), "Product should have 'price'");
    }

    // ── API 2: POST to Products List → 405 ───────────────────────────────────

    @Test(
            description = "API2: POST /api/productsList returns 405 method not supported",
            groups = {"api", "regression"}
    )
    @Description("POST /api/productsList → responseCode 405")
    @Step("API2: POST to products list expects 405")
    public void testPostToProductsListReturns405() {
        ApiResponse response = userApi.postToProductsList();

        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 405,
                "POST to productsList should return 405. Body: " + response.text());
        assertTrue(response.message().toLowerCase().contains("not supported"),
                "Message should contain 'not supported'. Got: " + response.message());
    }

    // ── API 3: GET All Brands ─────────────────────────────────────────────────

    @Test(
            description = "API3: GET /api/brandsList returns 200 with brands array",
            groups = {"api", "smoke"}
    )
    @Description("GET /api/brandsList → responseCode 200, brands list not empty")
    @Step("API3: Get all brands list")
    public void testGetAllBrandsList() {
        ApiResponse response = userApi.getAllBrands();

        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 200,
                "API responseCode should be 200. Body: " + response.text());

        JsonNode brands = response.json().path("brands");
        assertFalse(brands.isMissingNode(), "Response should contain 'brands' array");
        assertTrue(brands.isArray(), "'brands' should be an array");
        assertTrue(brands.size() > 0, "Brands list should not be empty");

        // Verify first brand has required fields
        JsonNode first = brands.get(0);
        assertFalse(first.path("id").isMissingNode(), "Brand should have 'id'");
        assertFalse(first.path("brand").isMissingNode(), "Brand should have 'brand' name");
    }

    // ── API 4: PUT to Brands List → 405 ──────────────────────────────────────

    @Test(
            description = "API4: PUT /api/brandsList returns 405 method not supported",
            groups = {"api", "regression"}
    )
    @Description("PUT /api/brandsList → responseCode 405")
    @Step("API4: PUT to brands list expects 405")
    public void testPutToBrandsListReturns405() {
        ApiResponse response = userApi.putToBrandsList();

        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 405,
                "PUT to brandsList should return 405. Body: " + response.text());
        assertTrue(response.message().toLowerCase().contains("not supported"),
                "Message should contain 'not supported'. Got: " + response.message());
    }
}
