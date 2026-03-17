package com.dmd.framework.reporting;

import com.dmd.framework.config.EnvironmentConfig;
import com.dmd.framework.utils.FileUtils;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestResultsWriter {
    private static final Logger logger = LogManager.getLogger(TestResultsWriter.class);

    public static void writeTestResult(String testName, String service, String endpoint, 
                                       String method, String requestPayload, Response response,
                                       boolean isPass, String failureReason) {
        String reportPath = EnvironmentConfig.getReportOutputPath() + "/test-results.txt";
        FileUtils.createDirectoryIfNotExists(EnvironmentConfig.getReportOutputPath());
        
        StringBuilder resultContent = new StringBuilder();
        resultContent.append("\n====================================== TEST RESULT ======================================\n");
        resultContent.append("Test Name: ").append(testName).append("\n");
        resultContent.append("Service: ").append(service).append("\n");
        resultContent.append("Endpoint: ").append(endpoint).append("\n");
        resultContent.append("Method: ").append(method).append("\n");
        resultContent.append("Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        resultContent.append("\n--- REQUEST ---\n");
        resultContent.append(requestPayload).append("\n");
        resultContent.append("\n--- RESPONSE ---\n");
        resultContent.append("Status Code: ").append(response.getStatusCode()).append("\n");
        resultContent.append("Response Body: \n").append(response.prettyPrint()).append("\n");
        resultContent.append("\n--- TEST RESULT ---\n");
        resultContent.append("Status: ").append(isPass ? "PASS" : "FAIL").append("\n");
        if (!isPass) {
            resultContent.append("Failure Reason: ").append(failureReason).append("\n");
        }
        resultContent.append("================================================================================================\n");
        
        FileUtils.appendToFile(reportPath, resultContent.toString());
        logger.info("Test result written to: " + reportPath);
    }

    public static void writeTestResult(String testName, String service, String endpoint, 
                                       String method, String requestPayload, Response response,
                                       boolean isPass) {
        writeTestResult(testName, service, endpoint, method, requestPayload, response, isPass, "");
    }
}
