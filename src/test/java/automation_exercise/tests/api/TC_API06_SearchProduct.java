package automation_exercise.tests.api;

import api.ApiResponse;
import automation_exercise.BaseApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * API Tests: Search Product
 *
 * API 5: POST /api/searchProduct with keyword     → 200 + products
 * API 6: POST /api/searchProduct without param    → 400 bad request
 */
public class TC_API06_SearchProduct extends BaseApiTest {

    @DataProvider(name = "searchKeywords")
    public Object[][] searchKeywords() {
        return new Object[][]{
                {"top"},
                {"dress"},
                {"tshirt"},
                {"jean"}
        };
    }

    // ── API 5: Search with valid keyword ─────────────────────────────────────

    @Test(
            description = "API5: POST /api/searchProduct with keyword returns matching products",
            dataProvider = "searchKeywords",
            groups = {"api", "smoke"}
    )
    @Description("POST /api/searchProduct with search_product param → 200 + products array")
    @Step("API5: Search product with keyword '{0}'")
    public void testSearchProductWithKeyword(String keyword) {
        ApiResponse response = userApi.searchProduct(keyword);

        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 200,
                "Search should return 200. Body: " + response.text());

        JsonNode products = response.json().path("products");
        assertFalse(products.isMissingNode(), "Response should contain 'products' array");
        assertTrue(products.isArray(), "'products' should be an array");
        assertTrue(products.size() > 0,
                "Search for '" + keyword + "' should return at least 1 product");
    }

    // ── API 6: Search without parameter → 400 ────────────────────────────────

    @Test(
            description = "API6: POST /api/searchProduct without search_product param returns 400",
            groups = {"api", "regression"}
    )
    @Description("POST /api/searchProduct without search_product → responseCode 400")
    @Step("API6: Search product without parameter expects 400")
    public void testSearchProductWithoutParam() {
        ApiResponse response = userApi.searchProductWithoutParam();

        assertEquals(response.status(), 200, "HTTP status should be 200");
        assertEquals(response.responseCode(), 400,
                "Missing param should return 400. Body: " + response.text());
        assertTrue(response.message().toLowerCase().contains("missing"),
                "Message should mention 'missing'. Got: " + response.message());
    }
}
