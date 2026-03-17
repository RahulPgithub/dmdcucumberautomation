package com.dmd.framework.config;

import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class TestContext {
    private Response lastResponse;
    private String lastRequestPayload;
    private String lastEndpoint;
    private Map<String, Object> testData;
    private Map<String, String> responseData;

    public TestContext() {
        this.testData = new HashMap<>();
        this.responseData = new HashMap<>();
    }

    // Getters and Setters
    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }

    public String getLastRequestPayload() {
        return lastRequestPayload;
    }

    public void setLastRequestPayload(String payload) {
        this.lastRequestPayload = payload;
    }

    public String getLastEndpoint() {
        return lastEndpoint;
    }

    public void setLastEndpoint(String endpoint) {
        this.lastEndpoint = endpoint;
    }

    public void setTestData(String key, Object value) {
        testData.put(key, value);
    }

    public Object getTestData(String key) {
        return testData.get(key);
    }

    public void setResponseData(String key, String value) {
        responseData.put(key, value);
    }

    public String getResponseData(String key) {
        return responseData.get(key);
    }

    public Map<String, Object> getAllTestData() {
        return testData;
    }

    public Map<String, String> getAllResponseData() {
        return responseData;
    }

    public void clearContext() {
        testData.clear();
        responseData.clear();
        lastResponse = null;
        lastRequestPayload = null;
        lastEndpoint = null;
    }
}
