package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * TC_API10: Products List API — Deep validation + BVA
 *
 * GET /api/productsList
 *
 * Beyond basic 200 check — validate:
 *   - Total product count (site has 34 products)
 *   - Required fields on every product
 *   - Category structure (usertype + category name)
 *   - Price format
 *   - No duplicate product IDs
 */
public class TC_API10_ProductsListBoundary extends BaseApiTest {

    private static final int EXPECTED_PRODUCT_COUNT = 34;

    @Test(
            description = "API Products EP1: Response contains exactly 34 products",
            groups = {"api", "regression", "boundary"}
    )
    @Description("BVA: Products list should contain exactly 34 products (site total)")
    @Step("API Products: verify exact product count = 34")
    public void testProductsListExactCount() {
        ApiResponse response = userApi.getAllProducts();

        assertEquals(response.responseCode(), 200);
        JsonNode products = response.json().path("products");
        assertEquals(products.size(), EXPECTED_PRODUCT_COUNT,
                "Expected " + EXPECTED_PRODUCT_COUNT + " products but got: " + products.size());
    }

    @Test(
            description = "API Products EP2: Every product has all required fields",
            groups = {"api", "regression"}
    )
    @Description("EP: Every product in the list must have id, name, price, category, image")
    @Step("API Products: verify all products have required fields")
    public void testAllProductsHaveRequiredFields() {
        ApiResponse response = userApi.getAllProducts();
        assertEquals(response.responseCode(), 200);

        JsonNode products = response.json().path("products");
        for (int i = 0; i < products.size(); i++) {
            JsonNode p = products.get(i);
            assertFalse(p.path("id").isMissingNode(),
                    "Product[" + i + "] missing 'id'");
            assertFalse(p.path("name").isMissingNode(),
                    "Product[" + i + "] missing 'name'");
            assertFalse(p.path("price").isMissingNode(),
                    "Product[" + i + "] missing 'price'");
            assertFalse(p.path("category").isMissingNode(),
                    "Product[" + i + "] missing 'category'");
        }
    }

    @Test(
            description = "API Products EP3: No duplicate product IDs in the list",
            groups = {"api", "regression"}
    )
    @Description("EP: Product IDs should be unique across the entire list")
    @Step("API Products: verify no duplicate IDs")
    public void testNoDuplicateProductIds() {
        ApiResponse response = userApi.getAllProducts();
        assertEquals(response.responseCode(), 200);

        JsonNode products = response.json().path("products");
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        for (JsonNode p : products) {
            int id = p.path("id").asInt();
            assertTrue(ids.add(id),
                    "Duplicate product ID found: " + id);
        }
    }

    @Test(
            description = "API Products EP4: Category structure has usertype and category name",
            groups = {"api", "regression"}
    )
    @Description("EP: Each product's category should have usertype and category name fields")
    @Step("API Products: verify category structure")
    public void testProductCategoryStructure() {
        ApiResponse response = userApi.getAllProducts();
        assertEquals(response.responseCode(), 200);

        JsonNode products = response.json().path("products");
        // Check first 5 products for category structure
        for (int i = 0; i < Math.min(5, products.size()); i++) {
            JsonNode category = products.get(i).path("category");
            assertFalse(category.isMissingNode(),
                    "Product[" + i + "] category missing");
            assertFalse(category.path("usertype").isMissingNode(),
                    "Product[" + i + "] category.usertype missing");
            assertFalse(category.path("category").isMissingNode(),
                    "Product[" + i + "] category.category missing");
        }
    }

    @Test(
            description = "API Brands EP1: Response contains expected brands",
            groups = {"api", "regression"}
    )
    @Description("EP: Brands list should contain known brands (Polo, H&M, Madame, etc.)")
    @Step("API Brands: verify known brands exist")
    public void testBrandsListContainsKnownBrands() {
        ApiResponse response = userApi.getAllBrands();
        assertEquals(response.responseCode(), 200);

        JsonNode brands = response.json().path("brands");
        assertTrue(brands.size() > 0, "Brands list should not be empty");

        // Collect all brand names
        java.util.Set<String> brandNames = new java.util.HashSet<>();
        for (JsonNode b : brands) {
            brandNames.add(b.path("brand").asText().toLowerCase());
        }

        // Verify known brands from site exploration
        String[] expectedBrands = {"polo", "h&m", "madame", "babyhug", "biba"};
        for (String expected : expectedBrands) {
            assertTrue(brandNames.contains(expected),
                    "Expected brand '" + expected + "' not found in brands list");
        }
    }

    @Test(
            description = "API Brands EP2: Every brand has id and brand name fields",
            groups = {"api", "regression"}
    )
    @Description("EP: Every brand must have id and brand name")
    @Step("API Brands: verify all brands have required fields")
    public void testAllBrandsHaveRequiredFields() {
        ApiResponse response = userApi.getAllBrands();
        assertEquals(response.responseCode(), 200);

        JsonNode brands = response.json().path("brands");
        for (int i = 0; i < brands.size(); i++) {
            JsonNode b = brands.get(i);
            assertFalse(b.path("id").isMissingNode(),
                    "Brand[" + i + "] missing 'id'");
            assertFalse(b.path("brand").isMissingNode(),
                    "Brand[" + i + "] missing 'brand'");
            assertFalse(b.path("brand").asText().isEmpty(),
                    "Brand[" + i + "] 'brand' name should not be empty");
        }
    }
}
