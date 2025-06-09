package com.nextmove.transaction_service.util;

import com.nextmove.transaction_service.dto.ErrorResponseDTO;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
public class ErrorBuilder {

    public static ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String error, String message, String path) {

        ErrorResponseDTO response = ErrorResponseDTO.of(
                status.value(),
                error,
                message,
                path
        );

        return ResponseEntity.status(status).body(response);
    }

}
