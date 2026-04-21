package utils;

import automation_exercise.BaseTest;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class TestListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        String className = result.getTestClass() != null ? result.getTestClass().getName() : "unknown";
        String methodName = result.getMethod() != null ? result.getMethod().getMethodName() : "unknown";

        String displayName = className + "." + methodName;
        try {
            if (result.getMethod() != null && result.getMethod().getConstructorOrMethod() != null) {
                Step step = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Step.class);
                if (step != null && step.value() != null && !step.value().isBlank()) {
                    displayName = step.value();
                }
            }
        } catch (Exception ignored) {
        }

        System.out.println("[TEST START] " + displayName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String methodName = result.getMethod() != null ? result.getMethod().getMethodName() : "unknown";
        System.out.println("[TEST PASS] " + methodName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod() != null ? result.getMethod().getMethodName() : "unknown";
        System.out.println("[TEST FAIL] " + methodName);

        if (!(result.getInstance() instanceof BaseTest baseTest)) {
            return;
        }

        Page page = baseTest.getPage();
        if (page == null) {
            return;
        }

        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(result.getName() + "_Failed_Screenshot", new ByteArrayInputStream(screenshot));
        } catch (Exception ignored) {
        }

        try {
            Allure.addAttachment("Failed_URL",
                    new ByteArrayInputStream(page.url().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ignored) {
        }

        try {
            Allure.addAttachment("Failed_Title",
                    new ByteArrayInputStream(page.title().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ignored) {
        }

        try {
            Allure.addAttachment("Page_Source",
                    new ByteArrayInputStream(page.content().getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String methodName = result.getMethod() != null ? result.getMethod().getMethodName() : "unknown";
        System.out.println("[TEST SKIP] " + methodName);
    }
}
