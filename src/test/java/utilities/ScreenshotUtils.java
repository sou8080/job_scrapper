package utilities;

import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.microsoft.playwright.Page;

public class ScreenshotUtils {

        private static final String BASE_DIR = "src/test/resources/Screenshots";

        private ScreenshotUtils() {
        }

        /**
         * Delete only old screenshots before new execution.
         * Folder structure will remain intact.
         */
        public static void clearOldScreenshots() {

                try {

                        Path basePath = Paths.get(BASE_DIR);

                        if (!Files.exists(basePath)) {
                                return;
                        }

                        // Loop all portal folders
                        try (DirectoryStream<Path> folders = Files.newDirectoryStream(basePath)) {

                                for (Path folder : folders) {

                                        if (!Files.isDirectory(folder)) {
                                                continue;
                                        }

                                        // Delete only png files
                                        try (DirectoryStream<Path> screenshots = Files.newDirectoryStream(folder,
                                                        "*.png")) {

                                                for (Path screenshot : screenshots) {

                                                        try {
                                                                Files.deleteIfExists(screenshot);

                                                        } catch (Exception e) {

                                                                System.out.println(
                                                                                "Failed to delete screenshot : "
                                                                                                + screenshot.getFileName());
                                                        }
                                                }
                                        }
                                }
                        }

                        System.out.println("Old screenshots cleared successfully.");

                } catch (IOException e) {

                        System.out.println("Failed to clear old screenshots.");
                        e.printStackTrace();
                }
        }

        public static void captureScreenshot(
                        Page page,
                        JobPortal portal,
                        String fileName) {

                try {

                        String folderName = portal != null
                                        ? portal.getPortalName() + "_ss"
                                        : "others_ss";

                        Path directoryPath = Paths.get(BASE_DIR, folderName);

                        // Auto create folders if missing
                        Files.createDirectories(directoryPath);

                        String cleanFileName = fileName
                                        .replace(".png", "")
                                        .replaceAll("[\\\\/:*?\"<>|]", "_")
                                        .trim();

                        String timestamp = LocalDateTime.now()
                                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

                        String finalFileName = cleanFileName + "_" + timestamp + ".png";

                        Path finalPath = directoryPath.resolve(finalFileName);

                        // Capture full page screenshot
                        page.screenshot(
                                        new Page.ScreenshotOptions()
                                                        .setPath(finalPath)
                                                        .setFullPage(true));

                        System.out.println("Screenshot captured : " + finalPath);

                } catch (Exception e) {

                        System.out.println(
                                        "Failed to capture screenshot : " + fileName);

                        e.printStackTrace();
                }
        }
}