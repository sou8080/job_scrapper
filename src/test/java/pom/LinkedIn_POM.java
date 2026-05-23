package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import config.PortalConfig;

import utilities.JobPortal;
import utilities.ScreenshotUtils;

public class LinkedIn_POM {

        private final Page page;

        private final String LINKEDIN_URL = PortalConfig.getUrl(
                        JobPortal.LINKEDIN);

        private final String[] keywordSelectors = {
                        "input[aria-label='Search job titles or companies']",
                        "input[id*='job-search-bar-keywords']",
                        "input[placeholder='Search job titles or companies']",
                        "input[name='keywords']"
        };

        private final String[] locationSelectors = {
                        "input[aria-label='Location']",
                        "input[id*='job-search-bar-location']",
                        "input[placeholder='Location']",
                        "input[name='location']"
        };

        private final String[] searchButtonSelectors = {
                        "button[aria-label='Search']",
                        "button.base-search-bar__submit-btn",
                        "button[type='submit']"
        };

        private final String[] jobCardSelectors = {
                        ".jobs-search-results__list-item",
                        ".base-card",
                        ".job-search-card",
                        ".jobs-search__results-list li"
        };

        public LinkedIn_POM(Page page) {

                this.page = page;
        }

        public void goToLinkedIn() {

                try {

                        page.navigate(
                                        LINKEDIN_URL);

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        waitMedium();

                        try {

                                page.locator("body")
                                                .click(
                                                                new Locator.ClickOptions()
                                                                                .setPosition(50, 50));

                        } catch (Exception ignored) {
                        }

                        System.out.println(
                                        "LinkedIn opened successfully.");

                } catch (Exception e) {

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.LINKEDIN,
                                        "linkedin_open_failure.png");

                        e.printStackTrace();
                }
        }

        private void waitShort() {

                page.waitForTimeout(1000);
        }

        private void waitMedium() {

                page.waitForTimeout(3000);
        }

        private Locator findWorkingLocator(
                        String[] selectors) {

                for (String selector : selectors) {

                        try {

                                Locator locator = page.locator(selector)
                                                .first();

                                locator.waitFor(
                                                new Locator.WaitForOptions()
                                                                .setState(
                                                                                WaitForSelectorState.VISIBLE)
                                                                .setTimeout(5000));

                                if (locator.count() > 0
                                                && locator.isVisible()) {

                                        return locator;
                                }

                        } catch (Exception ignored) {
                        }
                }

                throw new RuntimeException(
                                "No working locator found.");
        }

        private String getWorkingJobCardSelector() {

                for (String selector : jobCardSelectors) {

                        try {

                                if (page.locator(selector)
                                                .count() > 0) {

                                        return selector;
                                }

                        } catch (Exception ignored) {
                        }
                }

                throw new RuntimeException(
                                "No working job card selector found.");
        }

        private void fillField(
                        Locator field,
                        String value) {

                field.scrollIntoViewIfNeeded();

                field.click();

                waitShort();

                field.press("Control+A");

                waitShort();

                field.press("Backspace");

                waitShort();

                field.fill(value);

                waitMedium();
        }

        public void searchJobs(
                        String keyword,
                        String location) {

                try {

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        waitMedium();

                        Locator keywordField = findWorkingLocator(
                                        keywordSelectors);

                        fillField(
                                        keywordField,
                                        keyword.toLowerCase().trim());

                        System.out.println(
                                        "Keyword entered : "
                                                        + keyword);

                        Locator locationField = findWorkingLocator(
                                        locationSelectors);

                        fillField(
                                        locationField,
                                        location.trim());

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.LINKEDIN,
                                        "linkedin_location_filled.png");

                        System.out.println(
                                        "Location entered : "
                                                        + location);

                        try {

                                Locator searchBtn = findWorkingLocator(
                                                searchButtonSelectors);

                                searchBtn.scrollIntoViewIfNeeded();

                                waitShort();

                                searchBtn.click();

                                waitMedium();

                        } catch (Exception e) {

                                ScreenshotUtils.captureScreenshot(
                                                page,
                                                JobPortal.LINKEDIN,
                                                "linkedin_search_click_failure.png");

                                locationField.press(
                                                "Enter");
                        }

                        page.waitForSelector(
                                        ".jobs-search__results-list, .jobs-search-results-list",
                                        new Page.WaitForSelectorOptions()
                                                        .setTimeout(20000));

                        waitMedium();

                        int totalJobs = getJobCount();

                        System.out.println(
                                        "LinkedIn jobs found : "
                                                        + totalJobs);

                        System.out.println(
                                        "LinkedIn search completed successfully.");

                } catch (Exception e) {

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.LINKEDIN,
                                        "linkedin_search_failure.png");

                        e.printStackTrace();
                }
        }

        public boolean hasResults() {

                return getJobCount() > 0;
        }

        public int getJobCount() {

                try {

                        return page.locator(
                                        getWorkingJobCardSelector())
                                        .count();

                } catch (Exception e) {

                        return 0;
                }
        }

        public Locator getJobCards() {

                return page.locator(
                                getWorkingJobCardSelector());
        }
}