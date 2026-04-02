package com.dmd.api.utils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Fluent validator for JSON API responses.
 *
 * Mirrors XmlResponseValidator but targets JSON REST APIs.
 *
 * Validations supported:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  HTTP level                                                     │
 * │    assertStatusCode(int)        - exact HTTP status code        │
 * │    assertSuccess()              - HTTP 200                      │
 * │    assertCreated()              - HTTP 201                      │
 * │    assertBodyNotEmpty()         - non-null, non-blank body      │
 * │    assertContentTypeJson()      - Content-Type contains json    │
 * │                                                                 │
 * │  Field presence (JsonPath)                                      │
 * │    assertFieldPresent(path)     - field exists and non-null     │
 * │    assertFieldsPresent(list)    - all fields exist              │
 * │    assertFieldNull(path)        - field is null / absent        │
 * │                                                                 │
 * │  Field value                                                    │
 * │    assertFieldValue(path, val)  - exact match                   │
 * │    assertFieldContains(path, s) - string contains substring     │
 * │    assertFieldEquals(path, num) - numeric equality              │
 * │    assertFieldTrue(path)        - boolean true                  │
 * │    assertFieldFalse(path)       - boolean false                 │
 * │                                                                 │
 * │  Array / collection                                             │
 * │    assertArrayNotEmpty(path)    - array has at least one item   │
 * │    assertArraySize(path, n)     - array has exactly n items     │
 * │    assertArrayContains(path, v) - array contains value          │
 * │                                                                 │
 * │  DMD business logic                                             │
 * │    assertDmdJsonStatus(code)    - $.statusCode == code          │
 * │    assertDmdJsonSuccess()       - $.statusCode == "00"          │
 * │    assertDmdJsonMessage(msg)    - $.message == msg              │
 * └─────────────────────────────────────────────────────────────────┘
 *
 * All assertion methods return {@code this} for fluent chaining.
 */
public class JsonResponseValidator {

    private static final Logger log = LoggerFactory.getLogger(JsonResponseValidator.class);

    private final Response response;
    private final JsonPath jsonPath;

    public JsonResponseValidator(Response response) {
        this.response = response;
        JsonPath jp = null;
        try {
            jp = response.jsonPath();
        } catch (Exception e) {
            log.warn("Response body is not valid JSON: {} | Body: {}",
                     e.getMessage(),
                     response.getBody().asString().substring(
                         0, Math.min(200, response.getBody().asString().length())));
        }
        this.jsonPath = jp;
    }

    // ════════════════════════════════════════════════════════════════════
    //  HTTP level
    // ════════════════════════════════════════════════════════════════════

    public JsonResponseValidator assertStatusCode(int expected) {
        assertThat(response.getStatusCode())
            .as("HTTP Status Code")
            .isEqualTo(expected);
        return this;
    }

    public JsonResponseValidator assertSuccess() {
        return assertStatusCode(200);
    }

    public JsonResponseValidator assertCreated() {
        return assertStatusCode(201);
    }

    public JsonResponseValidator assertBadRequest() {
        return assertStatusCode(400);
    }

    public JsonResponseValidator assertUnauthorized() {
        return assertStatusCode(401);
    }

    public JsonResponseValidator assertNotFound() {
        return assertStatusCode(404);
    }

    public JsonResponseValidator assertBodyNotEmpty() {
        assertThat(response.getBody().asString())
            .as("Response body must not be empty")
            .isNotNull()
            .isNotBlank();
        return this;
    }

    public JsonResponseValidator assertContentTypeJson() {
        assertThat(response.getContentType())
            .as("Content-Type header")
            .containsIgnoringCase("application/json");
        return this;
    }

    // ════════════════════════════════════════════════════════════════════
    //  Field presence
    // ════════════════════════════════════════════════════════════════════

    /**
     * Assert a JSON field is present and not null.
     *
     * @param jsonPathExpr JsonPath expression  e.g. "statusCode", "data.deviceId",
     *                     "items[0].name", "response.header.message"
     */
    public JsonResponseValidator assertFieldPresent(String jsonPathExpr) {
        requireJsonParsed(jsonPathExpr);
        Object value = jsonPath.get(jsonPathExpr);
        assertThat(value)
            .as("Field must be present and non-null at path [%s]", jsonPathExpr)
            .isNotNull();
        if (value instanceof String) {
            assertThat((String) value)
                .as("Field must be non-blank at path [%s]", jsonPathExpr)
                .isNotBlank();
        }
        return this;
    }

