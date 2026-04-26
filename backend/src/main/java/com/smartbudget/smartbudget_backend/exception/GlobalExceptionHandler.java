package com.smartbudget.smartbudget_backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // Erreur 400 bad request
        @ExceptionHandler(BadRequestException.class)
        private ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException badResquest,
                        HttpServletRequest request) {
                Map<String, Object> error = new HashMap<>(); /// Object : accepte tous les types
                error.put("status", 400); // put: never accept 1 value, always 2 "type, key"
                error.put("message", badResquest.getMessage());
                error.put("timestamp", LocalDateTime.now()); // La date de maintenant
                error.put("path", request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        /// Erreur 404 not found
        @ExceptionHandler(NotFoundException.class) /// Si c’est une NotFoundException… tu fais ça :
        private ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException notFound,
                        HttpServletRequest request) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 404);
                error.put("message", notFound.getMessage());
                error.put("timestamp", LocalDateTime.now());
                error.put("path", request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        private ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException notValid,
                        HttpServletRequest request) {
                Map<String, Object> error = new HashMap<>();
                String message = notValid
                                .getBindingResult()
                                .getFieldError()
                                .getDefaultMessage();
                error.put("status", 400);
                error.put("message", message);
                error.put("timestamp", LocalDateTime.now());
                error.put("path", request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<Map<String, Object>> handleForbiddenException(
                        ForbiddenException ex,
                        HttpServletRequest request) {

                Map<String, Object> error = new HashMap<>();

                error.put("status", 403);
                error.put("message", ex.getMessage());
                error.put("timestamp", LocalDateTime.now());
                error.put("path", request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(error);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        private ResponseEntity<Map<String, Object>> handleUnreadableMessage(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 400);
                error.put("message", "Invalid request body");
                error.put("timestamp", LocalDateTime.now());
                error.put("path", request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        /// Toutes les erreurs 500
        @ExceptionHandler(Exception.class)
        private ResponseEntity<Map<String, Object>> handleInternalServerError(Exception ex,
                        HttpServletRequest request) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", 500);
                error.put("message", "Internal server error");
                error.put("timestamp", LocalDateTime.now());
                error.put("path", request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(error);
        }

}