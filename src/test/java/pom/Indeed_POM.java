package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import config.PortalConfig;
import utilities.JobPortal;

public class Indeed_POM {

        private final Page page;

        // ==========================================
        // URL
        // ==========================================

        private final String INDEED_URL = PortalConfig.getUrl(JobPortal.INDEED);

        // ==========================================
        // SELECTORS
        // ==========================================

        private final String searchKeywords = "input[name='q']";
        private final String searchLocation = "input[name='l']";
        private final String searchButton = "button[type='submit']";
        private final String jobCards = ".job_seen_beacon";

        // ==========================================
        // CONSTRUCTOR
        // ==========================================

        public Indeed_POM(Page page) {

                this.page = page;
        }

        // ==========================================
        // OPEN INDEED
        // ==========================================

        public void goToIndeed() {

                try {

                        page.navigate(INDEED_URL);

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        System.out.println(
                                        "Indeed opened successfully.");

                } catch (Exception e) {

                        System.out.println(
                                        "Failed to open Indeed.");

                        e.printStackTrace();
                }
        }

        // ==========================================
        // SEARCH JOBS
        // ==========================================

        public void searchJobs(
                        String keyword,
                        String location) {

                try {

                        // ==========================================
                        // KEYWORD FIELD
                        // ==========================================

                        Locator keywordField = page.locator(
                                        searchKeywords).first();

                        keywordField.fill(keyword);

                        System.out.println(
                                        "Keyword entered : " + keyword);

                        // ==========================================
                        // LOCATION FIELD
                        // ==========================================

                        Locator locationField = page.locator(
                                        searchLocation).first();

                        locationField.fill(location);

                        System.out.println(
                                        "Location entered : " + location);

                        // ==========================================
                        // SEARCH BUTTON
                        // ==========================================

                        Locator searchBtn = page.locator(
                                        searchButton).first();

                        searchBtn.click();

                        System.out.println(
                                        "Indeed search button clicked.");

                        // ==========================================
                        // WAIT RESULTS
                        // ==========================================

                        page.waitForSelector(
                                        jobCards);

                        // ==========================================
                        // FAST SCROLL
                        // ==========================================

                        page.mouse().wheel(0, 5000);

                        page.waitForTimeout(1000);

                        // ==========================================
                        // RESULT VALIDATION
                        // ==========================================

                        int totalJobs = getJobCount();

                        System.out.println(
                                        "Indeed jobs found : " + totalJobs);

                        System.out.println(
                                        "Indeed job search completed.");

                } catch (Exception e) {

                        System.out.println(
                                        "Indeed job search failed.");

                        e.printStackTrace();
                }
        }

        // ==========================================
        // HAS RESULTS
        // ==========================================

        public boolean hasResults() {

                return page.locator(
                                jobCards)
                                .count() > 0;
        }

        // ==========================================
        // GET JOB COUNT
        // ==========================================

        public int getJobCount() {

                return page.locator(
                                jobCards)
                                .count();
        }

        // ==========================================
        // GET JOB CARDS
        // ==========================================

        public Locator getJobCards() {

                return page.locator(
                                jobCards);
        }
}