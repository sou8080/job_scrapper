package base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;

import config.FrameworkConfig;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import utilities.BrowserManager;

public class Test_Base_Class {

    protected static Playwright playwright;

    protected static Browser browser;

    protected BrowserContext context;

    protected Page page;

    // ==========================================
    // SETUP
    // ==========================================

    @BeforeMethod(alwaysRun = true)
    public void setupBrowser() {

        try {

            // ==========================================
            // AVOID MULTIPLE BROWSER LAUNCH
            // ==========================================

            if (browser != null) {

                System.out.println(
                        "Browser already initialized.");

                return;
            }

            // ==========================================
            // PLAYWRIGHT INIT
            // ==========================================

            playwright = Playwright.create();

            BrowserType browserType;

            // ==========================================
            // BROWSER SELECTION
            // ==========================================

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

            // ==========================================
            // LAUNCH BROWSER
            // ==========================================

            try {
        // Context and page will be created in individual test methods via SessionLoginManager.
        // No need to create them here.
        // Ensure BrowserManager is initialized.
        BrowserManager.initBrowser();
        playwright = BrowserManager.getPlaywright();
        browser = BrowserManager.getBrowser();
            } catch (Exception e) {
                System.out.println("Browser initialization via BrowserManager failed.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // COMMON NAVIGATION
    // ==========================================

    protected void navigateTo(String url) {

        try {

            if (page == null) {

                throw new RuntimeException(
                        "Page is null. Initialize page before navigation.");
            }

            page.navigate(url);

            page.waitForLoadState(
                    LoadState.NETWORKIDLE);

            System.out.println(
                    "Navigation successful : "
                            + url);

        } catch (Exception e) {

            System.out.println(
                    "Navigation failed : "
                            + url);

            e.printStackTrace();
        }
    }

    protected void navigateToPortal(utilities.JobPortal portal) {
        navigateTo(config.PortalConfig.getUrl(portal));
    }

    // ==========================================
    // TEARDOWN
    // ==========================================

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        try {

            // ==========================================
            // CLOSE PAGE
            // ==========================================

            if (page != null) {

                page.close();

                System.out.println(
                        "Page closed.");
            }

            // ==========================================
            // CLOSE CONTEXT
            // ==========================================

            if (context != null) {

                context.close();

                System.out.println(
                        "Browser context closed.");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ==========================================
    // GLOBAL BROWSER CLOSE
    // ==========================================

    public static void closeBrowser() {
        // Delegate shutdown to BrowserManager for consistent cleanup
        BrowserManager.closeBrowser();
    }
}