package services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.SearchConfig;

public class JobFilterService {

        // ==========================================
        // DAY LIMITS
        // ==========================================

        private static final int[] DAY_LIMITS = {
                        7,
                        14,
                        21,
                        28
        };

        // ==========================================
        // CURRENT LIMIT INDEX
        // ==========================================

        private static int currentLimitIndex = 0;

        // ==========================================
        // JOB FOUND FLAG
        // ==========================================

        private static boolean jobsFound = false;

        // ==========================================
        // NUMBER PATTERN
        // ==========================================

        private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

        // ==========================================
        // KEYWORD FILTER
        // FULLY DYNAMIC
        // ==========================================

        public static boolean isRelevant(
                        String text) {

                try {

                        // ==========================================
                        // NULL SAFE
                        // ==========================================

                        if (text == null
                                        || text.isBlank()) {

                                return false;
                        }

                        // ==========================================
                        // NORMALIZE ROLE
                        // ==========================================

                        String role = normalize(text);

                        // ==========================================
                        // KEYWORD LOOP
                        // ==========================================

                        for (String keyword : SearchConfig.KEYWORDS) {

                                if (keyword == null
                                                || keyword.isBlank()) {

                                        continue;
                                }

                                // ==========================================
                                // NORMALIZE KEYWORD
                                // ==========================================

                                String normalizedKeyword = normalize(keyword);

                                // ==========================================
                                // FULL PHRASE MATCH
                                // ==========================================

                                if (role.contains(normalizedKeyword)) {

                                        return true;
                                }

                                // ==========================================
                                // TOKEN MATCH
                                // ==========================================

                                String[] tokens = normalizedKeyword.split("\\s+");

                                int matchedTokens = 0;

                                for (String token : tokens) {

                                        // ==========================================
                                        // SKIP WEAK TOKENS
                                        // ==========================================

                                        if (token.length() <= 1) {

                                                continue;
                                        }

                                        // ==========================================
                                        // TOKEN MATCH
                                        // ==========================================

                                        if (role.contains(token)) {

                                                matchedTokens++;
                                        }
                                }

                                // ==========================================
                                // MATCH THRESHOLD
                                // ==========================================

                                if (tokens.length == 1
                                                && matchedTokens >= 1) {

                                        return true;
                                }

                                if (tokens.length == 2
                                                && matchedTokens >= 1) {

                                        return true;
                                }

                                if (tokens.length >= 3
                                                && matchedTokens >= 2) {

                                        return true;
                                }
                        }

                } catch (Exception ignored) {
                }

                return false;
        }

        // ==========================================
        // NORMALIZER
        // ==========================================

        private static String normalize(
                        String text) {

                return text.toLowerCase()

                                .replaceAll("[^a-z0-9 ]", " ")

                                .replaceAll("\\s+", " ")

                                .trim();
        }

        // ==========================================
        // DATE FILTER
        // ==========================================

        public static boolean isRecent(
                        String posted) {

                try {

                        // ==========================================
                        // NULL SAFE
                        // ==========================================

                        if (posted == null
                                        || posted.isBlank()
                                        || posted.equalsIgnoreCase("N/A")) {

                                return true;
                        }

                        // ==========================================
                        // NORMALIZE
                        // ==========================================

                        posted = posted
                                        .toLowerCase()
                                        .replace("posted", "")
                                        .replace("ago", "")
                                        .replace("+", "")
                                        .trim();

                        // ==========================================
                        // TODAY / HOURS / MINUTES
                        // ==========================================

                        if (posted.contains("today")
                                        || posted.contains("just posted")
                                        || posted.contains("hour")
                                        || posted.contains("minute")) {

                                jobsFound = true;

                                return true;
                        }

                        // ==========================================
                        // EXTRACT NUMBER
                        // ==========================================

                        Matcher matcher = NUMBER_PATTERN.matcher(posted);

                        if (!matcher.find()) {

                                return false;
                        }

                        int value = Integer.parseInt(
                                        matcher.group());

                        int currentLimit = DAY_LIMITS[currentLimitIndex];

                        // ==========================================
                        // DAYS
                        // ==========================================

                        if (posted.contains("day")) {

                                if (value <= currentLimit) {

                                        jobsFound = true;

                                        return true;
                                }

                                return false;
                        }

                        // ==========================================
                        // WEEKS
                        // ==========================================

                        if (posted.contains("week")) {

                                int allowedWeeks = currentLimit / 7;

                                if (value <= allowedWeeks) {

                                        jobsFound = true;

                                        return true;
                                }

                                return false;
                        }

                        // ==========================================
                        // MONTHS
                        // ONLY 1 MONTH
                        // ==========================================

                        if (posted.contains("month")) {

                                if (value == 1
                                                && currentLimit >= 28) {

                                        jobsFound = true;

                                        return true;
                                }

                                return false;
                        }

                } catch (Exception ignored) {
                }

                return false;
        }

        // ==========================================
        // INCREASE RANGE
        // ==========================================

        public static void increaseDayLimitIfNeeded() {

                // ==========================================
                // STOP IF JOBS FOUND
                // ==========================================

                if (jobsFound) {

                        System.out.println(

                                        "Jobs found within "
                                                        + getCurrentDayLimit()
                                                        + " days.");

                        return;
                }

                // ==========================================
                // INCREASE LIMIT
                // ==========================================

                if (currentLimitIndex < DAY_LIMITS.length - 1) {

                        currentLimitIndex++;

                        System.out.println(

                                        "No jobs found. Increasing range to "
                                                        + getCurrentDayLimit()
                                                        + " days.");
                }
        }

        // ==========================================
        // CURRENT LIMIT
        // ==========================================

        public static int getCurrentDayLimit() {

                return DAY_LIMITS[currentLimitIndex];
        }

        // ==========================================
        // RESET
        // ==========================================

        public static void resetDayLimit() {

                currentLimitIndex = 0;

                jobsFound = false;
        }

        // ==========================================
        // JOB FOUND STATUS
        // ==========================================

        public static boolean hasJobsFound() {

                return jobsFound;
        }
}