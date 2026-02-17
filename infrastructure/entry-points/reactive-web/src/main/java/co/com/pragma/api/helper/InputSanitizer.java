package co.com.pragma.api.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Slf4j
public class InputSanitizer {

    private InputSanitizer() { throw new UnsupportedOperationException("This is a utility class and cannot be instantiated"); }

    private static final Pattern[] DANGEROUS_PATTERNS = {
        Pattern.compile(".*\\$.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\{\\s*\\$.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\[\\s*\\$.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*\\|\\|.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*&&.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*javascript:.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*<script.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*onerror=.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*onload=.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*eval\\(.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*exec\\(.*", Pattern.CASE_INSENSITIVE),
    };

    private static final Pattern VALID_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_ID_LENGTH = 50;


    public static String sanitize(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        String sanitized = input.trim();

        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(sanitized).matches()) {
                log.warn("Potentially dangerous input detected and rejected: {}", sanitized);
                throw new IllegalArgumentException("Input contains potentially dangerous characters");
            }
        }

        return sanitized;
    }


    public static String validateAndSanitizeId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("ID cannot be empty");
        }

        String sanitized = id.trim();

        if (sanitized.length() > MAX_ID_LENGTH) {
            throw new IllegalArgumentException("ID exceeds maximum length of " + MAX_ID_LENGTH);
        }

        if (!VALID_ID_PATTERN.matcher(sanitized).matches()) {
            log.warn("Invalid ID format detected: {}", sanitized);
            throw new IllegalArgumentException("ID contains invalid characters. Only alphanumeric characters, hyphens and underscores are allowed");
        }

        return sanitized;
    }


    public static String validateAndSanitizeName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        String sanitized = sanitize(name);

        if (sanitized.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name exceeds maximum length of " + MAX_NAME_LENGTH);
        }

        return sanitized;
    }


    public static int validateNumber(int number, int min, int max) {
        if (number < min || number > max) {
            throw new IllegalArgumentException(String.format("Number must be between %d and %d", min, max));
        }
        return number;
    }


    public static boolean containsDangerousCharacters(String input) {
        if (!StringUtils.hasText(input)) {
            return false;
        }

        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(input).matches()) {
                return true;
            }
        }
        return false;
    }
}
