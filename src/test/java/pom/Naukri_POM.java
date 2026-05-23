package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import config.PortalConfig;
import utilities.JobPortal;

public class Naukri_POM {

        private final Page page;

        // ==========================================
        // URL
        // ==========================================

        private final String NAUKRI_URL = PortalConfig.getUrl(JobPortal.NAUKRI);

        // ==========================================
        // SEARCH SELECTORS
        // ==========================================

        private final String keywordInput = "input.suggestor-input";
        private final String locationInput = "input[placeholder*='location']";
        private final String searchButton = ".qsbSubmit";
        private final String jobCards = ".srp-jobtuple-wrapper";

        // ==========================================
        // CONSTRUCTOR
        // ==========================================

        public Naukri_POM(Page page) {

                this.page = page;
        }

        // ==========================================
        // OPEN NAUKRI
        // ==========================================

        public void goToNaukri() {

                try {

                        page.navigate(NAUKRI_URL);

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        System.out.println(
                                        "Naukri opened successfully.");

                } catch (Exception e) {

                        System.out.println(
                                        "Failed to open Naukri.");

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
                        // WAIT PAGE LOAD
                        // ==========================================

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        // ==========================================
                        // LOCATORS
                        // ==========================================

                        Locator keywordBox = page.locator(
                                        keywordInput).first();

                        Locator locationBox = page.locator(
                                        locationInput).first();

                        Locator searchBtn = page.locator(
                                        searchButton).first();

                        // ==========================================
                        // WAIT & ENTER KEYWORD
                        // ==========================================

                        keywordBox.waitFor(
                                        new Locator.WaitForOptions()
                                                        .setState(
                                                                        WaitForSelectorState.VISIBLE)
                                                        .setTimeout(10000));

                        keywordBox.fill(keyword);

                        System.out.println(
                                        "Keyword entered : " + keyword);

                        // ==========================================
                        // WAIT & ENTER LOCATION
                        // ==========================================

                        locationBox.waitFor(
                                        new Locator.WaitForOptions()
                                                        .setState(
                                                                        WaitForSelectorState.VISIBLE)
                                                        .setTimeout(10000));

                        locationBox.fill(location);

                        System.out.println(
                                        "Location entered : " + location);

                        // ==========================================
                        // CLICK SEARCH
                        // ==========================================

                        searchBtn.waitFor(
                                        new Locator.WaitForOptions()
                                                        .setState(
                                                                        WaitForSelectorState.VISIBLE)
                                                        .setTimeout(10000));

                        searchBtn.click();

                        System.out.println(
                                        "Search button clicked.");

                        // ==========================================
                        // WAIT RESULT PAGE
                        // ==========================================

                        page.waitForSelector(
                                        jobCards,
                                        new Page.WaitForSelectorOptions()
                                                        .setTimeout(15000));

                        // ==========================================
                        // FAST SCROLL
                        // ==========================================

                        page.mouse().wheel(0, 5000);

                        page.waitForTimeout(1000);

                        page.mouse().wheel(0, 5000);

                        page.waitForTimeout(1000);

                        // ==========================================
                        // RESULT VALIDATION
                        // ==========================================

                        int totalJobs = getJobCount();

                        System.out.println(
                                        "Naukri jobs found : " + totalJobs);

                        System.out.println(
                                        "Naukri search completed successfully.");

                } catch (Exception e) {

                        System.out.println(
                                        "Naukri search failed.");

                        e.printStackTrace();
                }
        }

        // ==========================================
        // RESULTS
        // ==========================================

        public boolean hasResults() {

                return page.locator(
                                jobCards)
                                .count() > 0;
        }

        public int getJobCount() {

                return page.locator(
                                jobCards)
                                .count();
        }

        public Locator getJobCards() {

                return page.locator(
                                jobCards);
        }
}