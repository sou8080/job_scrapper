package factory;

import models.PortalScrapeConfig;
import utilities.JobPortal;

public class PortalScrapeConfigFactory {

    public static PortalScrapeConfig getConfig(
            JobPortal portal) {

        switch (portal) {

            // ==========================================
            // LINKEDIN
            // ==========================================

            case LINKEDIN:

                return new PortalScrapeConfig(".base-card.job-search-card", ".base-search-card__subtitle",
                        ".base-search-card__title", ".job-search-card__location",
                        ".job-search-card__listdate, .job-search-card__listdate--new", "a.base-card__full-link");

            // ==========================================
            // NAUKRI
            // ==========================================

            case NAUKRI:

                return new PortalScrapeConfig(".srp-jobtuple-wrapper", ".comp-name", ".title", ".locWdth",
                        ".job-post-day", "a");

            // ==========================================
            // INDEED
            // ==========================================

            case INDEED:

                return new PortalScrapeConfig(".job_seen_beacon, .result",
                        "[data-testid='company-name'], .companyName, .company_name",
                        "h2.jobTitle a span, h2.jobTitle span:not([class*='accessibility']), .jcs-JobTitle, a.jcs-JobTitle",
                        "[data-testid='text-location'], .companyLocation, .location",
                        "[class*='date']:not(a), .result-footer .date, [data-testid*='date'], span[id*='date']",
                        "h2.jobTitle a, a.jcs-JobTitle, a[data-jk]");

            // ==========================================
            // SHINE
            // ==========================================

            case SHINE:

                return new PortalScrapeConfig(".jobCardNova_bigCard__W2xn3", ".jobCardNova_bigCardTopTitleName__M_W_m",
                        "h3.jobCardNova_bigCardTopTitleHeading__Rj2sC a",
                        ".jobCardNova_bigCardCenterListLoc__usiPB span",
                        ".jobCardNova_postedData__LTERc", "h3.jobCardNova_bigCardTopTitleHeading__Rj2sC a");

            // ==========================================
            // FOUNDIT
            // ==========================================

            case FOUNDIT:

                return new PortalScrapeConfig(".cardContainer", ".companyName p", ".jobTitle", ".details.location",
                        ".jobAddedTime .timeText", ".jobTitle");

            // ==========================================
            // TIMESJOBS
            // ==========================================

            case TIMESJOBS:

                return new PortalScrapeConfig(

                        // ==========================================
                        // JOB CARD
                        // ==========================================

                        ".srp-card, div[class*='srp-card'], div.rounded-xl.mb-4.shadow-sm.relative",

                        // ==========================================
                        // COMPANY
                        // ==========================================

                        "div.text-xs.text-gray-400.flex.items-center span:first-child, "
                                +
                                "div[class*='text-gray-400'] span:first-child, "
                                +
                                ".company-name, "
                                +
                                "span[title]",

                        // ==========================================
                        // ROLE
                        // ==========================================

                        "h2, "
                                +
                                "h2[class*='font-bold'], "
                                +
                                "h2.text-sm, "
                                +
                                "h2.text-base",

                        // ==========================================
                        // LOCATION
                        // ==========================================

                        "span.font-semibold:has(i.locations-icon), "
                                +
                                "span[class*='font-semibold']",

                        // ==========================================
                        // POSTED DATE
                        // ==========================================

                        "div.text-xs.text-gray-400.flex.items-center, "
                                +
                                "div[class*='text-gray-400'][class*='items-center'], "
                                +
                                "div[class*='posted'], "
                                +
                                "span[class*='posted']",

                        // ==========================================
                        // APPLY LINK
                        // ==========================================

                        "a.absolute.left-0.right-0.top-0.bottom-0.z-20, "
                                +
                                "a[href*='job-detail'], "
                                +
                                "a[target='_blank']");

            // ==========================================
            // DEFAULT
            // ==========================================

            default:

                throw new RuntimeException("Unsupported portal : " + portal);
        }
    }
}