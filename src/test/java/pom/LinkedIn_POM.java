package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import config.PortalConfig;
import utilities.JobPortal;

public class LinkedIn_POM {

        private final Page page;

        // ==========================================
        // URL
        // ==========================================

        private final String LINKEDIN_URL = PortalConfig.getUrl(JobPortal.LINKEDIN);

        // ==========================================
        // SEARCH SELECTORS
        // ==========================================

        private final String keywordInput = "input[aria-label='Search job titles or companies'], " +
                        "input[id*='job-search-bar-keywords'], " +
                        "input[placeholder='Search job titles or companies'], " +
                        "input[name='keywords']";

        private final String locationInput = "input[aria-label='Location'], " +
                        "input[id*='job-search-bar-location'], " +
                        "input[placeholder='Location'], " +
                        "input[name='location']";

        private final String searchButton = "button[aria-label='Search'], " +
                        "button.base-search-bar__submit-btn, " +
                        "button[type='submit']";

        private final String jobCards = ".jobs-search-results__list-item, " +
                        ".base-card, " +
                        ".job-search-card, " +
                        ".jobs-search__results-list li";

        // ==========================================
        // CONSTRUCTOR
        // ==========================================

        public LinkedIn_POM(Page page) {

                this.page = page;
        }

        // ==========================================
        // OPEN LINKEDIN
        // ==========================================

        public void goToLinkedIn() {

                try {

                        page.navigate(LINKEDIN_URL);

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        System.out.println(
                                        "LinkedIn opened successfully.");

                        // ==========================================
                        // SAFE BODY CLICK
                        // ==========================================

                        try {

                                page.locator("body")
                                                .click(
                                                                new Locator.ClickOptions()
                                                                                .setPosition(50, 50));

                                System.out.println(
                                                "LinkedIn body clicked.");

                        } catch (Exception e) {

                                System.out.println(
                                                "Body click skipped.");
                        }

                } catch (Exception e) {

                        System.out.println(
                                        "Failed to open LinkedIn.");

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
                        // NORMALIZE SEARCH
                        // ==========================================

                        keyword = keyword
                                        .toLowerCase()
                                        .trim();

                        location = location
                                        .trim();

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
                        // WAIT KEYWORD FIELD
                        // ==========================================

                        keywordBox.waitFor(
                                        new Locator.WaitForOptions()
                                                        .setState(
                                                                        WaitForSelectorState.VISIBLE)
                                                        .setTimeout(15000));

                        // ==========================================
                        // ENTER KEYWORD
                        // ==========================================

                        keywordBox.click();

                        keywordBox.press("Control+A");

                        keywordBox.press("Backspace");

                        keywordBox.fill(keyword);

                        System.out.println(
                                        "Keyword entered : " + keyword);

                        // ==========================================
                        // WAIT LOCATION FIELD
                        // ==========================================

                        locationBox.waitFor(
                                        new Locator.WaitForOptions()
                                                        .setState(
                                                                        WaitForSelectorState.VISIBLE)
                                                        .setTimeout(15000));

                        // ==========================================
                        // ENTER LOCATION
                        // ==========================================

                        locationBox.click();

                        locationBox.press("Control+A");

                        locationBox.press("Backspace");

                        locationBox.fill(location);

                        System.out.println(
                                        "Location entered : " + location);

                        // ==========================================
                        // SEARCH BUTTON
                        // ==========================================

                        try {

                                searchBtn.waitFor(
                                                new Locator.WaitForOptions()
                                                                .setState(
                                                                                WaitForSelectorState.VISIBLE)
                                                                .setTimeout(5000));

                                searchBtn.click();

                                System.out.println(
                                                "LinkedIn search button clicked.");

                        } catch (Exception e) {

                                System.out.println(
                                                "Search button not visible. Using ENTER fallback.");

                                locationBox.press("Enter");
                        }

                        // ==========================================
                        // WAIT RESULTS
                        // ==========================================

                        page.waitForSelector(
                                        ".jobs-search__results-list, .jobs-search-results-list",
                                        new Page.WaitForSelectorOptions()
                                                        .setTimeout(20000));

                        // ==========================================
                        // FAST SCROLL
                        // ==========================================

                        for (int i = 0; i < 3; i++) {

                                page.mouse().wheel(0, 5000);

                                page.waitForTimeout(1000);
                        }

                        // ==========================================
                        // RESULT VALIDATION
                        // ==========================================

                        int totalJobs = getJobCount();

                        System.out.println(
                                        "LinkedIn jobs found : " + totalJobs);

                        System.out.println(
                                        "LinkedIn search completed successfully.");

                } catch (Exception e) {

                        System.out.println(
                                        "LinkedIn search failed.");

                        e.printStackTrace();
                }
        }

        // ==========================================
        // RESULTS
        // ==========================================

        public boolean hasResults() {

                return getJobCount() > 0;
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