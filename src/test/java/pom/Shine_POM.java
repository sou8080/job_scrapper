package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import config.PortalConfig;
import utilities.JobPortal;

public class Shine_POM {

        private final Page page;

        // ==========================================
        // URL
        // ==========================================

        private final String SHINE_URL = PortalConfig.getUrl(JobPortal.SHINE);

        // ==========================================
        // SELECTORS
        // ==========================================

        private final String keywordInput = "input#id_q, input[placeholder='Enter Skills/Roles']";
        private final String locationInput = "input#id_loc, input[placeholder='Enter Location']";
        private final String searchButton = "button#_r_3_, button[type='submit'], button.search-btn";
        private final String jobCards = "li[itemprop='itemListElement'], div.jobCard, .jobCard_wrapper, .jobCard";

        // ==========================================
        // CONSTRUCTOR
        // ==========================================

        public Shine_POM(Page page) {

                this.page = page;
        }

        // ==========================================
        // OPEN SHINE
        // ==========================================

        public void goToShine() {

                try {

                        page.navigate(SHINE_URL);

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        System.out.println(
                                        "Shine opened successfully.");

                } catch (Exception e) {

                        System.out.println(
                                        "Failed to open Shine.");

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
                                        "Shine search button clicked.");

                        // ==========================================
                        // WAIT RESULTS
                        // ==========================================

                        page.waitForSelector(
                                        jobCards);

                        // ==========================================
                        // FAST SCROLL
                        // ==========================================

                        page.mouse().wheel(0, 5000);
                        try {
                                page.locator("body").click(new Locator.ClickOptions().setPosition(50, 50));
                                System.out.println("Shine body clicked.");

                        } catch (Exception e) {
                                System.out.println("Shine body click skipped.");
                        }

                        page.waitForTimeout(1000);

                        // ==========================================
                        // RESULT VALIDATION
                        // ==========================================

                        int totalJobs = getJobCount();

                        System.out.println(
                                        "Shine jobs found : " + totalJobs);

                        System.out.println(
                                        "Shine job search completed.");

                } catch (Exception e) {

                        System.out.println(
                                        "Shine job search failed.");

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