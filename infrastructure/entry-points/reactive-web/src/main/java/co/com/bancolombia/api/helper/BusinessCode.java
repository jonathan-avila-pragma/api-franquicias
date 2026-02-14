package co.com.bancolombia.api.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessCode {
    // Success codes
    S200000("S200000", "Operación exitosa"),
    S201000("S201000", "Recurso creado exitosamente"),
    
    // Error codes
    B400000("B400000", "Solicitud inválida"),
    B400001("B400001", "Parámetros requeridos faltantes"),
    B400002("B400002", "Validación de datos fallida"),
    B404000("B404000", "Recurso no encontrado"),
    B409000("B409000", "Conflicto de negocio"),
    E500000("E500000", "Error interno del servidor");
    
    private final String code;
    private final String log;
}
