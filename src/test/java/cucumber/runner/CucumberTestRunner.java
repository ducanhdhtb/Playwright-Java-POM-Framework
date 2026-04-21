package cucumber.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Cucumber TestNG runner.
 *
 * Run all scenarios:
 *   mvn test -Dtestng.suiteXmlFile=testng-cucumber.xml
 *
 * Run by tag:
 *   mvn test -Dtestng.suiteXmlFile=testng-cucumber.xml -Dcucumber.filter.tags="@smoke"
 *   mvn test -Dtestng.suiteXmlFile=testng-cucumber.xml -Dcucumber.filter.tags="@api"
 *   mvn test -Dtestng.suiteXmlFile=testng-cucumber.xml -Dcucumber.filter.tags="@regression and not @e2e"
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"cucumber.steps"},
        tags = "${cucumber.filter.tags:}",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true,
        publish = false
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {

    /**
     * Override to enable parallel scenario execution.
     * Set parallel=true to run scenarios in parallel (thread-safe via ScenarioContext).
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
