package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import config.PortalConfig;

import utilities.JobPortal;
import utilities.ScreenshotUtils;

public class Foundit_POM {

        private final Page page;

        private final String FOUNDIT_URL = PortalConfig.getUrl(
                        JobPortal.FOUNDIT);

        private final String[] keywordSelectors = {
                        "input[placeholder*='Skills']",
                        "input[name='fts']",
                        "input[placeholder*='Search']",
                        "input[type='search']"
        };

        private final String[] locationSelectors = {
                        "input[placeholder*='Location']",
                        "input[name='lmy']",
                        "input[placeholder*='location']"
        };

        private final String[] searchButtonSelectors = {
                        "button.search-btn",
                        "button[type='submit']",
                        "input[type='submit']",
                        ".search-btn"
        };

        private final String[] jobCardSelectors = {
                        ".cardContainer",
                        ".srpResultCard",
                        ".jobTuple",
                        ".jobCard",
                        ".jobcard",
                        ".results-card",
                        ".job-result-card",
                        "[data-testid='job-card']",
                        "section.cardContainer",
                        "div.cardContainer",
                        ".search-result-card"
        };

        public Foundit_POM(Page page) {

                this.page = page;
        }

        public void goToFoundit() {

                try {
                        page.navigate(FOUNDIT_URL,
                                        new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
                        page.waitForTimeout(5000);

                        System.out.println("Foundit opened successfully.");

                } catch (Exception e) {
                        System.out.println("Failed to open Foundit.");

                        ScreenshotUtils.captureScreenshot(page, JobPortal.FOUNDIT, "foundit_open_failure.png");
                        e.printStackTrace();
                }
        }

        private Locator findWorkingLocator(String[] selectors) {
                for (String selector : selectors) {
                        try {
                                Locator locator = page.locator(selector).first();
                                locator.waitFor(new Locator.WaitForOptions().setTimeout(3000));
                                if (locator.count() > 0 && locator.isVisible()) {
                                        return locator;
                                }
                        } catch (Exception ignored) {
                        }
                }
                throw new RuntimeException("No working locator found.");
        }

        private String getWorkingJobCardSelector() {

                for (String selector : jobCardSelectors) {
                        try {
                                Locator cards = page.locator(selector);
                                if (cards.count() > 0) {
                                        return selector;
                                }
                        } catch (Exception ignored) {
                        }
                }
                throw new RuntimeException("No working job card selector found.");
        }

        public void searchJobs(String keyword,
                        String location) {

                try {
                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);
                        page.waitForTimeout(3000);
                        Locator keywordField = findWorkingLocator(
                                        keywordSelectors);

                        keywordField.scrollIntoViewIfNeeded();
                        keywordField.click();
                        page.waitForTimeout(1000);
                        keywordField.fill("");
                        page.waitForTimeout(1000);
                        keywordField.fill(keyword);
                        page.waitForTimeout(1500);
                        System.out.println("Keyword entered : " + keyword);

                        Locator locationField = findWorkingLocator(locationSelectors);

                        locationField.scrollIntoViewIfNeeded();
                        locationField.click();
                        page.waitForTimeout(1000);
                        locationField.press("Control+A");
                        page.waitForTimeout(1000);
                        locationField.press("Backspace");
                        page.waitForTimeout(1000);
                        locationField.fill(location);
                        page.waitForTimeout(1500);
                        ScreenshotUtils.captureScreenshot(page, JobPortal.FOUNDIT, "foundit_location_filled.png");
                        System.out.println("Location entered : " + location);
                        try {
                                Locator searchBtn = findWorkingLocator(searchButtonSelectors);
                                searchBtn.scrollIntoViewIfNeeded();
                                page.waitForTimeout(1000);
                                searchBtn.click();
                                page.waitForTimeout(3000);
                                System.out.println("Search button clicked.");
                        } catch (Exception e) {
                                ScreenshotUtils.captureScreenshot(page, JobPortal.FOUNDIT,
                                                "foundit_search_click_failure.png");
                                System.out.println("Search button failed. Using ENTER fallback.");
                                keywordField.press("Enter");
                        }
                        page.waitForTimeout(8000);
                        String activeJobCardSelector = getWorkingJobCardSelector();
                        int totalJobs = page.locator(activeJobCardSelector).count();
                        System.out.println("Foundit jobs found : " + totalJobs);
                        System.out.println("Foundit job search completed.");
                } catch (Exception e) {
                        System.out.println("Foundit job search failed.");
                        ScreenshotUtils.captureScreenshot(page, JobPortal.FOUNDIT, "foundit_search_failure.png");
                        e.printStackTrace();
                }
        }

        public boolean hasResults() {
                try {
                        String selector = getWorkingJobCardSelector();
                        return page.locator(selector).count() > 0;
                } catch (Exception e) {
                        return false;
                }
        }

        public int getJobCount() {
                try {
                        String selector = getWorkingJobCardSelector();
                        return page.locator(selector).count();
                } catch (Exception e) {
                        return 0;
                }
        }

        public Locator getJobCards() {
                String selector = getWorkingJobCardSelector();
                return page.locator(selector);
        }
}