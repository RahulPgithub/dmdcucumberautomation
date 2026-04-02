cat > /home/claude/dmd-testng-framework/src/test/java/com/dmd/api/utils/UrlBuilder.java << 'JAVA'
package com.dmd.api.utils;

import com.dmd.api.config.EnvironmentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts Excel / hardcoded prod URLs to the target environment URL at runtime.
 *
 * Supports both:
 *   - XML API URLs  (base.url / service.{name}.base.url)
 *   - JSON API URLs (json.{name}.base.url)
 *
 * All feature-file URLs reference the prod host — this class swaps the host
 * with whatever is configured in config/{env}.properties.
 */
public class UrlBuilder {

    private static final Logger log = LoggerFactory.getLogger(UrlBuilder.class);

    private static final String PROD_XML_BASE  = "https://dmdprod-awswest.verizon.com/dmd";
    private static final String PROD_JSON_BASE = "https://dmdprod-api.verizon.com";

    private UrlBuilder() {}

    // ── XML URL helpers ───────────────────────────────────────────────────

    /**
     * Replace prod XML base with the environment-specific base URL.
     * Uses service-specific override when microservice is provided.
     */
    public static String toEnvUrl(String excelUrl) {
        String envBase = EnvironmentConfig.get().getBaseUrl();
        String result  = excelUrl.replace(PROD_XML_BASE, envBase);
        log.debug("XML URL → {}", result.substring(0, Math.min(100, result.length())));
        return result;
    }

    /**
     * Replace prod XML base with per-microservice environment URL.
     * Use this when a service may live on a different host.
     */
    public static String toEnvUrl(String excelUrl, String microservice) {
        String envBase = EnvironmentConfig.get().getServiceBaseUrl(microservice);
        String result  = excelUrl.replace(PROD_XML_BASE, envBase);
        log.debug("XML URL [{}] → {}", microservice,
                  result.substring(0, Math.min(100, result.length())));
        return result;
    }

    // ── JSON URL helpers ──────────────────────────────────────────────────

    /**
     * Build a full JSON API URL for a given microservice and path.
     * e.g. buildJsonUrl("dmd-device-info", "/devices/123")
     *      → "https://dmdstage-api.verizon.com/device-info/v1/devices/123"
     */
    public static String buildJsonUrl(String microservice, String path) {
        String base   = EnvironmentConfig.get().getJsonBaseUrl(microservice);
        String prefix = path.startsWith("/") ? "" : "/";
        String url    = base + prefix + path;
        log.debug("JSON URL [{}] → {}", microservice, url);
        return url;
    }

    /**
     * Replace prod JSON base host with the environment-specific JSON base URL.
     * Used when JSON URLs are hardcoded in feature files pointing at prod.
     */
    public static String toEnvJsonUrl(String prodUrl, String microservice) {
        String envBase  = EnvironmentConfig.get().getJsonBaseUrl(microservice);

        // Strip the prod base prefix and attach the env base
        String path = prodUrl.contains(PROD_JSON_BASE)
            ? prodUrl.substring(prodUrl.indexOf(PROD_JSON_BASE) + PROD_JSON_BASE.length())
            : prodUrl;

        String result = envBase + (path.startsWith("/") ? path : "/" + path);
        log.debug("JSON URL adapted → {}", result);
        return result;
    }

    // ── Path / query string helpers ───────────────────────────────────────

    /** Extract just the path segment, stripping base URL and query string. */
    public static String extractPath(String fullUrl) {
        String stripped = fullUrl
            .replace(PROD_XML_BASE, "")
            .replace(EnvironmentConfig.get().getBaseUrl(), "");
        int q = stripped.indexOf('?');
        return q >= 0 ? stripped.substring(0, q) : stripped;
    }

    /** Parse query parameters from URL into an ordered map. */
    public static Map<String, String> extractQueryParams(String fullUrl) {
        Map<String, String> params = new LinkedHashMap<>();
        int q = fullUrl.indexOf('?');
        if (q < 0) return params;
        String qs = fullUrl.substring(q + 1);

        // Split on & but not inside XML tags
        for (String token : qs.split("&(?![^<>]*>)")) {
            int eq = token.indexOf('=');
            if (eq <= 0) continue;
            String key = token.substring(0, eq);
            String val = token.substring(eq + 1);
            try { val = URLDecoder.decode(val, StandardCharsets.UTF_8); }
            catch (Exception ignored) {}
            params.put(key, val);
        }
        return params;
    }
}
