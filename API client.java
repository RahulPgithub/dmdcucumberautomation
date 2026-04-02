cat > /home/claude/dmd-testng-framework/src/test/java/com/dmd/api/utils/ApiClient.java << 'JAVA'
package com.dmd.api.utils;

import com.dmd.api.config.EnvironmentConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Central HTTP client for both XML and JSON DMD APIs.
 *
 * Two distinct request specs are maintained:
 *   xmlSpec  — for XML APIs (existing DMD pattern, no auth header)
 *   jsonSpec — for JSON REST APIs (Accept: application/json, optional Bearer token)
 *
 * Per-microservice JSON base URLs are resolved via EnvironmentConfig
 * so stage / qa / prod all point to the correct host automatically.
 */
public class ApiClient {

    private static final Logger log = LoggerFactory.getLogger(ApiClient.class);

    private static final ApiClient INSTANCE = new ApiClient();

    private final RestAssuredConfig   raConfig;
    private final RequestSpecification xmlBaseSpec;
    private final RequestSpecification jsonBaseSpec;

    private ApiClient() {
        EnvironmentConfig cfg = EnvironmentConfig.get();

        raConfig = RestAssured.config()
            .httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", cfg.getConnectionTimeout())
                .setParam("http.socket.timeout",     cfg.getReadTimeout()));

        // ── XML spec (legacy DMD XML APIs) ────────────────────────────────
        xmlBaseSpec = new RequestSpecBuilder()
            .setBaseUri(cfg.getBaseUrl())
            .setConfig(raConfig)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .build();

        // ── JSON spec (REST/JSON APIs) ────────────────────────────────────
        RequestSpecBuilder jsonBuilder = new RequestSpecBuilder()
            .setConfig(raConfig)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter());

        // Attach Bearer token if auth is enabled in the current environment
        if (cfg.isJsonAuthEnabled()) {
            String token = cfg.getJsonAuthToken();
            if (token != null && !token.isBlank() && !token.endsWith("_TOKEN_HERE")) {
                jsonBuilder.addHeader("Authorization", "Bearer " + token);
                log.info("JSON auth: Bearer token attached (env={})", cfg.getEnv());
            } else {
                log.warn("JSON auth enabled but token not set for env='{}' — " +
                         "set json.auth.token in config/{}.properties or via env var",
                         cfg.getEnv(), cfg.getEnv());
            }
        }

        jsonBaseSpec = jsonBuilder.build();
        log.info("ApiClient ready — xmlBase={} | env={}", cfg.getBaseUrl(), cfg.getEnv());
    }

    public static ApiClient get() { return INSTANCE; }

    // ════════════════════════════════════════════════════════════
    //  XML API methods
    // ════════════════════════════════════════════════════════════

    /** GET with explicit query params (XML API). */
    public Response get(String path, Map<String, String> queryParams) {
        RequestSpecification spec = given().spec(xmlBaseSpec);
        if (queryParams != null && !queryParams.isEmpty()) {
            spec.queryParams(queryParams);
        }
        return spec.when().get(path).then().extract().response();
    }

    /**
     * GET using a fully-qualified URL (XML payload embedded as query param).
     * Used for the classic DMD XML-over-HTTP pattern.
     */
    public Response getFromFullUrl(String fullUrl) {
        return given()
            .config(raConfig)
            .filter(new RequestLoggingFilter())
            .filter(new ResponseLoggingFilter())
            .when()
            .get(fullUrl)
            .then()
            .extract()
            .response();
    }

    // ════════════════════════════════════════════════════════════
    //  JSON API methods
    // ════════════════════════════════════════════════════════════

    /**
     * JSON GET — builds the URL from microservice name + path.
     * Base URL resolved per environment from config.
     *
     * @param microservice e.g. "dmd-device-info"
     * @param path         e.g. "/devices/123" or "/devices?deviceId=123"
     */
    public Response jsonGet(String microservice, String path) {
        String url = UrlBuilder.buildJsonUrl(microservice, path);
        log.info("JSON GET → {}", url);
        return given()
            .spec(jsonBaseSpec)
            .when()
            .get(url)
            .then()
            .extract()
            .response();
    }

    /**
     * JSON GET with additional query parameters.
     */
    public Response jsonGet(String microservice, String path,
                            Map<String, String> queryParams) {
        String url = UrlBuilder.buildJsonUrl(microservice, path);
        log.info("JSON GET → {} | params={}", url, queryParams);
        RequestSpecification spec = given().spec(jsonBaseSpec);
        if (queryParams != null && !queryParams.isEmpty()) {
            spec.queryParams(queryParams);
        }
        return spec.when().get(url).then().extract().response();
    }

    /**
     * JSON GET using a full pre-built URL (e.g. from feature file).
     * Useful when the full URL including host is defined in the feature.
     */
    public Response jsonGetFromFullUrl(String fullUrl) {
        log.info("JSON GET (full URL) → {}", fullUrl);
        return given()
            .spec(jsonBaseSpec)
            .when()
            .get(fullUrl)
            .then()
            .extract()
            .response();
    }

    /**
     * JSON POST with a body.
     */
    public Response jsonPost(String microservice, String path, String jsonBody) {
        String url = UrlBuilder.buildJsonUrl(microservice, path);
        log.info("JSON POST → {}", url);
        return given()
            .spec(jsonBaseSpec)
            .body(jsonBody)
            .when()
            .post(url)
            .then()
            .extract()
            .response();
    }

    /**
     * JSON GET with a custom override token (per-request auth).
     * Useful when different services use different tokens.
     */
    public Response jsonGetWithToken(String fullUrl, String bearerToken) {
        log.info("JSON GET (custom token) → {}", fullUrl);
        return given()
            .spec(jsonBaseSpec)
            .header("Authorization", "Bearer " + bearerToken)
            .when()
            .get(fullUrl)
            .then()
            .extract()
            .response();
    }
}
