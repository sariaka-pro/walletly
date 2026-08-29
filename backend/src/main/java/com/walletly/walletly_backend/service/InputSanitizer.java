package com.walletly.walletly_backend.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import com.walletly.walletly_backend.exception.BadRequestException;
import com.walletly.walletly_backend.exception.ErrorMessages;

@Component
public class InputSanitizer {

    public String sanitizePlainText(String value, String fieldName) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        String cleaned = Jsoup.clean(trimmed, Safelist.none());

        // Reject payloads that rely on HTML/script markup instead of silently mutating user input.
        if (!trimmed.equals(cleaned)) {
            throw new BadRequestException(ErrorMessages.XSS_CONTENT_NOT_ALLOWED + fieldName);
        }

        return cleaned;
    }
}
