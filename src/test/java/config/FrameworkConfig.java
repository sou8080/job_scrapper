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
        // TELEGRAM
        // ==========================================

        public static final String TELEGRAM_BOT_TOKEN = ConfigLoader.get("telegram_bot_token");
        public static final String TELEGRAM_CHAT_ID = ConfigLoader.get("telegram_chat_id");

}