    /** Assert all fields in the list are present. */
    public JsonResponseValidator assertFieldsPresent(List<String> paths) {
        paths.forEach(this::assertFieldPresent);
        return this;
    }

    /** Assert a field is null or absent. */
    public JsonResponseValidator assertFieldNull(String jsonPathExpr) {
        requireJsonParsed(jsonPathExpr);
        Object value = jsonPath.get(jsonPathExpr);
        assertThat(value)
            .as("Field should be null/absent at path [%s]", jsonPathExpr)
            .isNull();
        return this;
    }

    // ════════════════════════════════════════════════════════════════════
    //  Field value — String
    // ════════════════════════════════════════════════════════════════════

    /** Assert field value equals expected string (exact match). */
    public JsonResponseValidator assertFieldValue(String jsonPathExpr, String expected) {
        requireJsonParsed(jsonPathExpr);
        String actual = jsonPath.getString(jsonPathExpr);
        assertThat(actual)
            .as("Value at [%s]", jsonPathExpr)
            .isEqualTo(expected);
        return this;
    }

    /** Assert field value contains expected substring (case-sensitive). */
    public JsonResponseValidator assertFieldContains(String jsonPathExpr, String substring) {
        requireJsonParsed(jsonPathExpr);
        String actual = jsonPath.getString(jsonPathExpr);
        assertThat(actual)
            .as("Value at [%s] should contain [%s]", jsonPathExpr, substring)
            .contains(substring);
        return this;
    }

    /** Assert field value matches a regex pattern. */
    public JsonResponseValidator assertFieldMatches(String jsonPathExpr, String regex) {
        requireJsonParsed(jsonPathExpr);
        String actual = jsonPath.getString(jsonPathExpr);
        assertThat(actual)
            .as("Value at [%s] should match regex [%s]", jsonPathExpr, regex)
            .matches(regex);
        return this;
    }

    // ════════════════════════════════════════════════════════════════════
    //  Field value — Numeric
    // ════════════════════════════════════════════════════════════════════

    /** Assert integer field equals expected value. */
    public JsonResponseValidator assertFieldEquals(String jsonPathExpr, int expected) {
        requireJsonParsed(jsonPathExpr);
        int actual = jsonPath.getInt(jsonPathExpr);
        assertThat(actual)
            .as("Integer value at [%s]", jsonPathExpr)
            .isEqualTo(expected);
        return this;
    }

    /** Assert numeric field is greater than min value. */
    public JsonResponseValidator assertFieldGreaterThan(String jsonPathExpr, double min) {
        requireJsonParsed(jsonPathExpr);
        double actual = jsonPath.getDouble(jsonPathExpr);
        assertThat(actual)
            .as("Numeric value at [%s] should be > %s", jsonPathExpr, min)
            .isGreaterThan(min);
        return this;
    }

    // ════════════════════════════════════════════════════════════════════
    //  Field value — Boolean
    // ════════════════════════════════════════════════════════════════════

    public JsonResponseValidator assertFieldTrue(String jsonPathExpr) {
        requireJsonParsed(jsonPathExpr);
        Boolean actual = jsonPath.getBoolean(jsonPathExpr);
        assertThat(actual)
            .as("Boolean at [%s] should be true", jsonPathExpr)
            .isTrue();
        return this;
    }

    public JsonResponseValidator assertFieldFalse(String jsonPathExpr) {
        requireJsonParsed(jsonPathExpr);
        Boolean actual = jsonPath.getBoolean(jsonPathExpr);
        assertThat(actual)
            .as("Boolean at [%s] should be false", jsonPathExpr)
            .isFalse();
        return this;
    }

    // ════════════════════════════════════════════════════════════════════
    //  Array / collection
    // ════════════════════════════════════════════════════════════════════

