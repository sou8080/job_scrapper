package utilities;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.microsoft.playwright.Page;

public class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    public static void captureScreenshot(
            Page page,
            JobPortal portal,
            String fileName) {

        try {

            // ==========================================
            // BASE SCREENSHOT DIRECTORY
            // ==========================================

            String baseDir = "src/test/resources/Screenshots/";

            // ==========================================
            // PORTAL FOLDER
            // ==========================================

            String portalFolder = portal.getPortalName() + "_ss";

            // ==========================================
            // FINAL DIRECTORY
            // ==========================================

            Path directoryPath = Paths.get(
                    baseDir,
                    portalFolder);

            // ==========================================
            // CREATE DIRECTORY IF NOT EXISTS
            // ==========================================

            if (!Files.exists(directoryPath)) {

                Files.createDirectories(
                        directoryPath);
            }

            // ==========================================
            // FINAL FILE PATH
            // ==========================================

            Path finalPath = directoryPath.resolve(
                    fileName);

            // ==========================================
            // CAPTURE SCREENSHOT
            // ==========================================

            page.screenshot(

                    new Page.ScreenshotOptions()

                            .setPath(
                                    finalPath));

            System.out.println(
                    "Screenshot captured : "
                            + finalPath);

        } catch (Exception e) {

            System.out.println(
                    "Failed to capture screenshot : "
                            + fileName);

            e.printStackTrace();
        }
    }
}