package pom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import config.PortalConfig;
import utilities.JobPortal;
import utilities.ScreenshotUtils;
import utilities.WaitUtils;

public class Timesjobs_POM {

    private final Page page;

    private final String TIMESJOBS_URL = PortalConfig.getUrl(JobPortal.TIMESJOBS);

    private static final int MAX_RETRIES = 3;

    private final String[] keywordSelectors = {
            "input[placeholder*='Enter Skills, Designation, etc']",
            "input[id='txtKeywords']",
            "input[name='txtKeywords']",
            "input[placeholder*='skills']",
            "input[type='text']"
    };

    private final String[] locationSelectors = {
            "input[placeholder*='Enter locations']",
            "input[id='txtLocation']",
            "input[name='txtLocation']",
            "input[placeholder*='city']"
    };

    private final String[] searchButtonSelectors = {
            "button:has-text(\"Let's Find\")",
            "button[type='submit']",
            "input[type='submit']",
            ".search-btn"
    };

    private final String[] jobCardSelectors = {
            ".srp-card",
            "div[data-jobid]",
            "div[class*='srp-card']",
            "div.rounded-lg.border",
            "a.absolute.left-0.right-0.top-0.bottom-0.z-20",
            "div.relative.flex.flex-col"
    };

    public Timesjobs_POM(Page page) {
        this.page = page;
    }

    // ==========================================
    // GOTO TIMESJOBS WITH RETRIES
    // ==========================================

    public void goToTimesjobs() {
        int attempts = 0;

        while (attempts < MAX_RETRIES) {
            try {
                attempts++;
                System.out.println("[TIMESJOBS] Navigating to TimesJobs portal (Attempt: " + attempts + ")...");

                page.navigate(TIMESJOBS_URL);
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
                waitMedium();

                String currentUrl = page.url().toLowerCase();
                boolean isBlocked = currentUrl.contains("captcha")
                        || currentUrl.contains("blocked")
                        || page.title().toLowerCase().contains("just a moment");

                if (isBlocked) {
                    System.out.println("[TIMESJOBS] ⚠️ TimesJobs anti-bot / challenge page detected. Retrying...");
                    ScreenshotUtils.captureScreenshot(page, JobPortal.TIMESJOBS,
                            "timesjobs_blocked_retry_" + attempts + ".png");

                    try {
                        page.context().clearCookies();
                    } catch (Exception ignored) {
                    }
                    waitMedium();
                    continue;
                }

                try {
                    page.locator("body").click(new Locator.ClickOptions().setPosition(50, 50));
                } catch (Exception ignored) {
                }

                System.out.println("[TIMESJOBS] TimesJobs opened successfully.");
                return;

            } catch (Exception e) {
                ScreenshotUtils.captureScreenshot(page, JobPortal.TIMESJOBS,
                        "timesjobs_open_failure_" + attempts + ".png");
                e.printStackTrace();
                waitMedium();
            }
        }

        throw new RuntimeException("Failed to open TimesJobs after " + MAX_RETRIES + " retries.");
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
        throw new RuntimeException("No working selector resolved on the page viewport.");
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
            waitShort();
            field.click();
            waitShort();
            field.press("Control+A");
            field.press("Backspace");
            waitShort();

            field.fill(value);

            waitShort();

        } catch (Exception e) {

            System.err.println("[TIMESJOBS] Direct fill failed.");

            try {
                field.fill(value);
            } catch (Exception ignored) {
            }
        }
    }

    // ==========================================
    // DOM RENDERING STABILIZATION
    // ==========================================

    public void waitForDOMStabilization() {
        System.out.println("[TIMESJOBS] Waiting for dynamic DOM to stabilize...");
        int previousCount = -1;

        for (int i = 0; i < 6; i++) {
            try {
                String selector = getWorkingJobCardSelector();
                int currentCount = page.locator(selector).count();
                System.out.println("[TIMESJOBS] Poll " + (i + 1) + " - job cards count: " + currentCount);

                if (currentCount > 0 && currentCount == previousCount) {
                    System.out.println("[TIMESJOBS] DOM stabilized successfully with card count: " + currentCount);
                    return;
                }
                previousCount = currentCount;
            } catch (Exception ignored) {
            }
            page.waitForTimeout(1500);
        }
    }

    public void searchJobs(String keyword, String location) {
        try {
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            waitMedium();

            Locator keywordField = findWorkingLocator(keywordSelectors);
            fillField(keywordField, keyword);
            System.out.println("[TIMESJOBS] Keyword entered : " + keyword);

            Locator locationField = findWorkingLocator(locationSelectors);
            fillField(locationField, location);
            ScreenshotUtils.captureScreenshot(page, JobPortal.TIMESJOBS, "timesjobs_location_filled.png");
            System.out.println("[TIMESJOBS] Location entered : " + location);

            try {
                Locator searchBtn = findWorkingLocator(searchButtonSelectors);
                searchBtn.scrollIntoViewIfNeeded();
                waitShort();
                searchBtn.hover();
                waitShort();
                searchBtn.click();
                waitMedium();
                System.out.println("[TIMESJOBS] Search button clicked.");
            } catch (Exception e) {
                ScreenshotUtils.captureScreenshot(page, JobPortal.TIMESJOBS, "timesjobs_search_click_failure.png");
                System.out.println("[TIMESJOBS] Search button clicked failed. Initiating ENTER fallback strategy...");

                boolean searchSuccess = false;
                for (int enterRetry = 1; enterRetry <= 3; enterRetry++) {
                    try {
                        System.out.println("[TIMESJOBS] ENTER query retry: " + enterRetry);
                        keywordField.press("Enter");
                        page.waitForTimeout(2500);

                        if (getJobCount() > 0) {
                            searchSuccess = true;
                            System.out.println("[TIMESJOBS] ENTER fallback search successful.");
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (!searchSuccess) {
                    throw new RuntimeException("TimesJobs search failed using both button and keyboard fallbacks.");
                }
            }

            // Pause after navigation to wait for list to fully render
            page.waitForTimeout(8000);

            try {
                page.locator("body").click(new Locator.ClickOptions().setPosition(50, 50));
            } catch (Exception ignored) {
            }

            // Stabilize DOM layout completely
            waitForDOMStabilization();

            // Smooth scrolling to render any dynamic lazy-loaded elements
            WaitUtils.scrollToBottom(page);

            int totalJobs = getJobCount();
            System.out.println("[TIMESJOBS] TimesJobs jobs extracted: " + totalJobs);

        } catch (Exception e) {
            ScreenshotUtils.captureScreenshot(page, JobPortal.TIMESJOBS, "timesjobs_search_failure.png");
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