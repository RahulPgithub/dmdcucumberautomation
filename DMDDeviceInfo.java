package com.dmd.framework.api;

import com.dmd.framework.config.EnvironmentConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.restassured.response.Response;

public class DMDDeviceInfoServiceAPI extends BaseAPIClient {
    private static final Logger logger = LogManager.getLogger(DMDDeviceInfoServiceAPI.class);

    private static final String BASE_URL = EnvironmentConfig.getDeviceInfoServiceBaseUrl();
    private static final String API_VERSION = EnvironmentConfig.getDeviceInfoServiceApiVersion();
    private static final String GET_ENDPOINT = EnvironmentConfig.getDeviceInfoServiceGetEndpoint();
    private static final String POST_ENDPOINT = EnvironmentConfig.getDeviceInfoServicePostEndpoint();

    public Response getAllDeviceInfo() {
        logger.info("Fetching all device info from DMD DeviceInfo Service");
        String endpoint = API_VERSION + GET_ENDPOINT;
        return performGetRequest(BASE_URL, endpoint);
    }

    public Response getDeviceInfoById(String deviceId) {
        logger.info("Fetching device info with ID: " + deviceId);
        String endpoint = API_VERSION + GET_ENDPOINT + "/" + deviceId;
        return performGetRequest(BASE_URL, endpoint);
    }

    public Response createDeviceInfo(String xmlPayload) {
        logger.info("Creating new device info with payload");
        String endpoint = API_VERSION + POST_ENDPOINT;
        return performPostRequest(BASE_URL, endpoint, xmlPayload);
    }

    public Response updateDeviceInfo(String deviceId, String xmlPayload) {
        logger.info("Updating device info with ID: " + deviceId);
        String endpoint = API_VERSION + GET_ENDPOINT + "/" + deviceId;
        return performPutRequest(BASE_URL, endpoint, xmlPayload);
    }

    public Response deleteDeviceInfo(String deviceId) {
        logger.info("Deleting device info with ID: " + deviceId);
        String endpoint = API_VERSION + GET_ENDPOINT + "/" + deviceId;
        return performDeleteRequest(BASE_URL, endpoint);
    }
}
