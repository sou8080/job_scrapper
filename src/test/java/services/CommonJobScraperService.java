package services;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import models.JobModel;

public class CommonJobScraperService {

        // ==========================================
        // SCRAPE JOBS
        // ==========================================

        public static void scrapeJobs(

                        JobFilterService filterService,
                        Page page,
                        String portal,
                        String jobCardSelector,
                        String companySelector,
                        String roleSelector,
                        String locationSelector,
                        String postedSelector,
                        String linkSelector) {

                Locator jobCards = page.locator(jobCardSelector);

                int count = jobCards.count();

                System.out.println(
                                portal + " jobs found : " + count);

                for (int i = 0; i < count; i++) {

                        try {

                                Locator card = jobCards.nth(i);

                                if (i == 0) {

                                        System.out.println(
                                                        "DEBUG "
                                                                        + portal
                                                                        + " Card HTML: "
                                                                        + card.innerHTML());
                                }

                                // ==========================================
                                // FAST EXTRACTION
                                // ==========================================

                                String company = fastText(
                                                card,
                                                companySelector);

                                String role = fastText(
                                                card,
                                                roleSelector);

                                String location = fastText(
                                                card,
                                                locationSelector);

                                String posted = fastText(
                                                card,
                                                postedSelector);

                                // ==========================================
                                // TIMESJOBS DATE CLEANUP
                                // ==========================================

                                if (portal.equalsIgnoreCase(
                                                "TimesJobs")) {

                                        try {

                                                java.util.regex.Matcher matcher = java.util.regex.Pattern
                                                                .compile(
                                                                                "(\\d{2}[-/]\\d{2}[-/]\\d{4})")
                                                                .matcher(
                                                                                posted);

                                                if (matcher.find()) {

                                                        posted = matcher.group(1)
                                                                        .trim();

                                                } else {

                                                        posted = "N/A";
                                                }

                                        } catch (Exception ignored) {

                                                posted = "N/A";
                                        }
                                }

                                String jobUrl = fastAttribute(
                                                card,
                                                linkSelector,
                                                "href");

                                // ==========================================
                                // LINKEDIN URL FIX
                                // ==========================================

                                if (portal.equalsIgnoreCase(
                                                "LinkedIn")
                                                && (jobUrl.equals("N/A")
                                                                || jobUrl.isEmpty()
                                                                || jobUrl.startsWith(
                                                                                "N/A"))) {

                                        String compKey = card.getAttribute(
                                                        "componentkey");

                                        if (compKey == null
                                                        || compKey.equals(
                                                                        "N/A")) {

                                                Locator ckDiv = card.locator(
                                                                "div[componentkey]")
                                                                .first();

                                                if (ckDiv.count() > 0) {

                                                        compKey = ckDiv.getAttribute(
                                                                        "componentkey");
                                                }
                                        }

                                        if (compKey != null
                                                        && !compKey.isEmpty()
                                                        && !compKey.equals(
                                                                        "N/A")) {

                                                java.util.regex.Pattern p = java.util.regex.Pattern
                                                                .compile(
                                                                                "\\d+");

                                                java.util.regex.Matcher m = p.matcher(
                                                                compKey);

                                                if (m.find()) {

                                                        jobUrl = "https://www.linkedin.com/jobs/view/"
                                                                        + m.group();
                                                }
                                        }
                                }

                                // ==========================================
                                // DEBUG
                                // ==========================================

                                if (i == 0) {

                                        System.out.println(
                                                        "DEBUG "
                                                                        + portal
                                                                        + " Extracted - Company: "
                                                                        + company
                                                                        + ", Role: "
                                                                        + role
                                                                        + ", Location: "
                                                                        + location
                                                                        + ", Posted: "
                                                                        + posted
                                                                        + ", Link: "
                                                                        + jobUrl);
                                }

                                // ==========================================
                                // EMPTY CHECK
                                // ==========================================

                                if (company.equals("N/A")
                                                || role.equals("N/A")
                                                || role.isBlank()) {

                                        continue;
                                }

                                // ==========================================
                                // FILTER
                                // ==========================================

                                if (filterService.isRelevant(role)
                                                && filterService.isRecent(
                                                                posted)) {

                                        JobModel job = new JobModel(
                                                        company,
                                                        role,
                                                        portal,
                                                        location,
                                                        posted,
                                                        jobUrl);

                                        JobCollectorService.addJob(
                                                        job);

                                        System.out.println(
                                                        "Job added : "
                                                                        + role);
                                }

                        } catch (Exception e) {

                                System.out.println(
                                                "Failed to scrape one job from : "
                                                                + portal);

                                e.printStackTrace();
                        }
                }
        }

        // ==========================================
        // FAST TEXT
        // ==========================================

        private static String fastText(
                        Locator card,
                        String selector) {

                try {

                        if (selector == null
                                        || selector.trim().isEmpty()) {

                                return "N/A";
                        }

                        Locator loc = card.locator(selector);

                        if (loc.count() > 0) {

                                String value = loc.first()
                                                .textContent();

                                return value != null
                                                ? value.trim()
                                                : "N/A";
                        }

                        return "N/A";

                } catch (Exception e) {

                        return "N/A";
                }
        }

        // ==========================================
        // FAST ATTRIBUTE
        // ==========================================

        private static String fastAttribute(
                        Locator card,
                        String selector,
                        String attribute) {

                try {

                        if (selector == null
                                        || selector.trim().isEmpty()) {

                                return "N/A";
                        }

                        Locator loc = card.locator(selector);

                        if (loc.count() > 0) {

                                String value = loc.first()
                                                .getAttribute(
                                                                attribute);

                                return value != null
                                                ? value.trim()
                                                : "N/A";
                        }

                        return "N/A";

                } catch (Exception e) {

                        return "N/A";
                }
        }
}