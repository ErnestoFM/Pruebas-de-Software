package com.ticketmaster.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException; // 👈 IMPORTANTE

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // ✅ PERFECTO: Usar HttpStatusCode aquí hace tu método flexible para el futuro
    private ResponseEntity<ErrorResponse> build(HttpStatusCode status, String mensaje, String error, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(mensaje, error, status.value(), Instant.now(), req.getRequestURI()));
    }

    // ✅ Solución correcta para el 'Deprecated'
    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessable(UnprocessableEntityException ex, HttpServletRequest req) {
        return build(HttpStatusCode.valueOf(422), ex.getMessage(), "UNPROCESSABLE_ENTITY", req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(NoResourceFoundException ex, HttpServletRequest req) {
        // AHORA SÍ LA USAMOS: ex.getResourcePath() nos dice qué archivo faltó
        String mensaje = "El recurso '" + ex.getResourcePath() + "' no fue encontrado.";

        return build(HttpStatus.NOT_FOUND, mensaje, "RESOURCE_NOT_FOUND", req);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST", req);
    }

    @ExceptionHandler(ValidacionDeIntegridad.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(ValidacionDeIntegridad ex, HttpServletRequest req) {
        log.warn("Business validation error: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "BUSINESS_RULE_VIOLATION", req);
    }

    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleForbidden(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), "FORBIDDEN", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, detalle.isBlank() ? "Datos inválidos" : detalle, "VALIDATION_ERROR", req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        String detalle = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, detalle.isBlank() ? "Violación de restricción" : detalle, "CONSTRAINT_VIOLATION", req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Cuerpo de la petición mal formado o incompleto", "MALFORMED_JSON", req);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpServletRequest req) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de contenido no soportado", "UNSUPPORTED_MEDIA_TYPE", req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("DataIntegrityViolation: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "Operación inválida por integridad de datos", "DATA_INTEGRITY_VIOLATION", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Error inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno inesperado", "INTERNAL_ERROR", req);
    }
}