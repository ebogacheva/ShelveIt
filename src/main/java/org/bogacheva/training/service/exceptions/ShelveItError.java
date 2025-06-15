package org.bogacheva.training.service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ShelveItError {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
