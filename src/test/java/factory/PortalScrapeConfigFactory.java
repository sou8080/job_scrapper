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

                return new PortalScrapeConfig(

                        // ==========================================
                        // JOB CARD
                        // ==========================================

                        ".base-card.job-search-card",

                        // ==========================================
                        // COMPANY
                        // ==========================================

                        ".base-search-card__subtitle",

                        // ==========================================
                        // TITLE
                        // ==========================================

                        ".base-search-card__title",

                        // ==========================================
                        // LOCATION
                        // ==========================================

                        ".job-search-card__location",

                        // ==========================================
                        // POSTED DATE
                        // ==========================================

                        ".job-search-card__listdate, .job-search-card__listdate--new",

                        // ==========================================
                        // APPLY LINK
                        // ==========================================

                        "a.base-card__full-link");

            // ==========================================
            // NAUKRI
            // ==========================================

            case NAUKRI:

                return new PortalScrapeConfig(

                        // JOB CARD
                        ".srp-jobtuple-wrapper",

                        // COMPANY
                        ".comp-name",

                        // TITLE
                        ".title",

                        // LOCATION
                        ".locWdth",

                        // POSTED DATE
                        ".job-post-day",

                        // APPLY LINK
                        "a");

            // ==========================================
            // INDEED
            // ==========================================

            case INDEED:

                return new PortalScrapeConfig(

                        // JOB CARD
                        ".job_seen_beacon, .result",

                        // COMPANY
                        "[data-testid='company-name'], .companyName, .company_name",

                        // TITLE
                        "h2.jobTitle a span, h2.jobTitle span:not([class*='accessibility']), .jcs-JobTitle, a.jcs-JobTitle",

                        // LOCATION
                        "[data-testid='text-location'], .companyLocation, .location",

                        // POSTED DATE
                        "[class*='date']:not(a), .result-footer .date, [data-testid*='date'], span[id*='date']",

                        // APPLY LINK
                        "h2.jobTitle a, a.jcs-JobTitle, a[data-jk]");

            // ==========================================
            // SHINE
            // ==========================================

            case SHINE:

                return new PortalScrapeConfig(

                        ".jobCardNova_bigCard__W2xn3",

                        ".jobCardNova_bigCardTopTitleName__M_W_m",

                        "h3.jobCardNova_bigCardTopTitleHeading__Rj2sC a",

                        ".jobCardNova_bigCardCenterListLoc__usiPB span",

                        ".jobCardNova_postedData__LTERc",

                        "h3.jobCardNova_bigCardTopTitleHeading__Rj2sC a");

            // ==========================================
            // FOUNDIT
            // ==========================================

            case FOUNDIT:

                return new PortalScrapeConfig(

                        // JOB CARD
                        ".cardContainer",

                        // COMPANY
                        ".companyName p",

                        // TITLE
                        ".jobTitle",

                        // LOCATION
                        ".details.location",

                        // POSTED DATE
                        ".jobAddedTime .timeText",

                        // APPLY LINK
                        ".jobTitle");

            // ==========================================
            // DEFAULT
            // ==========================================

            default:

                throw new RuntimeException(
                        "Unsupported portal : "
                                + portal);
        }
    }
}