package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import config.FrameworkConfig;

public class TelegramNotifier {

    private TelegramNotifier() {
    }

    // ==========================================
    // SEND MESSAGE
    // ==========================================

    public static void sendMessage(
            String message) {

        try {

            String apiUrl =

                    "https://api.telegram.org/bot"
                            + FrameworkConfig.TELEGRAM_BOT_TOKEN
                            + "/sendMessage";

            String payload =

                    "chat_id="
                            + FrameworkConfig.TELEGRAM_CHAT_ID
                            + "&text="
                            + message.replace(" ", "%20");

            URL url = new URL(apiUrl);

            HttpURLConnection connection =

                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(
                    "POST");

            connection.setDoOutput(
                    true);

            OutputStream outputStream =

                    connection.getOutputStream();

            outputStream.write(
                    payload.getBytes());

            outputStream.flush();

            outputStream.close();

            int responseCode = connection.getResponseCode();

            System.out.println(
                    "Telegram message sent : "
                            + responseCode);

        } catch (Exception e) {

            System.out.println(
                    "Failed to send telegram message.");

            e.printStackTrace();
        }
    }

    // ==========================================
    // SEND DOCUMENT
    // ==========================================

    public static void sendDocument(
            String filePath) {

        try {

            File file = new File(filePath);

            String boundary = Long.toHexString(
                    System.currentTimeMillis());

            URL url = new URL(

                    "https://api.telegram.org/bot"
                            + FrameworkConfig.TELEGRAM_BOT_TOKEN
                            + "/sendDocument");

            HttpURLConnection connection =

                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(
                    "POST");

            connection.setDoOutput(
                    true);

            connection.setRequestProperty(

                    "Content-Type",

                    "multipart/form-data; boundary="
                            + boundary);

            OutputStream output = connection.getOutputStream();

            // ==========================================
            // CHAT ID
            // ==========================================

            output.write(

                    ("--" + boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n"
                            + FrameworkConfig.TELEGRAM_CHAT_ID
                            + "\r\n")

                            .getBytes());

            // ==========================================
            // FILE
            // ==========================================

            output.write(

                    ("--" + boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"document\"; filename=\""
                            + file.getName()
                            + "\"\r\n"
                            + "Content-Type: application/octet-stream\r\n\r\n")

                            .getBytes());

            FileInputStream fis = new FileInputStream(file);

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {

                output.write(
                        buffer,
                        0,
                        bytesRead);
            }

            fis.close();

            output.write(

                    ("\r\n--"
                            + boundary
                            + "--\r\n")

                            .getBytes());

            output.flush();

            output.close();

            int responseCode = connection.getResponseCode();

            System.out.println(
                    "Telegram file sent : "
                            + responseCode);

        } catch (Exception e) {

            System.out.println(
                    "Failed to send telegram file.");

            e.printStackTrace();
        }
    }
}