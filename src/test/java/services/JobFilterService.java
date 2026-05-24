package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobFilterService {

        private static final Logger log = LoggerFactory.getLogger(JobFilterService.class);

        private static final int[] DAY_LIMITS = { 7, 14, 21, 28 };

        private static final int MIN_TOKEN_LENGTH = 2;

        private static final int MAX_LEVENSHTEIN_DIST = 1;

        private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

        private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                        DateTimeFormatter.ISO_LOCAL_DATE);

        private final List<String> keywords;

        private int dayLimitIndex = 0;

        private boolean jobsFound = false;

        public JobFilterService(List<String> keywords) {

                Objects.requireNonNull(
                                keywords,
                                "keywords must not be null");

                this.keywords = keywords;
        }

        public boolean isRelevant(String title) {

                if (isBlank(title)) {
                        return false;
                }

                String normalizedTitle = normalize(title);

                log.debug(
                                "Checking relevance for title='{}'",
                                normalizedTitle);

                for (String keyword : keywords) {

                        if (isBlank(keyword)) {
                                continue;
                        }

                        String normalizedKeyword = normalize(keyword);

                        if (normalizedTitle.contains(
                                        normalizedKeyword)) {

                                log.debug(
                                                "Exact match | title='{}' keyword='{}'",
                                                normalizedTitle,
                                                normalizedKeyword);

                                return true;
                        }

                        if (matchesByTokens(
                                        normalizedTitle,
                                        normalizedKeyword)) {

                                return true;
                        }
                }

                log.debug(
                                "Rejected | title='{}'",
                                normalizedTitle);

                return false;
        }

        public boolean isRecent(String posted) {

                if (isBlank(posted)
                                || posted.equalsIgnoreCase("N/A")) {

                        return true;
                }

                String normalized = normalizePostedDate(posted);

                if (isImmediatePost(normalized)) {

                        markJobFound();

                        return true;
                }

                Optional<LocalDate> parsedDate = tryParseDate(normalized);

                if (parsedDate.isPresent()) {

                        long daysAgo = ChronoUnit.DAYS.between(
                                        parsedDate.get(),
                                        LocalDate.now());

                        return acceptIfWithinLimit(
                                        (int) daysAgo);
                }

                return tryParseRelativeDate(normalized);
        }

        public void expandRangeIfNeeded() {

                if (jobsFound) {

                        log.info(
                                        "Jobs found within {} days — keeping current range.",
                                        currentDayLimit());

                        return;
                }

                if (dayLimitIndex < DAY_LIMITS.length - 1) {

                        dayLimitIndex++;

                        log.info(
                                        "No jobs found. Expanding range to {} days.",
                                        currentDayLimit());

                } else {

                        log.info(
                                        "Already at maximum range ({} days).",
                                        currentDayLimit());
                }
        }

        public void reset() {

                dayLimitIndex = 0;

                jobsFound = false;
        }

        public int currentDayLimit() {

                return DAY_LIMITS[dayLimitIndex];
        }

        public boolean hasJobsFound() {

                return jobsFound;
        }

        private boolean matchesByTokens(
                        String title,
                        String keyword) {

                String[] tokens = keyword
                                .toLowerCase()
                                .replace("or", " ")
                                .split("\\s+");

                for (String token : tokens) {

                        token = token.trim();

                        if (token.length() < MIN_TOKEN_LENGTH) {
                                continue;
                        }

                        if (titleContainsToken(title, token)) {

                                log.debug(
                                                "Token matched | title='{}' token='{}'",
                                                title,
                                                token);

                                return true;
                        }
                }

                return false;
        }

        private boolean titleContainsToken(
                        String title,
                        String token) {

                if (title.contains(token)) {
                        return true;
                }

                for (String titleToken : title.split("\\s+")) {

                        if (titleToken.startsWith(token)
                                        || token.startsWith(titleToken)
                                        || levenshteinDistance(
                                                        titleToken,
                                                        token) <= MAX_LEVENSHTEIN_DIST) {

                                return true;
                        }
                }

                return false;
        }

        private static String normalizePostedDate(
                        String raw) {

                return raw.toLowerCase()
                                .replace("posted on", "")
                                .replace("posted", "")
                                .replace("ago", "")
                                .replace("+", "")
                                .replace(":", "")
                                .trim();
        }

        private static boolean isImmediatePost(
                        String normalized) {

                return normalized.contains("today")
                                || normalized.contains("just posted")
                                || normalized.contains("hour")
                                || normalized.contains("minute");
        }

        private static Optional<LocalDate> tryParseDate(
                        String text) {

                for (DateTimeFormatter fmt : DATE_FORMATTERS) {

                        try {

                                return Optional.of(
                                                LocalDate.parse(text, fmt));

                        } catch (DateTimeParseException ignored) {
                        }
                }

                return Optional.empty();
        }

        private boolean tryParseRelativeDate(
                        String normalized) {

                Matcher m = NUMBER_PATTERN.matcher(normalized);

                if (!m.find()) {

                        log.debug(
                                        "Could not extract number from posted date: '{}'",
                                        normalized);

                        return false;
                }

                int value = Integer.parseInt(m.group());

                if (normalized.contains("day")) {
                        return acceptIfWithinLimit(value);
                }

                if (normalized.contains("week")) {
                        return acceptIfWithinLimit(value * 7);
                }

                if (normalized.contains("month")) {
                        return acceptIfWithinLimit(value * 30);
                }

                log.debug(
                                "Unknown date unit in: '{}'",
                                normalized);

                return false;
        }

        private boolean acceptIfWithinLimit(
                        int daysAgo) {

                if (daysAgo <= currentDayLimit()) {

                        markJobFound();

                        return true;
                }

                return false;
        }

        private void markJobFound() {

                jobsFound = true;
        }

        private static String normalize(
                        String text) {

                return text.toLowerCase()
                                .replaceAll("\\bor\\b", " ")
                                .replaceAll("[^a-z0-9 ]", " ")
                                .replaceAll("\\s+", " ")
                                .trim();
        }

        private static boolean isBlank(
                        String s) {

                return s == null || s.isBlank();
        }

        private static int levenshteinDistance(
                        String a,
                        String b) {

                int m = a.length();

                int n = b.length();

                int[][] dp = new int[m + 1][n + 1];

                for (int i = 0; i <= m; i++) {
                        dp[i][0] = i;
                }

                for (int j = 0; j <= n; j++) {
                        dp[0][j] = j;
                }

                for (int i = 1; i <= m; i++) {

                        for (int j = 1; j <= n; j++) {

                                int cost = a.charAt(i - 1) == b.charAt(j - 1)
                                                ? 0
                                                : 1;

                                dp[i][j] = Math.min(
                                                Math.min(
                                                                dp[i - 1][j] + 1,
                                                                dp[i][j - 1] + 1),
                                                dp[i - 1][j - 1] + cost);
                        }
                }

                return dp[m][n];
        }
}