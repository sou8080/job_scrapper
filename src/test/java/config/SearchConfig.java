package config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchConfig {

        // ==========================================
        // LOCATION
        // ==========================================

        public static final String LOCATION = ConfigLoader.get("job_location");

        // ==========================================
        // RAW KEYWORDS
        // ==========================================

        public static final List<String> KEYWORDS = Arrays.stream(ConfigLoader.get("job_search_keywords").split(","))
                        .map(String::trim).filter(keyword -> !keyword.isEmpty()).distinct()
                        .collect(Collectors.toList());

        public static final String COMMA_SEPARATED_KEYWORDS = buildCommaSeparatedKeywords(); // COMMA BASED SEARCH

        public static final String OR_BASED_KEYWORDS = buildOrBasedKeywords(); // OR BASED SEARCH

        // ==========================================
        // COMMA KEYWORDS BUILDER
        // ==========================================

        private static String buildCommaSeparatedKeywords() {

                // ==========================================
                // SINGLE KEYWORD
                // ==========================================

                if (KEYWORDS.size() == 1) {
                        return KEYWORDS.get(0).trim();
                }

                // ==========================================
                // MULTIPLE KEYWORDS
                // ==========================================

                return KEYWORDS.stream().map(String::toLowerCase).map(String::trim).distinct()
                                .collect(Collectors.joining(", "));
        }

        // ==========================================
        // OR KEYWORDS BUILDER
        // ==========================================

        private static String buildOrBasedKeywords() {

                if (KEYWORDS.size() == 1) {
                        return KEYWORDS.get(0).trim();
                }
                return KEYWORDS.stream().map(String::toLowerCase).map(String::trim).distinct()
                                .collect(Collectors.joining(" OR "));
        }
}