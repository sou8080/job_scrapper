package config;

import utilities.JobPortal;

public class PortalConfig {

    // ==========================================
    // PORTAL DIRECT URL
    // ==========================================

    public static String getUrl(
            JobPortal portal) {

        switch (portal) {

            case NAUKRI:

                return ConfigLoader.get("naukri_url");

            case LINKEDIN:

                return ConfigLoader.get("linkedin_url");

            case INDEED:

                return ConfigLoader.get("indeed_url");

            case SHINE:

                return ConfigLoader.get("shine_url");

            case FOUNDIT:

                return ConfigLoader.get("foundit_url");

            case TIMESJOBS:

                return ConfigLoader.get("timesjobs_url");

            default:

                throw new RuntimeException("Invalid portal.");
        }
    }
}