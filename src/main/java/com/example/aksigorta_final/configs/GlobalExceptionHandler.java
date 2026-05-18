package com.example.aksigorta_final.configs;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<HashMap<String, Object>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<HashMap<String, Object>> errors = parseError(e.getFieldErrors());
        return ResponseEntity.badRequest().body(errors);
    }

    private List<HashMap<String, Object>> parseError(List<FieldError> fieldErrors) {
        List<HashMap<String, Object>> errors = new ArrayList<>();
        for (FieldError error : fieldErrors) {
            HashMap<String, Object> errorMap = new HashMap<>();
            errorMap.put("field", error.getField());
            errorMap.put("message", error.getDefaultMessage());
            errorMap.put("rejectedValue", error.getRejectedValue());
            errors.add(errorMap);
        }
        return errors;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String paramName = ex.getName();
        Object value = ex.getValue();
        Class<?> requiredType = ex.getRequiredType();

        String message = "";

        if (requiredType != null) {
            if (requiredType == Long.class || requiredType == Integer.class) {
                message = "'" + paramName + "' must be a numeric value. Provided value: '" + value + "'";
            } else if (requiredType == Boolean.class) {
                message = "'" + paramName + "' must be true or false. Provided value: '" + value + "'";
            } else {
                message = "Invalid value for parameter '" + paramName + "': '" + value + "'";
            }
        } else {
            message = "Invalid parameter: '" + paramName + "'";
        }

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", ex.getLocalizedMessage());
        return ResponseEntity.badRequest().body(error);
    }
}