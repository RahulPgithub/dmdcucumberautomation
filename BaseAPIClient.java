package com.dmd.framework.api;

import com.dmd.framework.config.EnvironmentConfig;
import com.dmd.framework.utils.LoggerUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseAPIClient {

    protected RequestSpecification getRequestSpecification() {
        return RestAssured.given()
                .contentType(ContentType.XML)
                .accept(ContentType.JSON)
                .timeout(EnvironmentConfig.getApiTimeout() * 1000L);
    }

    protected Response performGetRequest(String baseUrl, String endpoint) {
        String fullUrl = baseUrl + endpoint;
        LoggerUtils.logRequest(fullUrl, "GET", "");
        
        Response response = getRequestSpecification()
                .when()
                .get(fullUrl);
        
        LoggerUtils.logResponse(response.getStatusCode(), response.asString());
        return response;
    }

    protected Response performPostRequest(String baseUrl, String endpoint, String payload) {
        String fullUrl = baseUrl + endpoint;
        LoggerUtils.logRequest(fullUrl, "POST", payload);
        
        Response response = getRequestSpecification()
                .body(payload)
                .when()
                .post(fullUrl);
        
        LoggerUtils.logResponse(response.getStatusCode(), response.asString());
        return response;
    }

    protected Response performPutRequest(String baseUrl, String endpoint, String payload) {
        String fullUrl = baseUrl + endpoint;
        LoggerUtils.logRequest(fullUrl, "PUT", payload);
        
        Response response = getRequestSpecification()
                .body(payload)
                .when()
                .put(fullUrl);
        
        LoggerUtils.logResponse(response.getStatusCode(), response.asString());
        return response;
    }

    protected Response performDeleteRequest(String baseUrl, String endpoint) {
        String fullUrl = baseUrl + endpoint;
        LoggerUtils.logRequest(fullUrl, "DELETE", "");
        
        Response response = getRequestSpecification()
                .when()
                .delete(fullUrl);
        
        LoggerUtils.logResponse(response.getStatusCode(), response.asString());
        return response;
    }
}
