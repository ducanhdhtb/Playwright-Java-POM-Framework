package utils;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.Cookie;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * CookieManager — save and restore Playwright browser cookies.
 *
 * Use case: Login once, save cookies, reuse in subsequent tests
 * without going through the login UI again.
 *
 * Usage:
 *   // Save after login
 *   CookieManager.save(context, "admin_session");
 *
 *   // Restore in another test
 *   CookieManager.restore(context, "admin_session");
 *   page.navigate(baseUrl); // already logged in
 */
public class CookieManager {

    private static final Logger log = LogManager.getLogger(CookieManager.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String COOKIE_DIR = "target/cookies";

    /**
     * Save all cookies from the current BrowserContext to a JSON file.
     *
     * @param context   Playwright BrowserContext
     * @param sessionName  Name for the cookie file (e.g. "admin_user", "checkout_user")
     */
    @Step("Saving browser cookies as session '{1}'")
    public static void save(BrowserContext context, String sessionName) {
        try {
            Files.createDirectories(Paths.get(COOKIE_DIR));
            Path cookiePath = getCookiePath(sessionName);
            List<Cookie> cookies = context.cookies();
            MAPPER.writeValue(cookiePath.toFile(), cookies);
            log.info("[CookieManager] Saved {} cookies as session '{}' → {}",
                    cookies.size(), sessionName, cookiePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save cookies for session: " + sessionName, e);
        }
    }

    /**
     * Restore cookies from a saved session file into the BrowserContext.
     *
     * @param context     Playwright BrowserContext
     * @param sessionName Name of the saved session
     */
    @Step("Restoring browser cookies from session '{1}'")
    public static void restore(BrowserContext context, String sessionName) {
        Path cookiePath = getCookiePath(sessionName);
        if (!Files.exists(cookiePath)) {
            throw new RuntimeException(
                    "Cookie session '" + sessionName + "' not found at: " + cookiePath
                    + ". Run the login test first to create it.");
        }
        try {
            List<Cookie> cookies = MAPPER.readValue(
                    cookiePath.toFile(),
                    new TypeReference<List<Cookie>>() {}
            );
            context.addCookies(cookies);
            log.info("[CookieManager] Restored {} cookies from session '{}'",
                    cookies.size(), sessionName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to restore cookies for session: " + sessionName, e);
        }
    }

    /**
     * Check if a saved session exists.
     */
    public static boolean exists(String sessionName) {
        return Files.exists(getCookiePath(sessionName));
    }

    /**
     * Delete a saved session file.
     */
    @Step("Clearing cookie session '{0}'")
    public static void clear(String sessionName) {
        try {
            Files.deleteIfExists(getCookiePath(sessionName));
            log.info("[CookieManager] Cleared session '{}'", sessionName);
        } catch (IOException e) {
            log.warn("[CookieManager] Failed to clear session '{}': {}", sessionName, e.getMessage());
        }
    }

    private static Path getCookiePath(String sessionName) {
        return Paths.get(COOKIE_DIR, sessionName + ".json");
    }
}
