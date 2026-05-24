package config;

public class FrameworkConfig {

        // ==========================================
        // BROWSER
        // ==========================================

        public static final String BROWSER = ConfigLoader.get("browser");

        // ==========================================
        // HEADLESS
        // ==========================================

        public static final boolean HEADLESS = ConfigLoader.getBoolean("headless");

        // ==========================================
        // TELEGRAM
        // ==========================================

        public static final String TELEGRAM_BOT_TOKEN = ConfigLoader.get("telegram_bot_token");
        public static final String TELEGRAM_CHAT_ID = ConfigLoader.get("telegram_chat_id");

        // ==========================================
        // RETRIES AND TIMEOUTS
        // ==========================================

        public static final int MAX_RETRIES = ConfigLoader.getIntOrDefault("max_retries", 3);
        public static final int DEFAULT_TIMEOUT = ConfigLoader.getIntOrDefault("default_timeout", 30000);
        public static final int NAVIGATION_TIMEOUT = ConfigLoader.getIntOrDefault("navigation_timeout", 45000);

}