package base;

import java.nio.file.Paths;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ColorScheme;
import com.microsoft.playwright.options.LoadState;

import utilities.BrowserManager;

public class Test_Base_Class {

    protected BrowserContext context;

    protected Page page;

    // ==========================================
    // SETUP
    // ==========================================

    @BeforeMethod(alwaysRun = true)
    public void setupBrowser() {

        try {

            Browser browser = BrowserManager.getBrowser();

            // ==========================================
            // CONTEXT
            // ==========================================

            context = browser.newContext(

                    new Browser.NewContextOptions()

                            // ==========================================
                            // VIEWPORT
                            // ==========================================

                            .setViewportSize(
                                    1920,
                                    1080)

                            // ==========================================
                            // USER AGENT
                            // ==========================================

                            .setUserAgent(

                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                            "Chrome/136.0.0.0 Safari/537.36")

                            // ==========================================
                            // LOCALE
                            // ==========================================

                            .setLocale(
                                    "en-US")

                            // ==========================================
                            // TIMEZONE
                            // ==========================================

                            .setTimezoneId(
                                    "Asia/Kolkata")

                            // ==========================================
                            // HTTPS
                            // ==========================================

                            .setIgnoreHTTPSErrors(
                                    true)

                            // ==========================================
                            // JAVASCRIPT
                            // ==========================================

                            .setJavaScriptEnabled(
                                    true)

                            // ==========================================
                            // COLOR SCHEME
                            // ==========================================

                            .setColorScheme(
                                    ColorScheme.LIGHT));

            // ==========================================
            // PAGE
            // ==========================================

            page = context.newPage();

            // ==========================================
            // REMOVE WEBDRIVER FLAG
            // ==========================================

            page.addInitScript(

                    "Object.defineProperty(" +
                            "navigator, " +
                            "'webdriver', {" +
                            "get: () => undefined" +
                            "});");

            // ==========================================
            // TIMEOUTS
            // ==========================================

            page.setDefaultTimeout(
                    15000);

            page.setDefaultNavigationTimeout(
                    30000);

            System.out.println(
                    "Browser setup completed successfully.");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ==========================================
    // COMMON NAVIGATION
    // ==========================================

    protected void navigateTo(
            String url) {

        try {

            page.navigate(url);

            page.waitForLoadState(
                    LoadState.DOMCONTENTLOADED);

            page.waitForTimeout(5000);

            System.out.println(
                    "Navigation successful : "
                            + url);

        } catch (Exception e) {

            try {

                page.screenshot(

                        new Page.ScreenshotOptions()

                                .setPath(
                                        Paths.get(
                                                "navigation_failure.png")));

            } catch (Exception ignored) {
            }

            e.printStackTrace();
        }
    }

    // ==========================================
    // TEARDOWN
    // ==========================================

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        try {

            if (page != null) {

                page.close();

                System.out.println(
                        "Page closed.");
            }

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
    // CLOSE SHARED BROWSER
    // ==========================================

    public static void closeBrowser() {

        BrowserManager.closeBrowser();
    }
}