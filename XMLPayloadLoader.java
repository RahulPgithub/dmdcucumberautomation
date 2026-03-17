package com.dmd.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XMLPayloadLoader {
    private static final Logger logger = LogManager.getLogger(XMLPayloadLoader.class);
    private static final String PAYLOAD_BASE_PATH = "src/test/resources/payloads/";

    public static String loadXMLPayload(String service, String payloadFileName) {
        try {
            String filePath = PAYLOAD_BASE_PATH + service + "/" + payloadFileName;
            String payload = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            logger.info("XML Payload loaded successfully: " + filePath);
            return payload;
        } catch (IOException e) {
            logger.error("Failed to load XML payload: " + payloadFileName, e);
            throw new RuntimeException("Unable to load XML payload: " + payloadFileName, e);
        }
    }

    public static String loadXMLPayloadWithReplacement(String service, String payloadFileName, 
                                                       String searchStr, String replaceStr) {
        String payload = loadXMLPayload(service, payloadFileName);
        return payload.replace(searchStr, replaceStr);
    }
}
