package api;

import io.qameta.allure.Step;
import utils.ConfigReader;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * High-level helper for user-related API operations on automationexercise.com.
 *
 * API reference: https://automationexercise.com/api_list
 *
 * Endpoints used:
 *   POST   /api/createAccount          – create a new user (responseCode 201)
 *   DELETE /api/deleteAccount          – delete a user    (responseCode 200)
 *   POST   /api/verifyLogin            – verify credentials (200 = valid, 404 = invalid)
 *   GET    /api/getUserDetailByEmail   – fetch user details (200 = found, 404 = not found)
 */
public class UserApiHelper {

    private final ApiClient client;

    public UserApiHelper(ApiClient client) {
        this.client = client;
    }

    /**
     * Creates a user via API with full details.
     *
     * @return ApiResponse (responseCode 201 = success, 400 = already exists)
     */
    @Step("API: Create user '{0}' with email '{1}'")
    public ApiResponse createUser(String name, String email, String password,
                                  String title, String birthDate, String birthMonth, String birthYear,
                                  String firstName, String lastName, String company,
                                  String address1, String address2, String country,
                                  String zipcode, String state, String city, String mobileNumber) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", name);
        form.put("email", email);
        form.put("password", password);
        form.put("title", title);
        form.put("birth_date", birthDate);
        form.put("birth_month", birthMonth);
        form.put("birth_year", birthYear);
        form.put("firstname", firstName);
        form.put("lastname", lastName);
        form.put("company", company);
        form.put("address1", address1);
        form.put("address2", address2);
        form.put("country", country);
        form.put("zipcode", zipcode);
        form.put("state", state);
        form.put("city", city);
        form.put("mobile_number", mobileNumber);
        return new ApiResponse(client.postForm("/api/createAccount", form));
    }

    /**
     * Creates a user with minimal required fields (sensible defaults for the rest).
     */
    @Step("API: Create minimal user '{0}' with email '{1}'")
    public ApiResponse createUser(String name, String email, String password) {
        return createUser(
                name, email, password,
                "Mr", "10", "July", "1990",
                name, "Tester", "AutoCorp",
                "123 Test Street", "",
                "United States", "10001", "New York", "New York", "0123456789"
        );
    }

    /**
     * Deletes a user via API.
     *
     * @return ApiResponse (responseCode 200 = success, 404 = not found)
     */
    @Step("API: Delete user with email '{0}'")
    public ApiResponse deleteUser(String email, String password) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("email", email);
        form.put("password", password);
        return new ApiResponse(client.deleteForm("/api/deleteAccount", form));
    }

    /**
     * Verifies login credentials via API.
     *
     * @return ApiResponse (responseCode 200 = valid, 404 = invalid, 400 = missing params)
     */
    @Step("API: Verify login for email '{0}'")
    public ApiResponse verifyLogin(String email, String password) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("email", email);
        form.put("password", password);
        return new ApiResponse(client.postForm("/api/verifyLogin", form));
    }

    /**
     * Fetches user details by email.
     *
     * @return ApiResponse containing user JSON under "user" key
     */
    @Step("API: Get user details for email '{0}'")
    public ApiResponse getUserByEmail(String email) {
        return new ApiResponse(client.get("/api/getUserDetailByEmail",
                Map.of("email", email)));
    }

    // ── Convenience factory ──────────────────────────────────────────────────

    /**
     * Creates a user via API and returns the email used.
     * Throws if creation fails.
     */
    @Step("API: Setup test user '{0}'")
    public String setupUser(String name, String password) {
        String email = "api_" + System.currentTimeMillis() + "@testmail.com";
        ApiResponse resp = createUser(name, email, password);
        if (resp.responseCode() != 201) {
            throw new RuntimeException(
                    "API user creation failed: code=" + resp.responseCode()
                            + " msg=" + resp.message());
        }
        return email;
    }

    /**
     * Deletes a user via API. Silently ignores 404 (already deleted).
     */
    @Step("API: Teardown test user '{0}'")
    public void teardownUser(String email, String password) {
        ApiResponse resp = deleteUser(email, password);
        int code = resp.responseCode();
        if (code != 200 && code != 404) {
            System.err.println("[WARN] API teardown returned unexpected code=" + code
                    + " for email=" + email);
        }
    }

    /**
     * Default password used when creating users for test setup.
     */
    public static String defaultPassword() {
        return ConfigReader.getProperty("test.defaultPassword", "Password123");
    }
}
