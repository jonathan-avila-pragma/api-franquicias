package co.com.pragma.api.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Utilidad para sanitizar y validar inputs del usuario
 * Previene NoSQL injection y otros ataques de inyección
 */
@Slf4j
public class InputSanitizer {

    // Patrones peligrosos que podrían usarse para NoSQL injection
    private static final Pattern[] DANGEROUS_PATTERNS = {
        Pattern.compile(".*\\$.*", Pattern.CASE_INSENSITIVE), // Operadores MongoDB ($gt, $ne, etc.)
        Pattern.compile(".*\\{\\s*\\$.*", Pattern.CASE_INSENSITIVE), // Objetos MongoDB
        Pattern.compile(".*\\[\\s*\\$.*", Pattern.CASE_INSENSITIVE), // Arrays con operadores
        Pattern.compile(".*\\|\\|.*", Pattern.CASE_INSENSITIVE), // Operador OR
        Pattern.compile(".*&&.*", Pattern.CASE_INSENSITIVE), // Operador AND
        Pattern.compile(".*javascript:.*", Pattern.CASE_INSENSITIVE), // XSS
        Pattern.compile(".*<script.*", Pattern.CASE_INSENSITIVE), // XSS
        Pattern.compile(".*onerror=.*", Pattern.CASE_INSENSITIVE), // XSS
        Pattern.compile(".*onload=.*", Pattern.CASE_INSENSITIVE), // XSS
        Pattern.compile(".*eval\\(.*", Pattern.CASE_INSENSITIVE), // Code injection
        Pattern.compile(".*exec\\(.*", Pattern.CASE_INSENSITIVE), // Code injection
    };

    // Patrón para validar IDs (solo alfanuméricos, guiones y guiones bajos)
    private static final Pattern VALID_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    // Longitud máxima para nombres y otros campos de texto
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_ID_LENGTH = 50;

    /**
     * Sanitiza un string removiendo caracteres peligrosos
     */
    public static String sanitize(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        // Remover espacios al inicio y final
        String sanitized = input.trim();

        // Verificar patrones peligrosos
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(sanitized).matches()) {
                log.warn("Potentially dangerous input detected and rejected: {}", sanitized);
                throw new IllegalArgumentException("Input contains potentially dangerous characters");
            }
        }

        return sanitized;
    }

    /**
     * Valida y sanitiza un ID
     */
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

    /**
     * Valida y sanitiza un nombre
     */
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

    /**
     * Valida un número (stock, etc.)
     */
    public static int validateNumber(int number, int min, int max) {
        if (number < min || number > max) {
            throw new IllegalArgumentException(String.format("Number must be between %d and %d", min, max));
        }
        return number;
    }

    /**
     * Verifica si un string contiene caracteres peligrosos sin lanzar excepción
     */
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
