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

        private final String INDEED_URL = PortalConfig.getUrl(
                        JobPortal.INDEED);

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
                        System.out.println(
                                        "Indeed opened successfully.");
                } catch (Exception e) {
                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.INDEED,
                                        "indeed_open_failure.png");
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
                field.fill("");
                waitShort();
                field.fill(value);
                waitMedium();
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

        private void handleCloudflareChallenge() {
                int attempts = 3;
                while (attempts > 0) {
                        if (!page.title()
                                        .contains("Just a moment")) {
                                return;
                        }
                        page.waitForTimeout(10000);
                        page.reload();
                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);
                        attempts--;
                }
                ScreenshotUtils.captureScreenshot(
                                page,
                                JobPortal.INDEED,
                                "cloudflare_block.png");
                throw new RuntimeException(
                                "Cloudflare bot protection triggered.");
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
                                waitMedium();
                        } catch (Exception e) {
                                ScreenshotUtils.captureScreenshot(
                                                page,
                                                JobPortal.INDEED,
                                                "indeed_search_click_failure.png");
                                keywordField.press(
                                                "Enter");
                        }
                        page.waitForTimeout(8000);
                        handleCloudflareChallenge();
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

                        e.printStackTrace();
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