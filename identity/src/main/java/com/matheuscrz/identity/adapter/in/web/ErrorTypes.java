package com.matheuscrz.identity.adapter.in.web;

import java.net.URI;

public final class ErrorTypes {

    private static final String BASE_URI = "https://shopflow.dev/errors/";

    public static final URI INVALID_CREDENTIALS = URI.create(BASE_URI + "invalid-credentials");
    public static final URI EMAIL_ALREADY_EXISTS = URI.create(BASE_URI + "email-already-exists");
    public static final URI USER_NOT_FOUND = URI.create(BASE_URI + "user-not-found");
    public static final URI INVALID_TOKEN = URI.create(BASE_URI + "invalid-token");
    public static final URI INVALID_REFRESH_TOKEN = URI.create(BASE_URI + "invalid-refresh-token");
    public static final URI INVALID_USER_DATA = URI.create(BASE_URI + "invalid-user-data");
    public static final URI VALIDATION_ERROR = URI.create(BASE_URI + "validation-error");

    private ErrorTypes() {
    }
}