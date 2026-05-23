package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import config.PortalConfig;

import utilities.JobPortal;
import utilities.ScreenshotUtils;

public class Naukri_POM {

        private final Page page;

        private final String NAUKRI_URL = PortalConfig.getUrl(
                        JobPortal.NAUKRI);

        private final String[] keywordSelectors = {
                        "input.suggestor-input",
                        "input[placeholder*='Enter skills']",
                        "input[placeholder*='skills']",
                        "input[placeholder*='designation']",
                        "input[type='text']"
        };

        private final String[] locationSelectors = {
                        "input[placeholder*='location']",
                        "input[placeholder*='Location']",
                        "input[placeholder*='city']",
                        "input.suggestor-location"
        };

        private final String[] searchButtonSelectors = {
                        ".qsbSubmit",
                        "button.qsbSubmit",
                        "button[type='submit']",
                        ".search-btn"
        };

        private final String[] jobCardSelectors = {
                        ".srp-jobtuple-wrapper",
                        ".cust-job-tuple",
                        ".jobTuple",
                        ".jobCard",
                        ".styles_job-listing-container",
                        "article.jobTuple",
                        ".tuple-wrap",
                        ".jobTupleHeader",
                        ".jobTuple.bgWhite",
                        "div[data-job-id]"
        };

        public Naukri_POM(Page page) {

                this.page = page;
        }

        public void goToNaukri() {

                try {

                        page.navigate(
                                        NAUKRI_URL,
                                        new Page.NavigateOptions()
                                                        .setWaitUntil(
                                                                        WaitUntilState.DOMCONTENTLOADED));

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        waitMedium();

                        System.out.println(
                                        "Naukri opened successfully.");

                } catch (Exception e) {

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.NAUKRI,
                                        "naukri_open_failure.png");

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
                                                                .setTimeout(3000));

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
                                        keyword);

                        System.out.println(
                                        "Keyword entered : "
                                                        + keyword);

                        Locator locationField = findWorkingLocator(
                                        locationSelectors);

                        fillField(
                                        locationField,
                                        location);

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.NAUKRI,
                                        "naukri_location_filled.png");

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
                                                JobPortal.NAUKRI,
                                                "naukri_search_click_failure.png");

                                keywordField.press(
                                                "Enter");
                        }

                        page.waitForTimeout(8000);

                        int totalJobs = getJobCount();

                        System.out.println(
                                        "Naukri jobs found : "
                                                        + totalJobs);

                        System.out.println(
                                        "Naukri search completed successfully.");

                } catch (Exception e) {

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.NAUKRI,
                                        "naukri_search_failure.png");

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