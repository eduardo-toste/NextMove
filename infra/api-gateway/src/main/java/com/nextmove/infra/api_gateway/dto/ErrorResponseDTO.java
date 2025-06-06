package com.nextmove.infra.api_gateway.dto;

public record ErrorResponseDTO(

        int status,
        String error,
        String message,
        String path

) {

    public static ErrorResponseDTO of(int status, String error, String message, String path) {
        return new ErrorResponseDTO(status, error, message, path);
    }

}
