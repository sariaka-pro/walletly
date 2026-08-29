package com.walletly.walletly_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.walletly.walletly_backend.exception.BadRequestException;

class InputSanitizerTest {

    private final InputSanitizer sanitizer = new InputSanitizer();

    @Test
    void sanitizePlainText_shouldReturnTrimmedText_whenInputIsSafe() {
        String cleaned = sanitizer.sanitizePlainText("  Courses hebdo  ", "expense.description");

        assertEquals("Courses hebdo", cleaned);
    }

    @Test
    void sanitizePlainText_shouldThrow_whenInputContainsScriptTag() {
        assertThrows(BadRequestException.class,
            () -> sanitizer.sanitizePlainText("<script>alert('xss')</script>", "expense.description"));
    }

    @Test
    void sanitizePlainText_shouldThrow_whenInputContainsHtmlTag() {
        assertThrows(BadRequestException.class,
            () -> sanitizer.sanitizePlainText("<b>Important</b>", "category.name"));
    }
}
