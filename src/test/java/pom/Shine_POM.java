package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import config.PortalConfig;

import utilities.JobPortal;
import utilities.ScreenshotUtils;

public class Shine_POM {

        private final Page page;

        private final String SHINE_URL = PortalConfig.getUrl(
                        JobPortal.SHINE);

        private final String[] keywordSelectors = {

                        "input#id_q",

                        "input[placeholder='Enter Skills/Roles']",

                        "input[placeholder*='Skills']",

                        "input[type='text']"
        };

        private final String[] locationSelectors = {

                        "input#id_loc",

                        "input[placeholder='Enter Location']",

                        "input[placeholder*='Location']"
        };

        private final String[] searchButtonSelectors = {

                        "button#_r_3_",

                        "button[type='submit']",

                        "button.search-btn"
        };

        private final String[] jobCardSelectors = {

                        "li[itemprop='itemListElement']",

                        "div.jobCard",

                        ".jobCard_wrapper",

                        ".jobCard",

                        ".jobCard_wrapper__vJGZq",

                        ".jobCardNova_bigCard__W2xn3"
        };

        public Shine_POM(Page page) {

                this.page = page;
        }

        public void goToShine() {

                try {

                        page.navigate(
                                        SHINE_URL);

                        page.waitForLoadState(
                                        LoadState.DOMCONTENTLOADED);

                        waitMedium();

                        System.out.println(
                                        "Shine opened successfully.");

                } catch (Exception e) {

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.SHINE,
                                        "shine_open_failure.png");

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
                                        JobPortal.SHINE,
                                        "shine_location_filled.png");

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

                                System.out.println(
                                                "Shine search button clicked.");

                        } catch (Exception e) {

                                ScreenshotUtils.captureScreenshot(
                                                page,
                                                JobPortal.SHINE,
                                                "shine_search_click_failure.png");

                                System.out.println(
                                                "Search button failed. Using ENTER fallback.");

                                keywordField.press(
                                                "Enter");
                        }

                        page.waitForTimeout(8000);

                        try {

                                page.locator("body")
                                                .click(
                                                                new Locator.ClickOptions()
                                                                                .setPosition(50, 50));

                        } catch (Exception ignored) {
                        }

                        int totalJobs = getJobCount();

                        System.out.println(
                                        "Shine jobs found : "
                                                        + totalJobs);

                        System.out.println(
                                        "Shine search completed successfully.");

                } catch (Exception e) {

                        ScreenshotUtils.captureScreenshot(
                                        page,
                                        JobPortal.SHINE,
                                        "shine_search_failure.png");

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