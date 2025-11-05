package org.bogacheva.training.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Error model for displaying error pages in web UI.
 * Contains only user-friendly information - no internal implementation details.
 */
@Getter
@AllArgsConstructor
public class ErrorModel {
    private String title;
    private String details;
    private int statusCode;
    private LocalDateTime timestamp;
}

