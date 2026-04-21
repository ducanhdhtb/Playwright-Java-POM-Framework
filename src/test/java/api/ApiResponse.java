package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;

/**
 * Thin wrapper around Playwright's APIResponse that adds JSON parsing helpers.
 */
public class ApiResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final APIResponse raw;
    private JsonNode jsonBody;

    public ApiResponse(APIResponse raw) {
        this.raw = raw;
    }

    public int status() {
        return raw.status();
    }

    public boolean ok() {
        return raw.ok();
    }

    public String text() {
        return raw.text();
    }

    /** Parse response body as JSON and return the root node. */
    public JsonNode json() {
        if (jsonBody == null) {
            String body = raw.text();
            // Server có thể trả HTML khi bị overload (503, 429...) — wrap thành JSON error node
            if (body == null || body.isBlank() || body.trim().startsWith("<")) {
                try {
                    jsonBody = MAPPER.readTree(
                            "{\"responseCode\":" + raw.status()
                            + ",\"message\":\"Server returned non-JSON response (HTTP "
                            + raw.status() + "): " + body.replace("\"", "'").replaceAll("\\s+", " ").trim() + "\"}");
                } catch (Exception e) {
                    throw new RuntimeException("Server returned non-JSON body (HTTP "
                            + raw.status() + "): " + body);
                }
            } else {
                try {
                    jsonBody = MAPPER.readTree(body);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse API response as JSON (HTTP "
                            + raw.status() + "): " + body, e);
                }
            }
        }
        return jsonBody;
    }

    /** Convenience: get a top-level integer field (e.g. "responseCode"). */
    public int responseCode() {
        return json().path("responseCode").asInt(-1);
    }

    /** Convenience: get a top-level string field (e.g. "message"). */
    public String message() {
        return json().path("message").asText("");
    }

    @Override
    public String toString() {
        return "ApiResponse{status=" + status() + ", body=" + text() + "}";
    }
}
