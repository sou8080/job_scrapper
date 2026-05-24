package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import config.PortalConfig;
import utilities.JobPortal;
import utilities.ScreenshotUtils;
import utilities.WaitUtils;

public class Shine_POM {

        private final Page page;

        private final String SHINE_URL = PortalConfig.getUrl(JobPortal.SHINE);

        private static final int MAX_RETRIES = 3;

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

        // ==========================================
        // GOTO SHINE (WITH RETRY & VERIFICATION)
        // ==========================================

        public void goToShine() {
                int attempts = 0;
                while (attempts < MAX_RETRIES) {
                        try {
                                attempts++;
                                System.out.println("[SHINE] Navigating to Shine portal (Attempt: " + attempts + ")...");

                                page.navigate(SHINE_URL);
                                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
                                waitMedium();

                                String currentUrl = page.url().toLowerCase();
                                boolean isBlocked = currentUrl.contains("captcha")
                                                || currentUrl.contains("blocked")
                                                || page.title().toLowerCase().contains("just a moment");

                                if (isBlocked) {
                                        System.out.println(
                                                        "[SHINE] ⚠️ Shine anti-bot verification detected. Clearing cookies and retrying...");
                                        ScreenshotUtils.captureScreenshot(page, JobPortal.SHINE,
                                                        "shine_blocked_retry_" + attempts + ".png");

                                        try {
                                                page.context().clearCookies();
                                        } catch (Exception ignored) {
                                        }
                                        WaitUtils.humanDelay(page, 3000, 4500);
                                        continue;
                                }

                                System.out.println("[SHINE] Shine portal loaded successfully.");
                                return;

                        } catch (Exception e) {
                                ScreenshotUtils.captureScreenshot(page, JobPortal.SHINE,
                                                "shine_open_failure_" + attempts + ".png");
                                e.printStackTrace();
                                WaitUtils.humanDelay(page, 3000, 4000);
                        }
                }
                throw new RuntimeException("Failed to open Shine after " + MAX_RETRIES + " attempts.");
        }

        private void waitShort() {
                WaitUtils.humanDelay(page, 800, 1500);
        }

        private void waitMedium() {
                WaitUtils.humanDelay(page, 2000, 3500);
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
                throw new RuntimeException("No working selector resolved.");
        }

        private String getWorkingJobCardSelector() {
                for (String selector : jobCardSelectors) {
                        try {
                                if (page.locator(selector).count() > 0) {
                                        return selector;
                                }
                        } catch (Exception ignored) {
                        }
                }
                throw new RuntimeException("No working job card selector resolved.");
        }

        private void fillField(Locator field, String value) {
                try {
                        field.scrollIntoViewIfNeeded();
                        field.click();
                        waitShort();
                        field.press("Control+A");
                        waitShort();
                        field.press("Backspace");
                        waitShort();

                        // FAST DIRECT ENTRY
                        field.fill(value);

                        waitMedium();

                } catch (Exception e) {

                        System.err.println("[SHINE] Direct fill failed.");

                        try {
                                field.fill(value);
                                waitMedium();
                        } catch (Exception ignored) {
                        }
                }
        }

        public void searchJobs(String keyword, String location) {
                try {
                        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
                        waitMedium();

                        Locator keywordField = findWorkingLocator(keywordSelectors);
                        fillField(keywordField, keyword);
                        System.out.println("[SHINE] Keyword entered : " + keyword);

                        Locator locationField = findWorkingLocator(locationSelectors);
                        fillField(locationField, location);
                        ScreenshotUtils.captureScreenshot(page, JobPortal.SHINE, "shine_location_filled.png");
                        System.out.println("[SHINE] Location entered : " + location);

                        try {
                                Locator searchBtn = findWorkingLocator(searchButtonSelectors);
                                searchBtn.scrollIntoViewIfNeeded();
                                waitShort();
                                searchBtn.click();
                                waitMedium();
                                System.out.println("[SHINE] Search button clicked.");
                        } catch (Exception e) {
                                ScreenshotUtils.captureScreenshot(page, JobPortal.SHINE,
                                                "shine_search_click_failure.png");
                                System.out.println("[SHINE] Search button execution failed. Using ENTER key fallback.");
                                keywordField.press("Enter");
                        }

                        // Let listings render and stabilize
                        page.waitForTimeout(8000);

                        try {
                                page.locator("body").click(new Locator.ClickOptions().setPosition(50, 50));
                        } catch (Exception ignored) {
                        }

                        // Scroll viewport to trigger lazy loading of lists
                        WaitUtils.scrollToBottom(page);

                        int totalJobs = getJobCount();
                        System.out.println("[SHINE] Shine search completed. Scraped job count: " + totalJobs);

                } catch (Exception e) {
                        ScreenshotUtils.captureScreenshot(page, JobPortal.SHINE, "shine_search_failure.png");
                        e.printStackTrace();
                }
        }

        public boolean hasResults() {
                return getJobCount() > 0;
        }

        public int getJobCount() {
                try {
                        return page.locator(getWorkingJobCardSelector()).count();
                } catch (Exception e) {
                        return 0;
                }
        }

        public Locator getJobCards() {
                return page.locator(getWorkingJobCardSelector());
        }
}