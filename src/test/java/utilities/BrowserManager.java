package utilities;

import java.util.Arrays;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

import config.FrameworkConfig;

public class BrowserManager {

    private static Playwright playwright;
    private static Browser browser;
    private static boolean initialized = false;

    private BrowserManager() {
    }

    // ==========================================
    // INIT BROWSER
    // ==========================================

    public synchronized static void initBrowser() {
        if (initialized) {
            System.out.println(
                    "Browser already initialized.");
            return;
        }

        try {
            playwright = Playwright.create();
            BrowserType browserType;
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();

            // ==========================================
            // BROWSER SELECTION
            // ==========================================

            switch (FrameworkConfig.BROWSER.toLowerCase()) {

                // ==========================================
                // CHROME
                // ==========================================

                case "chrome":
                    browserType = playwright.chromium();
                    options.setChannel("chrome");
                    break;

                // ==========================================
                // MICROSOFT EDGE
                // ==========================================

                case "edge":
                case "msedge":
                    browserType = playwright.chromium();
                    options.setChannel("msedge");
                    break;

                // ==========================================
                // FIREFOX
                // ==========================================

                case "firefox":
                    browserType = playwright.firefox();
                    break;

                // ==========================================
                // WEBKIT
                // ==========================================

                case "webkit":
                    browserType = playwright.webkit();
                    break;

                // ==========================================
                // DEFAULT CHROMIUM
                // ==========================================

                default:
                    browserType = playwright.chromium();
            }

            // ==========================================
            // HEADLESS
            // ==========================================

            options.setHeadless(
                    FrameworkConfig.HEADLESS);

            // ==========================================
            // SLOWMO
            // ==========================================

            options.setSlowMo(

                    FrameworkConfig.HEADLESS
                            ? 100
                            : 50);

            // ==========================================
            // ANTI BOT + STABILITY
            // ==========================================

            options.setArgs(Arrays.asList(

                    // ==========================================
                    // ANTI BOT
                    // ==========================================

                    "--disable-blink-features=AutomationControlled",
                    "--disable-features=IsolateOrigins,site-per-process",
                    "--disable-web-security",
                    "--allow-running-insecure-content",

                    // ==========================================
                    // STABILITY
                    // ==========================================

                    "--disable-dev-shm-usage",
                    "--no-sandbox",
                    "--disable-setuid-sandbox",
                    "--disable-notifications",
                    "--disable-popup-blocking",
                    "--disable-infobars",
                    "--disable-extensions",
                    "--disable-background-networking",
                    "--disable-sync",
                    "--disable-default-apps",
                    "--disable-renderer-backgrounding",
                    "--start-maximized",
                    "--disable-gpu"

            ));

            // ==========================================
            // LAUNCH BROWSER
            // ==========================================

            browser = browserType.launch(options);

            initialized = true;

            System.out.println(
                    "==========================================");

            System.out.println(
                    "Browser initialized successfully.");
            System.out.println(
                    "Browser : "
                            + FrameworkConfig.BROWSER);
            System.out.println(
                    "Headless : "
                            + FrameworkConfig.HEADLESS);
            System.out.println(
                    "==========================================");
        } catch (Exception e) {
            System.out.println(
                    "BrowserManager: Failed to initialize browser.");
            e.printStackTrace();
        }
    }

    // ==========================================
    // GET BROWSER
    // ==========================================

    public static Browser getBrowser() {

        if (!initialized) {
            initBrowser();
        }
        return browser;
    }

    // ==========================================
    // CLOSE BROWSER
    // ==========================================

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
            System.out.println(
                    "BrowserManager: Browser closed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}