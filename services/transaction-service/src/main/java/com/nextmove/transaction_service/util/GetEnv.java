package com.nextmove.transaction_service.util;

import io.github.cdimascio.dotenv.Dotenv;

public class GetEnv {
    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        return dotenv.get(key);
    }
}