package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final int DEFAULT_MAX_RETRIES = 1;
    private final int maxRetries = ConfigReader.getIntProperty("retryCount", DEFAULT_MAX_RETRIES);
    private int retryCount = 0;

    private static boolean isCausedByTimeout(Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            // Keep it string-based to avoid compile-time dependency issues across versions.
            String name = cur.getClass().getName();
            if ("com.microsoft.playwright.TimeoutError".equals(name)) {
                return true;
            }
            cur = cur.getCause();
        }
        return false;
    }

    private static boolean isAssertion(Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            if (cur instanceof AssertionError) {
                return true;
            }
            cur = cur.getCause();
        }
        return false;
    }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount >= maxRetries) {
            return false;
        }

        Throwable t = result.getThrowable();
        if (t == null) {
            return false;
        }

        // Default behavior: only retry flaky infra-style failures (timeouts), never logic/assertion failures.
        boolean onlyTimeouts = ConfigReader.getBooleanProperty("retry.onlyTimeouts", true);
        if (isAssertion(t)) {
            return false;
        }
        if (onlyTimeouts && !isCausedByTimeout(t)) {
            return false;
        }

        retryCount++;
        System.out.println("[RETRY] " + result.getName() + " attempt " + (retryCount + 1) + "/" + (maxRetries + 1)
                + " reason=" + t.getClass().getSimpleName());
        return true;
    }
}
