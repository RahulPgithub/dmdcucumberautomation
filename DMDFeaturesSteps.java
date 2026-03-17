package com.dmd.stepDefinitions;

import com.dmd.framework.api.DMDFeaturesServiceAPI;
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

public class DMDFeaturesSteps {
    private static final Logger logger = LogManager.getLogger(DMDFeaturesSteps.class);
    private DMDFeaturesServiceAPI featuresAPI = new DMDFeaturesServiceAPI();
    private TestContext testContext;

    public DMDFeaturesSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("User loads the XML payload for creating feature {string}")
    public void userLoadsXMLPayloadForCreatingFeature(String payloadFileName) {
        logger.info("Loading XML payload: " + payloadFileName);
        String xmlPayload = XMLPayloadLoader.loadXMLPayload("features", payloadFileName);
        testContext.setLastRequestPayload(xmlPayload);
        testContext.setTestData("feature_payload", xmlPayload);
        logger.info("Payload loaded successfully");
    }

    @When("User creates a new feature with the payload")
    public void userCreatesNewFeature() {
        logger.info("Creating new feature");
        String payload = (String) testContext.getTestData("feature_payload");
        Response response = featuresAPI.createFeature(payload);
        testContext.setLastResponse(response);
        testContext.setLastEndpoint("/api/v1/features/create");
    }

    @When("User fetches all features")
    public void userFetchesAllFeatures() {
        logger.info("Fetching all features");
        Response response = featuresAPI.getAllFeatures();
        testContext.setLastResponse(response);
        testContext.setLastEndpoint("/api/v1/features");
    }

    @When("User fetches feature with ID {string}")
    public void userFetchesFeatureWithId(String featureId) {
        logger.info("Fetching feature with ID: " + featureId);
        Response response = featuresAPI.getFeatureById(featureId);
        testContext.setLastResponse(response);
        testContext.setLastEndpoint("/api/v1/features/" + featureId);
    }

    @When("User updates feature {string} with payload {string}")
    public void userUpdatesFeature(String featureId, String payloadFileName) {
        logger.info("Updating feature with ID: " + featureId);
        String xmlPayload = XMLPayloadLoader.loadXMLPayload("features", payloadFileName);
        Response response = featuresAPI.updateFeature(featureId, xmlPayload);
        testContext.setLastResponse(response);
        testContext.setLastEndpoint("/api/v1/features/" + featureId);
    }

    @Then("Response status code should be {int}")
    public void responseStatusCodeShouldBe(int expectedStatusCode) {
        Response response = testContext.getLastResponse();
        try {
            ResponseValidator.validateStatusCode(response, expectedStatusCode);
            TestResultsWriter.writeTestResult(
                "Status Code Validation",
                "DMD Features Service",
                testContext.getLastEndpoint(),
                "POST/GET/PUT",
                testContext.getLastRequestPayload(),
                response,
                true
            );
        } catch (AssertionError e) {
            TestResultsWriter.writeTestResult(
                "Status Code Validation",
                "DMD Features Service",
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

    @Then("Response should contain field {string} with value {string}")
    public void responseShouldContainField(String fieldPath, String expectedValue) {
        Response response = testContext.getLastResponse();
        try {
            ResponseValidator.validateResponseField(response, fieldPath, expectedValue);
            TestResultsWriter.writeTestResult(
                "Field Validation: " + fieldPath,
                "DMD Features Service",
                testContext.getLastEndpoint(),
                "POST/GET/PUT",
                testContext.getLastRequestPayload(),
                response,
                true
            );
        } catch (AssertionError e) {
            TestResultsWriter.writeTestResult(
                "Field Validation: " + fieldPath,
                "DMD Features Service",
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

    @Then("Response field {string} should exist")
    public void responseFieldShouldExist(String fieldPath) {
        Response response = testContext.getLastResponse();
        try {
            ResponseValidator.validateResponseFieldExists(response, fieldPath);
            TestResultsWriter.writeTestResult(
                "Field Existence Validation: " + fieldPath,
                "DMD Features Service",
                testContext.getLastEndpoint(),
                "POST/GET/PUT",
                testContext.getLastRequestPayload(),
                response,
                true
            );
        } catch (AssertionError e) {
            TestResultsWriter.writeTestResult(
                "Field Existence Validation: " + fieldPath,
                "DMD Features Service",
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

    @Then("Extract and store response field {string} as {string}")
    public void extractAndStoreResponseField(String fieldPath, String variableName) {
        Response response = testContext.getLastResponse();
        Object value = ResponseValidator.getResponseFieldValue(response, fieldPath);
        testContext.setResponseData(variableName, String.valueOf(value));
        logger.info("Extracted field " + fieldPath + " and stored as " + variableName);
    }
}
