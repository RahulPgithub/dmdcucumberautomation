package com.dmd.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.dmd.stepDefinitions", "com.dmd.hooks"},
    plugin = {
        "pretty",
        "json:target/reports/cucumber-report.json",
        "html:target/reports/cucumber-report.html",
        "junit:target/reports/cucumber-report.xml"
    },
    monochrome = true,
    stepNotifications = true,
    dryRun = false
)
public class TestRunner {
}
