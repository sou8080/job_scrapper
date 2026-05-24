package base;

import java.io.File;
import java.lang.reflect.Method;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

import config.FrameworkConfig;
import services.JobCollectorService;
import services.JobFileStoreService;
import utilities.BrowserManager;
import utilities.JobPortal;
import utilities.ScreenshotUtils;
import utilities.TelegramNotifier;
import utilities.WaitUtils;

public class Test_Base_Class {

        protected BrowserContext context;
        protected Page page;
        protected JobPortal currentPortal;

        // ==========================================
        // BEFORE SUITE
        // ==========================================

        @BeforeSuite(alwaysRun = true)
        public void beforeSuite() {
                System.out.println("[SUITE] Initializing Job Scraper Execution...");
                TelegramNotifier.sendMessage("🚀 Job Scraper Execution Started");
                ScreenshotUtils.clearOldScreenshots();
        }

        // ==========================================
        // SETUP (THREAD-SAFE & PORTAL-AWARE)
        // ==========================================

        @BeforeMethod(alwaysRun = true)
        public void setupBrowser(Method method) {
                currentPortal = getPortalFromMethod(method);
                System.out.println("[SETUP] Preparing browser for portal: " + currentPortal.name()
                                + " on thread: " + Thread.currentThread().getName());

                int attempts = 0;
                Exception lastException = null;

                while (attempts < FrameworkConfig.MAX_RETRIES) {
                        try {
                                attempts++;

                                context = BrowserManager.getContext(currentPortal);
                                if (context == null) {
                                        throw new RuntimeException("Browser context initialization failed.");
                                }

                                page = BrowserManager.createFreshPage(currentPortal);
                                if (page == null) {
                                        throw new RuntimeException("Page creation failed.");
                                }

                                page.setDefaultTimeout(FrameworkConfig.DEFAULT_TIMEOUT);
                                page.setDefaultNavigationTimeout(FrameworkConfig.NAVIGATION_TIMEOUT);

                                System.out.println("[SETUP] Browser setup completed on attempt: " + attempts);
                                return;

                        } catch (Exception e) {
                                lastException = e;
                                System.err.println("[SETUP] Setup failed. Attempt " + attempts + " of "
                                                + FrameworkConfig.MAX_RETRIES);
                                e.printStackTrace();

                                try {
                                        BrowserManager.closeBrowser();
                                } catch (Exception ignored) {
                                }

                                if (attempts >= FrameworkConfig.MAX_RETRIES) {
                                        String errorMsg = "❌ Playwright setup failed after "
                                                        + FrameworkConfig.MAX_RETRIES
                                                        + " retries for " + currentPortal.name() + "\n\nReason: "
                                                        + e.getMessage();
                                        TelegramNotifier.sendMessage(errorMsg);
                                        throw new RuntimeException("Playwright setup failed after maximum retries",
                                                        lastException);
                                }

                                try {
                                        Thread.sleep(3000);
                                } catch (InterruptedException ie) {
                                        Thread.currentThread().interrupt();
                                }
                        }
                }
        }

        // ==========================================
        // INTERACTION HELPERS (LEGACY SUPPORT)
        // ==========================================

        protected void humanDelay() {
                WaitUtils.humanDelay(page);
        }

        protected void navigateTo(String url) {
                try {
                        WaitUtils.safeNavigate(page, url, 3);
                        System.out.println("[NAVIGATE] Success: " + url);
                } catch (Exception e) {
                        try {
                                ScreenshotUtils.captureScreenshot(page, currentPortal, "navigation_failure.png");
                        } catch (Exception ignored) {
                        }

                        TelegramNotifier.sendMessage("❌ Navigation failed\n\n"
                                        + "URL : " + url + "\n\nReason : " + e.getMessage());
                        e.printStackTrace();
                }
        }

        // ==========================================
        // TEARDOWN (PER METHOD DISPOSAL)
        // ==========================================

        @AfterMethod(alwaysRun = true)
        public void tearDown() {
                try {
                        System.out.println("[TEARDOWN] Closing browser for method on thread: "
                                        + Thread.currentThread().getName());
                        BrowserManager.closeBrowser();
                } catch (Exception e) {
                        System.err.println("[TEARDOWN] Browser closure failed: " + e.getMessage());
                        e.printStackTrace();
                }
        }

        // ==========================================
        // AFTER SUITE (SAVE JOBS & GENERATE REPORTS)
        // ==========================================

        @AfterSuite(alwaysRun = true)
        public void afterSuite() {
                try {
                        System.out.println("[SUITE] Scrape finished. Storing results...");
                        JobFileStoreService.saveJobs(JobCollectorService.getAllJobs());

                        String reportPath = "output/latest_jobs.txt";
                        File reportFile = new File(reportPath);
                        int totalJobs = JobCollectorService.getTotalJobs();

                        TelegramNotifier.sendMessage("🎉 Job Scraper Execution Completed\n\n"
                                        + "📊 Total Unique Jobs Stored : " + totalJobs + "\n\n"
                                        + "📎 latest_jobs.txt attached");

                        if (reportFile.exists()) {
                                TelegramNotifier.sendDocument(reportPath);
                                System.out.println("[SUITE] Scrape report sent successfully to Telegram.");
                        } else {
                                TelegramNotifier.sendMessage("❌ Report file not found\n\nPath: " + reportPath);
                                System.err.println("[SUITE] Report file not found at: " + reportPath);
                        }

                } catch (Exception e) {
                        TelegramNotifier.sendMessage(
                                        "❌ Failed to compile or send final suite report\n\n" + e.getMessage());
                        e.printStackTrace();
                } finally {
                        closeSuiteBrowser();
                }
        }

        public static void closeSuiteBrowser() {
                System.out.println("[SUITE] Cleaning up global browser references.");
                BrowserManager.closeBrowser();
        }

        // ==========================================
        // UTILITIES
        // ==========================================

        private JobPortal getPortalFromMethod(Method method) {
                String name = method.getName().toLowerCase();
                if (name.contains("linkedin")) {
                        return JobPortal.LINKEDIN;
                } else if (name.contains("naukri")) {
                        return JobPortal.NAUKRI;
                } else if (name.contains("indeed")) {
                        return JobPortal.INDEED;
                } else if (name.contains("shine")) {
                        return JobPortal.SHINE;
                } else if (name.contains("foundit")) {
                        return JobPortal.FOUNDIT;
                } else if (name.contains("timesjobs")) {
                        return JobPortal.TIMESJOBS;
                }
                return JobPortal.LINKEDIN; // Default fallback portal
        }
}