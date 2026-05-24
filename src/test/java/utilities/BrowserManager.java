package utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import config.FrameworkConfig;

public class BrowserManager {

    private static final ThreadLocal<Playwright> threadPlaywright = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> threadContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> threadPage = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> threadForceHeadful = ThreadLocal.withInitial(() -> false);

    private BrowserManager() {
    }

    // ==========================================
    // INIT BROWSER PER PORTAL (THREAD-SAFE)
    // ==========================================

    public static synchronized void initBrowser(JobPortal portal) {
        if (portal == null) {
            throw new IllegalArgumentException("Portal cannot be null during browser initialization.");
        }

        if (threadContext.get() != null) {
            System.out.println("[BROWSER] Context already initialized for thread: " + Thread.currentThread().getName());
            return;
        }

        try {
            if (threadPlaywright.get() == null) {
                threadPlaywright.set(Playwright.create());
            }

            Playwright playwright = threadPlaywright.get();
            BrowserType browserType;
            BrowserType.LaunchPersistentContextOptions options = new BrowserType.LaunchPersistentContextOptions();

            // ==========================================
            // BROWSER SELECTION
            // ==========================================

            switch (FrameworkConfig.BROWSER.toLowerCase()) {
                case "chrome":
                    browserType = playwright.chromium();
                    options.setChannel("chrome");
                    break;
                case "edge":
                case "msedge":
                    browserType = playwright.chromium();
                    options.setChannel("msedge");
                    break;
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
            // CONFIGURATION & ANTI-DETECTION
            // ==========================================

            boolean isHeadless = FrameworkConfig.HEADLESS && !threadForceHeadful.get();
            options.setHeadless(isHeadless);

            // Add SlowMo in headful mode to mimic human action speed
            options.setSlowMo(isHeadless ? 0 : 150);

            options.setViewportSize(1920, 1080);
            options.setLocale("en-US");
            options.setTimezoneId("Asia/Kolkata");
            options.setIgnoreHTTPSErrors(true);

            // A high-quality standard user agent to avoid automation flagging
            options.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/122.0.0.0 Safari/537.36");

            // ==========================================
            // SAFE STEALTH BROWSER ARGUMENTS
            // ==========================================

            options.setArgs(Arrays.asList(
                    "--start-maximized",
                    "--disable-blink-features=AutomationControlled",
                    "--disable-dev-shm-usage",
                    "--disable-notifications",
                    "--disable-popup-blocking",
                    "--no-first-run",
                    "--no-default-browser-check",
                    "--password-store=basic",
                    "--disable-save-password-bubble",
                    "--disable-infobars",
                    "--no-sandbox"
            ));

            // ==========================================
            // PORTAL STATE RESTORE (SESSION COOKIES)
            // ==========================================

            Path authDir = Paths.get("auth");
            if (!Files.exists(authDir)) {
                Files.createDirectories(authDir);
            }

            Path authStatePath = authDir.resolve(portal.getPortalName() + ".json");

            // ==========================================
            // PERSISTENT CONTEXT (THREAD-ISOLATED PATH)
            // ==========================================

            // Use a unique profile subdirectory per portal per thread to avoid lock contention
            String profileDirName = portal.getPortalName() + "_" + Thread.currentThread().getName().replaceAll("[^a-zA-Z0-9]", "_");
            Path profilePath = Paths.get("chrome-profile", profileDirName);
            
            System.out.println("[BROWSER] Launching context with profile path: " + profilePath.toAbsolutePath());
            
            BrowserContext context = browserType.launchPersistentContext(profilePath, options);

            // Manual cookie injection for session persistence
            restoreCookiesIfStateExists(context, authStatePath);

            // ==========================================
            // INJECT STEALTH SCRIPTS
            // ==========================================

            // Mask the navigator.webdriver property
            context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => false});");
            // Inject dummy chrome runtime properties
            context.addInitScript("window.chrome = { runtime: {} };");
            // Fake plugins list
            context.addInitScript("Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3,4,5]});");
            // Fake standard languages
            context.addInitScript("Object.defineProperty(navigator, 'languages', {get: () => ['en-US','en']});");

            threadContext.set(context);

            System.out.println("==========================================");
            System.out.println("Persistent Browser Context Initialized.");
            System.out.println("Portal   : " + portal.name());
            System.out.println("Thread   : " + Thread.currentThread().getName());
            System.out.println("Headless : " + isHeadless);
            System.out.println("==========================================");

        } catch (IOException e) {
            throw new RuntimeException("Failed to create auth directory or profile path", e);
        } catch (Exception e) {
            closeBrowser();
            throw new RuntimeException("BrowserManager thread-safe initialization failed", e);
        }
    }

    // ==========================================
    // GET CONTEXT
    // ==========================================

    public static BrowserContext getContext(JobPortal portal) {
        if (threadContext.get() == null) {
            initBrowser(portal);
        }
        return threadContext.get();
    }

    // ==========================================
    // CREATE SAFE PAGE
    // ==========================================

    public static Page createFreshPage(JobPortal portal) {
        try {
            BrowserContext context = getContext(portal);
            if (context == null) {
                throw new RuntimeException("Browser context is null for thread " + Thread.currentThread().getName());
            }

            Page page;
            if (context.pages().isEmpty()) {
                page = context.newPage();
            } else {
                page = context.pages().get(0);
            }

            page.bringToFront();
            threadPage.set(page);
            return page;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create fresh page for thread " + Thread.currentThread().getName(), e);
        }
    }

    // ==========================================
    // ANTIBOT HEADFUL RELAUNCH FALLBACK
    // ==========================================

    public static BrowserContext relaunchInHeadful(JobPortal portal) {
        System.out.println("[FALLBACK] ⚠️ Relaunching context in headful mode with SlowMo for portal: " + portal.getPortalName());
        
        // 1. Close current page and context
        closeBrowser();
        
        // 2. Set force headful flag
        threadForceHeadful.set(true);
        
        // 3. Initialize fresh context
        initBrowser(portal);
        
        return threadContext.get();
    }

    // ==========================================
    // CLOSE BROWSER (THREAD-SAFE)
    // ==========================================

    public static void closeBrowser() {
        System.out.println("[BROWSER] Tearing down browser for thread: " + Thread.currentThread().getName());

        Page page = threadPage.get();
        if (page != null) {
            try {
                if (!page.isClosed()) {
                    page.close();
                }
            } catch (Exception ignored) {
            }
            threadPage.remove();
        }

        BrowserContext context = threadContext.get();
        if (context != null) {
            try {
                context.close();
            } catch (Exception ignored) {
            }
            threadContext.remove();
        }

        Playwright playwright = threadPlaywright.get();
        if (playwright != null) {
            try {
                playwright.close();
            } catch (Exception ignored) {
            }
            threadPlaywright.remove();
        }

        threadForceHeadful.remove();
        System.out.println("[BROWSER] Teardown complete for thread: " + Thread.currentThread().getName());
    }

    private static void restoreCookiesIfStateExists(BrowserContext context, Path authStatePath) {
        if (!Files.exists(authStatePath)) {
            return;
        }
        try {
            if (Files.size(authStatePath) == 0) {
                return;
            }
            System.out.println("[BROWSER] Restoring cookies from storage state JSON: " + authStatePath.toAbsolutePath());
            String json = Files.readString(authStatePath);
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(json);
            com.fasterxml.jackson.databind.JsonNode cookiesNode = root.get("cookies");
            
            if (cookiesNode != null && cookiesNode.isArray()) {
                java.util.List<com.microsoft.playwright.options.Cookie> cookies = new java.util.ArrayList<>();
                for (com.fasterxml.jackson.databind.JsonNode cookieNode : cookiesNode) {
                    String name = cookieNode.has("name") ? cookieNode.get("name").asText() : "";
                    String value = cookieNode.has("value") ? cookieNode.get("value").asText() : "";
                    if (name.isEmpty() || value.isEmpty()) {
                        continue;
                    }
                    com.microsoft.playwright.options.Cookie cookie = new com.microsoft.playwright.options.Cookie(name, value);
                    if (cookieNode.has("domain")) cookie.domain = cookieNode.get("domain").asText();
                    if (cookieNode.has("path")) cookie.path = cookieNode.get("path").asText();
                    if (cookieNode.has("expires")) cookie.expires = cookieNode.get("expires").asDouble();
                    if (cookieNode.has("httpOnly")) cookie.httpOnly = cookieNode.get("httpOnly").asBoolean();
                    if (cookieNode.has("secure")) cookie.secure = cookieNode.get("secure").asBoolean();
                    if (cookieNode.has("sameSite")) {
                        String sameSite = cookieNode.get("sameSite").asText();
                        try {
                            cookie.sameSite = com.microsoft.playwright.options.SameSiteAttribute.valueOf(sameSite.toUpperCase());
                        } catch (Exception ignored) {}
                    }
                    cookies.add(cookie);
                }
                if (!cookies.isEmpty()) {
                    context.addCookies(cookies);
                    System.out.println("[BROWSER] Successfully restored " + cookies.size() + " cookies from " + authStatePath.getFileName());
                }
            }
        } catch (Exception e) {
            System.err.println("[BROWSER] Failed to restore cookies from storage state JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}