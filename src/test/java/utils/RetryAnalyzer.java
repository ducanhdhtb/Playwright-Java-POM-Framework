package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final int DEFAULT_MAX_RETRIES = 1;
    private final int maxRetries = ConfigReader.getIntProperty("retryCount", DEFAULT_MAX_RETRIES);
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetries) {
            retryCount++;
            return true;
        }
        return false;
    }
}
