package utilities;

import java.util.Random;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Common wait and navigation utilities.
 */
public class WaitUtils {

    private static final Random RANDOM = new Random();

    /**
     * Random human-like delay.
     */
    public static void humanDelay(Page page, int minMillis, int maxMillis) {

        int delay = minMillis + RANDOM.nextInt(maxMillis - minMillis + 1);

        try {

            Thread.sleep(delay);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    /**
     * Default delay.
     */
    public static void humanDelay(Page page) {

        humanDelay(page, 500, 1500);
    }

    /**
     * Safe page navigation with retry.
     */
    public static void safeNavigate(Page page, String url, int attempts) {

        int currentAttempt = 0;

        while (currentAttempt < attempts) {

            try {

                currentAttempt++;

                page.navigate(url);

                page.waitForLoadState();

                return;

            } catch (Exception e) {

                if (currentAttempt >= attempts) {

                    throw new RuntimeException(
                            "Navigation failed for URL : " + url,
                            e);
                }

                humanDelay(page, 800, 1500);
            }
        }
    }

    /**
     * Scroll page to bottom.
     */
    public static void scrollToBottom(Page page) {

        try {

            page.evaluate(
                    "window.scrollTo(0, document.body.scrollHeight)");

            humanDelay(page, 1000, 2000);

        } catch (Exception ignored) {
        }
    }

    /**
     * Safe locator click.
     */
    public static void safeClick(Locator locator) {

        try {

            locator.waitFor(
                    new Locator.WaitForOptions()
                            .setState(
                                    WaitForSelectorState.VISIBLE)
                            .setTimeout(5000));

            locator.click();

        } catch (Exception e) {

            try {

                locator.evaluate(
                        "element => element.click()");

            } catch (Exception ignored) {

                throw e;
            }
        }
    }
}