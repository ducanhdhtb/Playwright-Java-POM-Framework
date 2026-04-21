package api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import io.qameta.allure.Step;
import utils.ConfigReader;

import java.util.Map;

/**
 * Low-level HTTP client wrapping Playwright's APIRequestContext.
 * Provides GET / POST / DELETE helpers with form-encoded body support.
 */
public class ApiClient {

    private final APIRequestContext requestContext;

    public ApiClient(Playwright playwright) {
        String baseUrl = ConfigReader.getProperty("api.baseUrl",
                ConfigReader.getProperty("baseUrl", "https://automationexercise.com"));

        this.requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(baseUrl)
                        .setIgnoreHTTPSErrors(true)
        );
    }

    /** GET request. */
    @Step("API: GET {0}")
    public APIResponse get(String path) {
        return requestContext.get(path);
    }

    /** GET request with query params. */
    @Step("API: GET {0} (query)")
    public APIResponse get(String path, Map<String, String> params) {
        RequestOptions opts = RequestOptions.create();
        params.forEach(opts::setQueryParam);
        return requestContext.get(path, opts);
    }

    /** POST with form-encoded body (application/x-www-form-urlencoded). */
    @Step("API: POST form {0}")
    public APIResponse postForm(String path, Map<String, String> formData) {
        FormData form = buildFormData(formData);
        return requestContext.post(path, RequestOptions.create().setForm(form));
    }

    /** DELETE with form-encoded body. */
    @Step("API: DELETE form {0}")
    public APIResponse deleteForm(String path, Map<String, String> formData) {
        FormData form = buildFormData(formData);
        return requestContext.delete(path, RequestOptions.create().setForm(form));
    }

    /** PUT with form-encoded body. */
    @Step("API: PUT form {0}")
    public APIResponse putForm(String path, Map<String, String> formData) {
        FormData form = buildFormData(formData);
        return requestContext.put(path, RequestOptions.create().setForm(form));
    }

    private FormData buildFormData(Map<String, String> data) {
        FormData form = FormData.create();
        data.forEach(form::set);
        return form;
    }

    @Step("API: Dispose request context")
    public void dispose() {
        try {
            requestContext.dispose();
        } catch (Exception ignored) {
        }
    }
}
