/**
 * Singleton that loads the correct environment config at startup.
 * Driven by JVM system property:  -Denv=stage|qa|prod  (default: stage)
 *
 * Supports three environments:
 *   stage  → src/test/resources/config/stage.properties
 *   qa     → src/test/resources/config/qa.properties
 *   prod   → src/test/resources/config/prod.properties
 *
 * Each environment carries:
 *   - Default XML base URL
 *   - Per-microservice XML base URL overrides
 *   - Per-microservice JSON API base URLs
 *   - Auth settings for JSON APIs
 */
public class EnvironmentConfig {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentConfig.class);

    private static final EnvironmentConfig INSTANCE = new EnvironmentConfig();
    private final Properties props = new Properties();
    private final String env;

    private EnvironmentConfig() {
        env = System.getProperty("env", "stage").toLowerCase().trim();
        String configFile = "config/" + env + ".properties";
        log.info("Loading environment config: {}", configFile);
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (is == null) {
                throw new RuntimeException(
                    "Config file not found on classpath: " + configFile +
                    " — valid values are: stage, qa, prod");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + configFile, e);
        }
        log.info("Env={} | XML BaseURL={}", env, getBaseUrl());
    }

    public static EnvironmentConfig get() { return INSTANCE; }

    // ── Identity ──────────────────────────────────────────────────────────

    public String getEnv()          { return env; }
    public boolean isStage()        { return "stage".equals(env); }
    public boolean isQa()           { return "qa".equals(env); }
    public boolean isProd()         { return "prod".equals(env); }

    // ── XML API URLs ──────────────────────────────────────────────────────

    /** Default XML base URL (fallback for all services). */
    public String getBaseUrl() {
        return props.getProperty("base.url");
    }

    /**
     * Per-microservice XML base URL.
     * Falls back to getBaseUrl() if no service-specific override is configured.
     *
     * @param microservice e.g. "dmd-device-info", "dmd-data-service"
     */
    public String getServiceBaseUrl(String microservice) {
        String key = "service." + microservice + ".base.url";
        return props.getProperty(key, getBaseUrl());
    }

    // ── JSON API URLs ─────────────────────────────────────────────────────

    /**
     * JSON REST base URL for a specific microservice.
     * e.g. "https://dmdstage-api.verizon.com/device-info/v1"
     *
     * @param microservice e.g. "dmd-device-info"
     */
    public String getJsonBaseUrl(String microservice) {
        String key = "json." + microservice + ".base.url";
        String url = props.getProperty(key);
        if (url == null || url.isBlank()) {
            log.warn("No JSON base URL configured for service '{}' in {} env — " +
                     "add 'json.{}.base.url' to config/{}.properties",
                     microservice, env, microservice, env);
            return getBaseUrl();  // safe fallback
        }
        return url;
    }

    // ── Auth (JSON APIs) ──────────────────────────────────────────────────

    public boolean isJsonAuthEnabled() {
        return Boolean.parseBoolean(props.getProperty("json.auth.enabled", "false"));
    }

    /** Static Bearer token (used when OAuth client-credentials is not in play). */
    public String getJsonAuthToken() {
        return props.getProperty("json.auth.token", "");
    }

    public String getJsonAuthClientId() {
        return props.getProperty("json.auth.client.id", "");
    }

    public String getJsonAuthClientSecret() {
        return props.getProperty("json.auth.client.secret", "");
    }

    public String getJsonAuthTokenUrl() {
        return props.getProperty("json.auth.token.url", "");
    }

    // ── Timeouts ──────────────────────────────────────────────────────────

    public int getConnectionTimeout() { return intProp("connection.timeout", 30000); }
    public int getReadTimeout()       { return intProp("read.timeout",       60000); }

    // ── Email ─────────────────────────────────────────────────────────────

    public String   getEmailSmtpHost()      { return props.getProperty("email.smtp.host", ""); }
    public int      getEmailSmtpPort()      { return intProp("email.smtp.port", 587); }
    public String   getEmailFrom()          { return props.getProperty("email.from", ""); }
    public String[] getEmailTo()            { return props.getProperty("email.to", "").split(","); }
    public String   getEmailSubjectPrefix() { return props.getProperty("email.subject.prefix", "[DMD]"); }

    // ── Reports ───────────────────────────────────────────────────────────

    public String getReportDir()    { return props.getProperty("report.dir", "target/extent-reports"); }
    public String getMicroservice() { return System.getProperty("microservice", "all"); }

    // ── Helpers ───────────────────────────────────────────────────────────

    private int intProp(String key, int def) {
        try { return Integer.parseInt(props.getProperty(key, String.valueOf(def))); }
        catch (NumberFormatException e) { return def; }
    }
}
