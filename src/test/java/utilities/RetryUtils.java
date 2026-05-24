package utilities;

import java.util.concurrent.Callable;

/**
 * Common retry utility.
 */
public class RetryUtils {

    private static final int DEFAULT_DELAY_MS = 500;

    /**
     * Retry Runnable with default delay.
     */
    public static void retry(Runnable action, int maxAttempts) {

        retry(action, maxAttempts, DEFAULT_DELAY_MS);
    }

    /**
     * Retry Runnable with custom delay.
     */
    public static void retry(
            Runnable action,
            int maxAttempts,
            int delayMs) {

        int attempts = 0;

        while (true) {

            try {

                attempts++;

                action.run();

                return;

            } catch (Exception e) {

                if (attempts >= maxAttempts) {

                    throw new RuntimeException(
                            "Action failed after "
                                    + maxAttempts
                                    + " attempts",
                            e);
                }

                sleep(delayMs);
            }
        }
    }

    /**
     * Retry Callable with default delay.
     */
    public static <T> T retry(
            Callable<T> callable,
            int maxAttempts) {

        return retry(
                callable,
                maxAttempts,
                DEFAULT_DELAY_MS);
    }

    /**
     * Retry Callable with custom delay.
     */
    public static <T> T retry(
            Callable<T> callable,
            int maxAttempts,
            int delayMs) {

        int attempts = 0;

        while (true) {

            try {

                attempts++;

                return callable.call();

            } catch (Exception e) {

                if (attempts >= maxAttempts) {

                    throw new RuntimeException(
                            "Callable failed after "
                                    + maxAttempts
                                    + " attempts",
                            e);
                }

                sleep(delayMs);
            }
        }
    }

    /**
     * Sleep utility.
     */
    private static void sleep(int millis) {

        try {

            Thread.sleep(millis);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}