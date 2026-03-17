package com.dmd.stepDefinitions;

import com.dmd.framework.api.DMDDeviceInfoServiceAPI;
import com.dmd.framework.config.TestContext;
import com.dmd.framework.utils.XMLPayloadLoader;
import com.dmd.framework.utils.ResponseValidator;
import com.dmd.framework.reporting.TestResultsWriter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DMDDeviceInfoSteps {
    private static final Logger logger = LogManager.getLogger(DMDDeviceInfoSteps.class);
    private DMDDeviceInfoServiceAPI deviceInfoAPI = new DMDDeviceInfoServiceAPI();
    private TestContext testContext;

    public DMDDeviceInfoSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("User loads the XML payload for creating device info {string}")
    public void userLoadsXMLPayloadForCreatingDeviceInfo(String payloadFileName) {
        logger.info("Loading XML payload: " + payloadFileName);
        String xmlPayload = XMLPayloadLoader.loadXMLPayload("deviceinfo", payloadFileName);
        testContext.setLastRequestPayload(xmlPayload);
        testContext.setTestData("deviceinfo_payload", xmlPayload);
        logger.info("Payload loaded successfully");
    }

    @When("User creates a new device info with the payload")
    public void userCreatesNewDeviceInfo() {
        logger.info("Creating new device info");
        String payload = (String) testContext.getTestData("deviceinfo_payload");
        Response response = deviceInfoAPI.createDeviceInfo(payload);
        testContext.setLastResponse(response);
        testContext.setLastEndpoint("/api/v1/deviceinfo/create");
    }

    @When("User fetches all device info")
    public void userFetchesAllDeviceInfo() {
        logger.info("Fetching all device info");
        Response response = deviceInfoAPI.getAllDeviceInfo();
        testContext.setLastResponse(response);
        testContext.setLastEndpoint("/api/v1/deviceinfo");
    }

    @When("User fetches device info with ID {string}")
    public void userFetchesDeviceInfoWithId(String deviceId) {
        logger.info("Fetching device info with ID: " + deviceId);
        Response response = deviceInfoAPI.getDeviceInfoById(deviceId);
        testContext.setLastResponse(response);
        testContext.setLastEndpoint("/api/v1/deviceinfo/" + deviceId);
    }

    @When("User updates device info {string} with payload {string}")
    public void userUpdatesDeviceInfo(String deviceId, String payloadFileName) {
        logger.info("Updating device info with ID: " + deviceId);
        String xmlPayload = XMLPayloadLoader.loadXMLPayload("deviceinfo", payloadFileName);
        Response response = deviceInfoAPI.updateDeviceInfo(deviceId, xmlPayload);
        testContext.setLastResponse(response);
        testContext.setLastEndpoint("/api/v1/deviceinfo/" + deviceId);
    }

    @Then("Device info response status code should be {int}")
    public void deviceInfoResponseStatusCodeShouldBe(int expectedStatusCode) {
        Response response = testContext.getLastResponse();
        try {
            ResponseValidator.validateStatusCode(response, expectedStatusCode);
            TestResultsWriter.writeTestResult(
                "Device Info Status Code Validation",
                "DMD DeviceInfo Service",
                testContext.getLastEndpoint(),
                "POST/GET/PUT",
                testContext.getLastRequestPayload(),
                response,
                true
            );
        } catch (AssertionError e) {
            TestResultsWriter.writeTestResult(
                "Device Info Status Code Validation",
                "DMD DeviceInfo Service",
                testContext.getLastEndpoint(),
                "POST/GET/PUT",
                testContext.getLastRequestPayload(),
                response,
                false,
                e.getMessage()
            );
            throw e;
        }
    }

    @Then("Device info response should contain field {string} with value {string}")
    public void deviceInfoResponseShouldContainField(String fieldPath, String expectedValue) {
        Response response = testContext.getLastResponse();
        try {
            ResponseValidator.validateResponseField(response, fieldPath, expectedValue);
            TestResultsWriter.writeTestResult(
                "Device Info Field Validation: " + fieldPath,
                "DMD DeviceInfo Service",
                testContext.getLastEndpoint(),
                "POST/GET/PUT",
                testContext.getLastRequestPayload(),
                response,
                true
            );
        } catch (AssertionError e) {
            TestResultsWriter.writeTestResult(
                "Device Info Field Validation: " + fieldPath,
                "DMD DeviceInfo Service",
                testContext.getLastEndpoint(),
                "POST/GET/PUT",
                testContext.getLastRequestPayload(),
                response,
                false,
                e.getMessage()
            );
            throw e;
        }
    }
}
