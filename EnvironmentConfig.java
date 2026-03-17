package com.dmd.framework.config;

public class EnvironmentConfig {
    
    // DMD Features Service Configuration
    public static String getFeaturesServiceBaseUrl() {
        return ConfigLoader.getProperty("dmd.features.service.base.url");
    }

    public static String getFeaturesServiceApiVersion() {
        return ConfigLoader.getProperty("dmd.features.service.api.version");
    }

    public static String getFeaturesServiceGetEndpoint() {
        return ConfigLoader.getProperty("dmd.features.service.get.endpoint");
    }

    public static String getFeaturesServicePostEndpoint() {
        return ConfigLoader.getProperty("dmd.features.service.post.endpoint");
    }

    // DMD DeviceInfo Service Configuration
    public static String getDeviceInfoServiceBaseUrl() {
        return ConfigLoader.getProperty("dmd.deviceinfo.service.base.url");
    }

    public static String getDeviceInfoServiceApiVersion() {
        return ConfigLoader.getProperty("dmd.deviceinfo.service.api.version");
    }

    public static String getDeviceInfoServiceGetEndpoint() {
        return ConfigLoader.getProperty("dmd.deviceinfo.service.get.endpoint");
    }

    public static String getDeviceInfoServicePostEndpoint() {
        return ConfigLoader.getProperty("dmd.deviceinfo.service.post.endpoint");
    }

    // API Configuration
    public static int getApiTimeout() {
        return ConfigLoader.getIntProperty("api.timeout", 30);
    }

    public static int getConnectionTimeout() {
        return ConfigLoader.getIntProperty("api.connection.timeout", 15);
    }

    public static int getSocketTimeout() {
        return ConfigLoader.getIntProperty("api.socket.timeout", 15);
    }

    public static String getRequestContentType() {
        return ConfigLoader.getProperty("request.content.type", "application/xml");
    }

    public static String getResponseContentType() {
        return ConfigLoader.getProperty("response.content.type", "application/json");
    }

    public static String getReportOutputPath() {
        return ConfigLoader.getProperty("report.output.path", "./reports");
    }
}