    /** Assert JSON array at path is not empty. */
    public JsonResponseValidator assertArrayNotEmpty(String jsonPathExpr) {
        requireJsonParsed(jsonPathExpr);
        List<?> list = jsonPath.getList(jsonPathExpr);
        assertThat(list)
            .as("Array at [%s] should not be empty", jsonPathExpr)
            .isNotNull()
            .isNotEmpty();
        return this;
    }

    /** Assert JSON array has exactly n items. */
    public JsonResponseValidator assertArraySize(String jsonPathExpr, int expectedSize) {
        requireJsonParsed(jsonPathExpr);
        List<?> list = jsonPath.getList(jsonPathExpr);
        assertThat(list)
            .as("Array size at [%s]", jsonPathExpr)
            .hasSize(expectedSize);
        return this;
    }

    /** Assert JSON array contains a specific string value. */
    public JsonResponseValidator assertArrayContains(String jsonPathExpr, String value) {
        requireJsonParsed(jsonPathExpr);
        List<String> list = jsonPath.getList(jsonPathExpr, String.class);
        assertThat(list)
            .as("Array at [%s] should contain [%s]", jsonPathExpr, value)
            .contains(value);
        return this;
    }

    // ════════════════════════════════════════════════════════════════════
    //  DMD business logic (JSON API conventions)
    // ════════════════════════════════════════════════════════════════════

    /**
     * Assert the DMD JSON business status code.
     * Tries both "statusCode" and "responseHeader.statusCode" paths.
     */
    public JsonResponseValidator assertDmdJsonStatus(String expectedCode) {
        requireJsonParsed("statusCode");
        // Try top-level first, then nested under responseHeader
        String actual = jsonPath.getString("statusCode");
        if (actual == null) {
            actual = jsonPath.getString("responseHeader.statusCode");
        }
        if (actual == null) {
            actual = jsonPath.getString("response.statusCode");
        }
        assertThat(actual)
            .as("DMD JSON statusCode")
            .isEqualTo(expectedCode);
        return this;
    }

    /** Assert DMD JSON status = "00" (SUCCESS). */
    public JsonResponseValidator assertDmdJsonSuccess() {
        return assertDmdJsonStatus("00");
    }

    /** Assert DMD JSON message field. Tries multiple common paths. */
    public JsonResponseValidator assertDmdJsonMessage(String expectedMessage) {
        requireJsonParsed("message");
        String actual = jsonPath.getString("message");
        if (actual == null) actual = jsonPath.getString("responseHeader.message");
        if (actual == null) actual = jsonPath.getString("response.message");
        assertThat(actual)
            .as("DMD JSON message")
            .isEqualTo(expectedMessage);
        return this;
    }

    // ════════════════════════════════════════════════════════════════════
    //  Accessors
    // ════════════════════════════════════════════════════════════════════

    /** Extract a string value for use in subsequent steps. */
    public String getString(String jsonPathExpr) {
        requireJsonParsed(jsonPathExpr);
        return jsonPath.getString(jsonPathExpr);
    }

    /** Extract an integer value. */
    public int getInt(String jsonPathExpr) {
        requireJsonParsed(jsonPathExpr);
        return jsonPath.getInt(jsonPathExpr);
    }

    /** Extract a list value. */
    public <T> List<T> getList(String jsonPathExpr, Class<T> type) {
        requireJsonParsed(jsonPathExpr);
        return jsonPath.getList(jsonPathExpr, type);
    }

    /** Extract the full response as a Map (root object). */
    public Map<String, Object> asMap() {
        requireJsonParsed("root");
        return response.jsonPath().getMap("$");
    }

    public int    getHttpStatusCode() { return response.getStatusCode(); }
    public String getRawBody()        { return response.getBody().asString(); }
    public Response getResponse()     { return response; }

    // ════════════════════════════════════════════════════════════════════
    //  Internal helpers
    // ════════════════════════════════════════════════════════════════════

    private void requireJsonParsed(String context) {
        if (jsonPath == null) {
            fail("Cannot evaluate JSON path [" + context + "] — " +
                 "response was not valid JSON.\nHTTP Status: " +
                 response.getStatusCode() + "\nBody: " +
                 response.getBody().asString().substring(
                     0, Math.min(500, response.getBody().asString().length())));
        }
    }
}
