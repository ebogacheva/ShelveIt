package org.bogacheva.training.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorModel {

    private String title;
    private String details;
    private int statusCode;
    private LocalDateTime timestamp;    
}
