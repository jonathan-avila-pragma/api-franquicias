package co.com.pragma.api.helper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper para validar objetos usando Jakarta Validation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationHelper {

    private final Validator validator;

    /**
     * Valida un objeto y retorna un Mono con error si la validación falla
     */
    public <T> Mono<T> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            
            log.warn("Validation failed: {}", errorMessage);
            return Mono.error(new IllegalArgumentException(errorMessage));
        }
        
        return Mono.just(object);
    }
}
