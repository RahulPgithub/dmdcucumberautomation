package com.dmd.hooks;

import com.dmd.framework.config.TestContext;
import com.dmd.framework.utils.LoggerUtils;
import io.cucumber.java.Before;
import io.cucumber.java.After;

public class Hooks {
    private TestContext testContext;

    public Hooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before
    public void setUp() {
        LoggerUtils.info("===========================================");
        LoggerUtils.info("Starting new test scenario");
        LoggerUtils.info("===========================================");
        testContext.clearContext();
    }

    @After
    public void tearDown() {
        LoggerUtils.info("===========================================");
        LoggerUtils.info("Test scenario completed");
        LoggerUtils.info("===========================================");
    }
}
