package org.bogacheva.training.client.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bogacheva.training.client.dto.ShelveItError;
import org.bogacheva.training.client.exception.BackendException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

/**
 * Custom error handler for RestTemplate that deserializes backend error responses
 * and throws BackendException with error details.
 */
@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public RestTemplateErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
 
    @Override
    public boolean hasError(@NonNull ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() 
            || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(@NonNull URI url,
                            @NonNull HttpMethod method,
                            @NonNull ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());
        ShelveItError errorResponse = parseErrorResponse(response, statusCode, method, url);
        throw new BackendException(errorResponse, httpStatus);
    }

    private ShelveItError parseErrorResponse(ClientHttpResponse response,
                                             HttpStatusCode statusCode,
                                             HttpMethod method,
                                             URI url) {
        try {
            return objectMapper.readValue(response.getBody(), ShelveItError.class);
        } catch (Exception e) {
            String errorMessage = String.format(
                    "Backend API returned %d %s for %s %s",
                    statusCode.value(),
                    statusCode,
                    method,
                    url
            );

            return new ShelveItError(
                    statusCode.value(),
                    statusCode.toString(),
                    errorMessage,
                    null,
                    null // Backend exception class unknown
            );
        }
    }
}

