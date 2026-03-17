package com.dmd.framework.api;

import com.dmd.framework.config.EnvironmentConfig;
import com.dmd.framework.utils.LoggerUtils;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DMDFeaturesServiceAPI extends BaseAPIClient {
    private static final Logger logger = LogManager.getLogger(DMDFeaturesServiceAPI.class);

    private static final String BASE_URL = EnvironmentConfig.getFeaturesServiceBaseUrl();
    private static final String API_VERSION = EnvironmentConfig.getFeaturesServiceApiVersion();
    private static final String GET_ENDPOINT = EnvironmentConfig.getFeaturesServiceGetEndpoint();
    private static final String POST_ENDPOINT = EnvironmentConfig.getFeaturesServicePostEndpoint();

    public Response getAllFeatures() {
        logger.info("Fetching all features from DMD Features Service");
        String endpoint = API_VERSION + GET_ENDPOINT;
        return performGetRequest(BASE_URL, endpoint);
    }

    public Response getFeatureById(String featureId) {
        logger.info("Fetching feature with ID: " + featureId);
        String endpoint = API_VERSION + GET_ENDPOINT + "/" + featureId;
        return performGetRequest(BASE_URL, endpoint);
    }

    public Response createFeature(String xmlPayload) {
        logger.info("Creating new feature with payload");
        String endpoint = API_VERSION + POST_ENDPOINT;
        return performPostRequest(BASE_URL, endpoint, xmlPayload);
    }

    public Response updateFeature(String featureId, String xmlPayload) {
        logger.info("Updating feature with ID: " + featureId);
        String endpoint = API_VERSION + GET_ENDPOINT + "/" + featureId;
        return performPutRequest(BASE_URL, endpoint, xmlPayload);
    }

    public Response deleteFeature(String featureId) {
        logger.info("Deleting feature with ID: " + featureId);
        String endpoint = API_VERSION + GET_ENDPOINT + "/" + featureId;
        return performDeleteRequest(BASE_URL, endpoint);
    }
}
