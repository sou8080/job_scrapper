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

            browser = browserType.launch(

                    new BrowserType.LaunchOptions()

                            .setHeadless(
                                    FrameworkConfig.HEADLESS)

                            .setSlowMo(
                                    FrameworkConfig.HEADLESS
                                            ? 80
                                            : 0)

                            .setArgs(Arrays.asList(

                                    // ==========================================
                                    // ANTI BOT
                                    // ==========================================

                                    "--disable-blink-features=AutomationControlled",

                                    // ==========================================
                                    // STABILITY
                                    // ==========================================

                                    "--disable-dev-shm-usage",

                                    "--no-sandbox",

                                    "--disable-setuid-sandbox",

                                    "--disable-notifications",

                                    "--disable-popup-blocking",

                                    "--disable-infobars",

                                    "--disable-gpu",

                                    "--disable-extensions",

                                    "--disable-background-networking",

                                    "--disable-sync",

                                    "--disable-default-apps",

                                    "--disable-renderer-backgrounding"

                            )));

            initialized = true;

            System.out.println(
                    "BrowserManager: Browser initialized successfully.");

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