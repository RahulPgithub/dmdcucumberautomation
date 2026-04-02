cat > /home/claude/dmd-testng-framework/src/test/java/com/dmd/api/model/ScenarioContext.java << 'JAVA'
package com.dmd.api.model;

import io.restassured.response.Response;
import lombok.Data;

/**
 * Shared state injected between step-definition classes via Cucumber PicoContainer.
 * A fresh instance is created for every scenario — no cross-scenario leakage.
 *
 * Holds state for both XML and JSON API scenarios.
 */
@Data
public class ScenarioContext {

    // ── Request context ───────────────────────────────────────────────────
    private String   currentUrl;
    private String   currentApiName;
    private String   currentMicroservice;
    private String   responseType;         // "xml" | "json"

    // ── Response ──────────────────────────────────────────────────────────
    private Response currentResponse;

    // ── Dynamic data fields (XML and JSON scenarios) ──────────────────────
    private String deviceId;
    private String simId;
    private String macId;
    private String meid;
    private String appType;
    private String clientId;
    private String dacc;
    private String sacc;
    private String requestBody;           // for JSON POST scenarios

    // ── JSON-specific context ─────────────────────────────────────────────
    private String jsonPath;              // last extracted JSON path value
    private String jsonRequestBody;       // POST/PUT body for JSON APIs
    private String overrideToken;         // per-scenario bearer token override
}
JAVA
