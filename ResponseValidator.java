package com.dmd.framework.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.hamcrest.Matchers.*;

public class ResponseValidator {
    private static final Logger logger = LogManager.getLogger(ResponseValidator.class);

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        logger.info("Validating Status Code: Expected=" + expectedStatusCode + ", Actual=" + response.getStatusCode());
        response.then().statusCode(expectedStatusCode);
        logger.info("Status Code validation passed!");
    }

    public static void validateResponseField(Response response, String fieldPath, Object expectedValue) {
        logger.info("Validating Field: " + fieldPath + ", Expected Value: " + expectedValue);
        response.then().body(fieldPath, equalTo(expectedValue));
        logger.info("Field validation passed!");
    }

    public static void validateResponseFieldExists(Response response, String fieldPath) {
        logger.info("Validating Field exists: " + fieldPath);
        response.then().body(fieldPath, notNullValue());
        logger.info("Field existence validation passed!");
    }

    public static void validateResponseFieldContains(Response response, String fieldPath, String value) {
        logger.info("Validating Field contains: " + fieldPath + ", Value: " + value);
        response.then().body(fieldPath, containsString(value));
        logger.info("Field contains validation passed!");
    }

    public static Object getResponseFieldValue(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        logger.info("Retrieved field value - Path: " + fieldPath + ", Value: " + value);
        return value;
    }

    public static String getResponseFieldAsString(Response response, String fieldPath) {
        return response.jsonPath().getString(fieldPath);
    }

    public static int getResponseFieldAsInt(Response response, String fieldPath) {
        return response.jsonPath().getInt(fieldPath);
    }
}
