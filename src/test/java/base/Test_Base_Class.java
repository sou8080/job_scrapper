package base;

import java.io.File;
import java.nio.file.Paths;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ColorScheme;
import com.microsoft.playwright.options.LoadState;

import services.JobCollectorService;
import services.JobFileStoreService;
import utilities.BrowserManager;
import utilities.TelegramNotifier;

public class Test_Base_Class {

    protected BrowserContext context;

    protected Page page;

    // ==========================================
    // BEFORE SUITE
    // ==========================================

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {

        TelegramNotifier.sendMessage(

                "🚀 Job Scraper Execution Started");
    }

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

                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                                            +
                                            "AppleWebKit/537.36 (KHTML, like Gecko) "
                                            +
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
            // REMOVE AUTOMATION FLAGS
            // ==========================================

            page.addInitScript(

                    "Object.defineProperty("
                            +
                            "navigator, "
                            +
                            "'webdriver', {"
                            +
                            "get: () => undefined"
                            +
                            "});"

                            +

                            "window.chrome = { runtime: {} };"

                            +

                            "Object.defineProperty(navigator, 'plugins', {"
                            +
                            "get: () => [1, 2, 3, 4, 5]"
                            +
                            "});"

                            +

                            "Object.defineProperty(navigator, 'languages', {"
                            +
                            "get: () => ['en-US', 'en']"
                            +
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

            TelegramNotifier.sendMessage(

                    "❌ Browser setup failed\n\n"
                            + e.getMessage());

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

                String screenshotPath = "navigation_failure.png";

                page.screenshot(

                        new Page.ScreenshotOptions()

                                .setPath(
                                        Paths.get(
                                                screenshotPath)));

                TelegramNotifier.sendDocument(
                        screenshotPath);

            } catch (Exception ignored) {
            }

            TelegramNotifier.sendMessage(

                    "❌ Navigation failed\n\n"
                            + "URL : "
                            + url
                            + "\n\nReason : "
                            + e.getMessage());

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
    // AFTER SUITE
    // ==========================================

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {

        try {

            // ==========================================
            // SAVE ALL JOBS
            // ==========================================

            JobFileStoreService.saveJobs(
                    JobCollectorService.getAllJobs());

            // ==========================================
            // REPORT PATH
            // ==========================================

            String reportPath = "output/latest_jobs.txt";

            File reportFile = new File(reportPath);

            // ==========================================
            // TOTAL UNIQUE JOBS
            // ==========================================

            int totalJobs = JobCollectorService.getTotalJobs();

            // ==========================================
            // FINAL MESSAGE
            // ==========================================

            TelegramNotifier.sendMessage(

                    "🎉 Job Scraper Execution Completed\n\n"

                            + "📊 Total Unique Jobs Stored : "
                            + totalJobs

                            + "\n\n"

                            + "📎 latest_jobs.txt attached");

            // ==========================================
            // SEND REPORT
            // ==========================================

            if (reportFile.exists()) {

                TelegramNotifier.sendDocument(
                        reportPath);

                System.out.println(
                        "Report sent successfully.");

            } else {

                TelegramNotifier.sendMessage(

                        "❌ Report file not found\n\n"
                                + reportPath);

                System.out.println(
                        "Report file not found.");
            }

        } catch (Exception e) {

            TelegramNotifier.sendMessage(

                    "❌ Failed to send final report\n\n"
                            + e.getMessage());

            e.printStackTrace();
        }

        // ==========================================
        // CLOSE BROWSER
        // ==========================================

        closeBrowser();
    }

    // ==========================================
    // CLOSE SHARED BROWSER
    // ==========================================

    public static void closeBrowser() {

        BrowserManager.closeBrowser();
    }
}