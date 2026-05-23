package utilities;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserType;
import config.FrameworkConfig;

/**
 * Singleton manager for Playwright browser lifecycle.
 * Ensures a single Playwright instance and Browser are shared across all tests.
 */
public class BrowserManager {
    private static Playwright playwright;
    private static Browser browser;
    private static boolean initialized = false;

    private BrowserManager() {
        // Prevent instantiation
    }

    /**
     * Initialize Playwright and launch the browser if not already done.
     * This method is thread‑safe and can be called from multiple test classes.
     */
    public synchronized static void initBrowser() {
        if (initialized) {
            return;
        }
        try {
            playwright = Playwright.create();
            BrowserType browserType;
            switch (FrameworkConfig.BROWSER.toLowerCase()) {
                case "firefox":
                    browserType = playwright.firefox();
                    break;
                case "webkit":
                    browserType = playwright.webkit();
                    break;
                default:
                    browserType = playwright.chromium();
            }
            browser = browserType.launch(new BrowserType.LaunchOptions()
                    .setHeadless(FrameworkConfig.HEADLESS)
                    .setArgs(java.util.Arrays.asList(
                            "--disable-notifications",
                            "--disable-popup-blocking",
                            "--disable-save-password-bubble",
                            "--disable-infobars",
                            "--disable-geolocation",
                            "--disable-blink-features=AutomationControlled",
                            "--disable-dev-shm-usage",
                            "--no-sandbox",
                            "--disable-web-security",
                            "--disable-features=IsolateOrigins,site-per-process")));
            initialized = true;
            System.out.println("BrowserManager: Browser initialized successfully.");
        } catch (Exception e) {
            System.out.println("BrowserManager: Failed to initialize browser.");
            e.printStackTrace();
            throw e;
        }
    }

    public static Playwright getPlaywright() {
        if (!initialized) {
            initBrowser();
        }
        return playwright;
    }

    public static Browser getBrowser() {
        if (!initialized) {
            initBrowser();
        }
        return browser;
    }

    /**
     * Close the shared browser and Playwright instance.
     * Safe to call multiple times.
     */
    public synchronized static void closeBrowser() {
        try {
            if (browser != null) {
                browser.close();
                browser = null;
            }
            if (playwright != null) {
                playwright.close();
                playwright = null;
            }
            initialized = false;
            System.out.println("BrowserManager: Browser and Playwright closed.");
        } catch (Exception e) {
            System.out.println("BrowserManager: Error during close.");
            e.printStackTrace();
        }
    }
}
