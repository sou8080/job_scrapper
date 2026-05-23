package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import config.PortalConfig;
import utilities.JobPortal;

public class Foundit_POM {

        private final Page page;

        // ==========================================
        // URL
        // ==========================================

        private final String FOUNDIT_URL = PortalConfig.getUrl(JobPortal.FOUNDIT);

        // ==========================================
        // SELECTORS
        // ==========================================

        private final String keywordInput = "input[placeholder*='Skills'], input[name='fts']";
        private final String locationInput = "input[placeholder*='Location'], input[name='lmy']";
        private final String searchButton = "input[type='submit'], button.search-btn, button[type='submit'], .search-btn";
        private final String jobCards = ".cardContainer";

        // ==========================================
        // CONSTRUCTOR
        // ==========================================

        public Foundit_POM(Page page) {

                this.page = page;
        }

        // ==========================================
        // OPEN FOUNDIT
        // ==========================================

        public void goToFoundit() {

                try {

                        page.navigate(FOUNDIT_URL);

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        System.out.println(
                                        "Foundit opened successfully.");

                } catch (Exception e) {

                        System.out.println(
                                        "Failed to open Foundit.");

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
                                        keywordInput).first();

                        keywordField.fill(keyword);

                        System.out.println(
                                        "Keyword entered : " + keyword);

                        // ==========================================
                        // LOCATION FIELD
                        // ==========================================

                        Locator locationField = page.locator(
                                        locationInput).first();

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
                                        "Foundit search button clicked.");

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
                                        "Foundit jobs found : " + totalJobs);

                        System.out.println(
                                        "Foundit job search completed.");

                } catch (Exception e) {

                        System.out.println(
                                        "Foundit job search failed.");

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