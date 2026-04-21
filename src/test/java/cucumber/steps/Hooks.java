package cucumber.steps;

import cucumber.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;

/**
 * Cucumber Hooks — runs before/after each scenario.
 * Manages browser lifecycle and attaches artifacts on failure.
 */
public class Hooks {

    private final ScenarioContext ctx;

    public Hooks(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @Before
    public void setUp(Scenario scenario) {
        ctx.initBrowser();
        System.out.println("[SCENARIO START] " + scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ctx.takeScreenshot();
            if (screenshot != null) {
                // Attach to Cucumber report
                scenario.attach(screenshot, "image/png", scenario.getName() + "_failed");
                // Attach to Allure report
                Allure.addAttachment(scenario.getName() + "_Failed_Screenshot",
                        new ByteArrayInputStream(screenshot));
            }
            if (ctx.page != null) {
                try {
                    Allure.addAttachment("Failed_URL",
                            new ByteArrayInputStream(ctx.page.url().getBytes()));
                    Allure.addAttachment("Page_Source",
                            new ByteArrayInputStream(ctx.page.content().getBytes()));
                } catch (Exception ignored) {}
            }
        }
        ctx.tearDown();
        System.out.println("[SCENARIO END] " + scenario.getName()
                + " — " + (scenario.isFailed() ? "FAILED" : "PASSED"));
    }
}
