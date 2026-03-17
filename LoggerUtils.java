package com.dmd.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtils {
    private static final Logger logger = LogManager.getLogger(LoggerUtils.class);

    public static void info(String message) {
        logger.info(message);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void logRequest(String endpoint, String method, String payload) {
        logger.info("========== API REQUEST ==========");
        logger.info("Endpoint: " + endpoint);
        logger.info("Method: " + method);
        logger.info("Payload: " + payload);
        logger.info("================================");
    }

    public static void logResponse(int statusCode, String responseBody) {
        logger.info("========== API RESPONSE ==========");
        logger.info("Status Code: " + statusCode);
        logger.info("Response Body: " + responseBody);
        logger.info("=================================");
    }
}
