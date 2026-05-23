package config;

public class FrameworkConfig {

    // ==========================================
    // BROWSER
    // ==========================================

    public static final String BROWSER = ConfigLoader.get(
            "browser");

    // ==========================================
    // HEADLESS
    // ==========================================

    public static final boolean HEADLESS = ConfigLoader.getBoolean(
            "headless");

    // ==========================================
    // SESSION TTL
    // ==========================================

    public static final int SESSION_TTL_HOURS = ConfigLoader.getInt(
            "session_ttl_hours");

    // ==========================================
    // PARALLEL THREADS
    // ==========================================

    public static final int THREAD_COUNT = ConfigLoader.getInt(
            "thread_count");
}