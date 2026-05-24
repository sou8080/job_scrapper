package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import config.PortalConfig;
import utilities.JobPortal;
import utilities.ScreenshotUtils;

public class Indeed_POM {

        private final Page page;

        private final String INDEED_URL = PortalConfig.getUrl(JobPortal.INDEED);

        private final String[] keywordSelectors = {
                        "input[name='q']",
                        "input[placeholder='Job title, keywords, or company']",
                        "input[id='text-input-what']",
                        "input[aria-label*='Job title']",
                        "input[aria-label*='keywords']"
        };

        private final String[] locationSelectors = {
                        "#text-input-where",
                        "input[name='l']",
                        "input[placeholder*='City']",
                        "input[aria-label*='location']",
                        "input[aria-label*='Edit location']"
        };

        private final String[] searchButtonSelectors = {
                        "button.yosegi-InlineWhatWhere-primaryButton",
                        "button[type='submit']",
                        "button:has-text('Find jobs')"
        };

        private final String[] jobCardSelectors = {
                        ".job_seen_beacon",
                        ".tapItem",
                        ".cardOutline",
                        ".slider_item",
                        "[data-testid='slider_item']",
                        ".job_seen_beacon.tapItem",
                        ".jobsearch-SerpJobCard",
                        ".jobsearch-ResultsList > li",
                        "li.css-5lfssm",
                        "ul li div[data-testid='slider_item']",
                        "main ul li",
                        "a[data-jk]"
        };

        public Indeed_POM(Page page) {
                this.page = page;
        }

        public void goToIndeed() {

                try {

                        page.navigate(
                                        INDEED_URL,
                                        new Page.NavigateOptions()
                                                        .setWaitUntil(
                                                                        WaitUntilState.DOMCONTENTLOADED));

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        waitMedium();

                        String currentUrl = page.url().toLowerCase();

                        System.out.println(
                                        "Current URL : "
                                                        + currentUrl);

                        boolean invalidPage = currentUrl.contains("blocked")
                                        || currentUrl.contains("captcha")
                                        || currentUrl.contains("challenge")
                                        || currentUrl.contains("login");

                        if (invalidPage) {

                                System.out.println(
                                                "[INDEED] Blocked or challenge page detected.");

                                ScreenshotUtils.captureScreenshot(
                                                page,
                                                JobPortal.INDEED,
                                                "indeed_blocked.png");

                                throw new RuntimeException(
                                                "Indeed blocked.");
                        }

                        try {

                                page.locator("body")
                                                .click(
                                                                new Locator.ClickOptions()
                                                                                .setPosition(50, 50));

                        } catch (Exception ignored) {
                        }

                        System.out.println(
                                        "Indeed opened successfully.");

                } catch (Exception e) {

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.INDEED,
                                        "indeed_open_failure.png");

                        System.out.println(
                                        "[INDEED] Unable to open portal.");

                        throw new RuntimeException(
                                        "Failed to open Indeed.");
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

                try {

                        field.scrollIntoViewIfNeeded();
                        waitShort();

                        field.click();
                        waitShort();

                        field.press("Control+A");
                        field.press("Backspace");
                        waitShort();

                        field.pressSequentially(
                                        value,
                                        new Locator.PressSequentiallyOptions()
                                                        .setDelay(80));

                        waitShort();

                } catch (Exception e) {

                        System.out.println(
                                        "[INDEED] Text entry failed.");
                }
        }

        private void closeCookiePopup() {

                try {

                        String[] cookieSelectors = {
                                        "button:has-text('Accept')",
                                        "button:has-text('Accept All')",
                                        "button:has-text('I Accept')",
                                        "#onetrust-accept-btn-handler"
                        };

                        for (String selector : cookieSelectors) {

                                try {

                                        Locator cookieBtn = page.locator(selector)
                                                        .first();

                                        if (cookieBtn.count() > 0
                                                        && cookieBtn.isVisible()) {

                                                cookieBtn.click();

                                                waitShort();

                                                return;
                                        }

                                } catch (Exception ignored) {
                                }
                        }

                } catch (Exception ignored) {
                }
        }

        public void searchJobs(
                        String keyword,
                        String location) {

                try {

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        waitMedium();

                        closeCookiePopup();

                        Locator keywordField = findWorkingLocator(
                                        keywordSelectors);

                        fillField(keywordField, keyword);

                        System.out.println(
                                        "Keyword entered : "
                                                        + keyword);

                        Locator locationField = findWorkingLocator(
                                        locationSelectors);

                        fillField(locationField, location);

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.INDEED,
                                        "indeed_location_filled.png");

                        System.out.println(
                                        "Location entered : "
                                                        + location);

                        try {

                                Locator searchBtn = findWorkingLocator(
                                                searchButtonSelectors);

                                searchBtn.scrollIntoViewIfNeeded();

                                waitShort();

                                searchBtn.click();

                        } catch (Exception e) {

                                System.out.println(
                                                "[INDEED] Search button failed.");

                                keywordField.press("Enter");
                        }

                        page.waitForTimeout(8000);

                        String activeJobCardSelector = getWorkingJobCardSelector();

                        int totalJobs = page.locator(
                                        activeJobCardSelector)
                                        .count();

                        System.out.println(
                                        "Indeed jobs found : "
                                                        + totalJobs);

                        System.out.println(
                                        "Indeed job search completed.");

                } catch (Exception e) {

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.INDEED,
                                        "indeed_search_failure.png");

                        System.out.println(
                                        "[INDEED] Search failed.");
                }
        }

        public boolean hasResults() {

                try {

                        return page.locator(
                                        getWorkingJobCardSelector())
                                        .count() > 0;

                } catch (Exception e) {

                        return false;
                }
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