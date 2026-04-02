cat > /home/claude/dmd-testng-framework/src/test/java/com/dmd/api/stepdefs/JsonApiStepDefs.java << 'JAVA'
package com.dmd.api.stepdefs;

import com.dmd.api.model.ScenarioContext;
import com.dmd.api.utils.ApiClient;
import com.dmd.api.utils.JsonResponseValidator;
import com.dmd.api.utils.UrlBuilder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cucumber step definitions for JSON REST API scenarios.
 *
 * Works alongside the existing {@link ApiStepDefs} (XML).
 * Both share the same {@link ScenarioContext} via PicoContainer DI.
 *
 * Step naming convention distinguishes JSON steps:
 *   Given the JSON API endpoint is "..." for microservice "..."
 *   When  the client sends a JSON GET request
 *   Then  the JSON response status code should be 200
 *   Then  the JSON field "statusCode" should be "00"
 *
 * Env-aware:  all base URLs are resolved via EnvironmentConfig
 *             so -Denv=stage|qa|prod auto-switches to the right host.
 */
public class JsonApiStepDefs {

    private static final Logger log = LoggerFactory.getLogger(JsonApiStepDefs.class);

    private final ScenarioContext ctx;

    public JsonApiStepDefs(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    // ════════════════════════════════════════════════════════════
    //  GIVEN — Setup
    // ════════════════════════════════════════════════════════════

    /**
     * Set a JSON API endpoint using a path relative to the microservice base URL.
     * The base URL is resolved from config/{env}.properties automatically.
     *
     * Example:
     *   Given the JSON API path is "/devices/359324081412299"
     *         for microservice "dmd-device-info"
     */
    @Given("the JSON API path is {string} for microservice {string}")
    public void setJsonApiPath(String path, String microservice) {
        ctx.setCurrentMicroservice(microservice);
        ctx.setCurrentUrl(UrlBuilder.buildJsonUrl(microservice, path));
        ctx.setResponseType("json");
        log.info("JSON endpoint [{}]: {}", microservice, ctx.getCurrentUrl());
    }

    /**
     * Set a full JSON API URL (env substitution applied automatically).
     * Use when the complete URL is defined in the feature file.
     */
    @Given("the JSON API endpoint is {string} for microservice {string}")
    public void setJsonApiFullUrl(String url, String microservice) {
        ctx.setCurrentMicroservice(microservice);
        ctx.setCurrentUrl(UrlBuilder.toEnvJsonUrl(url, microservice));
        ctx.setResponseType("json");
        log.info("JSON endpoint (full) [{}]: {}", microservice, ctx.getCurrentUrl());
    }

    /** Override the Bearer token for this scenario only. */
    @Given("the request uses bearer token {string}")
    public void setBearerToken(String token) {
        ctx.setOverrideToken(token);
    }

    /** Set a JSON request body for POST scenarios. */
    @Given("the JSON request body is:")
    public void setJsonRequestBody(String body) {
        ctx.setJsonRequestBody(body);
    }

    // ════════════════════════════════════════════════════════════
    //  WHEN — Actions
    // ════════════════════════════════════════════════════════════

    @When("the client sends a JSON GET request")
    public void sendJsonGetRequest() {
        String url = ctx.getCurrentUrl();
        log.info("JSON GET → {}", url);
        Response response = ctx.getOverrideToken() != null
            ? ApiClient.get().jsonGetWithToken(url, ctx.getOverrideToken())
            : ApiClient.get().jsonGetFromFullUrl(url);
        ctx.setCurrentResponse(response);
        ctx.setResponseType("json");
        log.info("HTTP {} ← {}", response.getStatusCode(), ctx.getCurrentApiName());
    }

    @When("the client sends a JSON GET request with query params:")
    public void sendJsonGetWithParams(Map<String, String> params) {
        String microservice = ctx.getCurrentMicroservice();
        String path         = UrlBuilder.extractPath(ctx.getCurrentUrl());
        // Dynamic substitution
        Map<String, String> resolved = new HashMap<>();
        params.forEach((k, v) -> resolved.put(k, substituteDynamic(v)));
        Response response = ApiClient.get().jsonGet(microservice, path, resolved);
        ctx.setCurrentResponse(response);
        ctx.setResponseType("json");
        log.info("HTTP {} ← JSON GET {}", response.getStatusCode(), path);
    }

    @When("the client sends a JSON POST request")
    public void sendJsonPostRequest() {
        String microservice = ctx.getCurrentMicroservice();
        String path         = UrlBuilder.extractPath(ctx.getCurrentUrl());
        String body         = ctx.getJsonRequestBody() != null
                              ? ctx.getJsonRequestBody() : "{}";
        Response response   = ApiClient.get().jsonPost(microservice, path, body);
        ctx.setCurrentResponse(response);
        ctx.setResponseType("json");
        log.info("HTTP {} ← JSON POST {}", response.getStatusCode(), path);
    }

    // ════════════════════════════════════════════════════════════
    //  THEN — HTTP level
    // ════════════════════════════════════════════════════════════

    @Then("the JSON response status code should be {int}")
    public void assertJsonStatusCode(int expected) {
        validator().assertStatusCode(expected);
    }

    @Then("the JSON response should be successful")
    public void assertJsonSuccess() {
        validator().assertSuccess().assertBodyNotEmpty().assertContentTypeJson();
    }

    @Then("the JSON response should be created")
    public void assertJsonCreated() {
        validator().assertCreated().assertBodyNotEmpty();
    }

    @Then("the JSON response body should not be empty")
    public void assertJsonBodyNotEmpty() {
        validator().assertBodyNotEmpty();
    }

    @Then("the JSON response content type should be JSON")
    public void assertContentTypeJson() {
        validator().assertContentTypeJson();
    }

    // ════════════════════════════════════════════════════════════
    //  THEN — DMD business code
    // ════════════════════════════════════════════════════════════

    @Then("the JSON DMD status code should be {string}")
    public void assertDmdJsonStatus(String code) {
        validator().assertDmdJsonStatus(code);
    }

    @Then("the JSON DMD response should be success")
    public void assertDmdJsonSuccess() {
        validator().assertDmdJsonSuccess();
    }

    @Then("the JSON DMD message should be {string}")
    public void assertDmdJsonMessage(String message) {
        validator().assertDmdJsonMessage(message);
    }

    // ════════════════════════════════════════════════════════════
    //  THEN — Field presence
    // ════════════════════════════════════════════════════════════

    @Then("the JSON response should contain field {string}")
    public void assertJsonFieldPresent(String path) {
        validator().assertFieldPresent(path);
    }

    @Then("the JSON response should contain fields:")
    public void assertJsonFieldsPresent(List<String> paths) {
        validator().assertFieldsPresent(paths);
    }

    @Then("the JSON field {string} should be null")
    public void assertJsonFieldNull(String path) {
        validator().assertFieldNull(path);
    }

    // ════════════════════════════════════════════════════════════
    //  THEN — Field value
    // ════════════════════════════════════════════════════════════

    @Then("the JSON field {string} should be {string}")
    public void assertJsonFieldValue(String path, String expected) {
        validator().assertFieldValue(path, substituteDynamic(expected));
    }

    @Then("the JSON field {string} should contain {string}")
    public void assertJsonFieldContains(String path, String substring) {
        validator().assertFieldContains(path, substring);
    }

    @Then("the JSON field {string} should equal {int}")
    public void assertJsonFieldInt(String path, int expected) {
        validator().assertFieldEquals(path, expected);
    }

    @Then("the JSON field {string} should be greater than {double}")
    public void assertJsonFieldGreaterThan(String path, double min) {
        validator().assertFieldGreaterThan(path, min);
    }

    @Then("the JSON field {string} should be true")
    public void assertJsonFieldTrue(String path) {
        validator().assertFieldTrue(path);
    }

    @Then("the JSON field {string} should be false")
    public void assertJsonFieldFalse(String path) {
        validator().assertFieldFalse(path);
    }

    @Then("the JSON field {string} should match {string}")
    public void assertJsonFieldMatches(String path, String regex) {
        validator().assertFieldMatches(path, regex);
    }

    // ════════════════════════════════════════════════════════════
    //  THEN — Array / collection
    // ════════════════════════════════════════════════════════════

    @Then("the JSON array {string} should not be empty")
    public void assertJsonArrayNotEmpty(String path) {
        validator().assertArrayNotEmpty(path);
    }

    @Then("the JSON array {string} should have {int} items")
    public void assertJsonArraySize(String path, int size) {
        validator().assertArraySize(path, size);
    }

    @Then("the JSON array {string} should contain {string}")
    public void assertJsonArrayContains(String path, String value) {
        validator().assertArrayContains(path, value);
    }

    // ════════════════════════════════════════════════════════════
    //  THEN — Extract for use in subsequent steps
    // ════════════════════════════════════════════════════════════

    @Then("I extract JSON field {string} and save it")
    public void extractAndSaveJsonField(String path) {
        String value = validator().getString(path);
        ctx.setJsonPath(value);
        log.info("Extracted [{}] = {}", path, value);
    }

    @Then("the saved JSON value should be {string}")
    public void assertSavedJsonValue(String expected) {
        assertThat(ctx.getJsonPath())
            .as("Saved JSON value")
            .isEqualTo(expected);
    }

    // ════════════════════════════════════════════════════════════
    //  Helpers
    // ════════════════════════════════════════════════════════════

    private JsonResponseValidator validator() {
        assertThat(ctx.getCurrentResponse())
            .as("No HTTP response found — did you call 'the client sends a JSON GET request'?")
            .isNotNull();
        return new JsonResponseValidator(ctx.getCurrentResponse());
    }

    private String substituteDynamic(String value) {
        if (ctx.getDeviceId() != null) value = value.replace("${deviceId}", ctx.getDeviceId());
        if (ctx.getSimId()    != null) value = value.replace("${simId}",    ctx.getSimId());
        if (ctx.getMacId()    != null) value = value.replace("${macId}",    ctx.getMacId());
        if (ctx.getMeid()     != null) value = value.replace("${meid}",     ctx.getMeid());
        if (ctx.getAppType()  != null) value = value.replace("${appType}",  ctx.getAppType());
        if (ctx.getClientId() != null) value = value.replace("${clientId}", ctx.getClientId());
        if (ctx.getDacc()     != null) value = value.replace("${dacc}",     ctx.getDacc());
        if (ctx.getSacc()     != null) value = value.replace("${sacc}",     ctx.getSacc());
        return value;
    }
}
