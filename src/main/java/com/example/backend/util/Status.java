package com.example.backend.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    SERVICE_ENABLE(1L,"Service was running"),
    ERROR(2L, "An error occurred"),
    SERVICE_DISABLED(3L,"The service was disabled"),
    TOKEN_EXPIRED(4L, "The token has expired"),

    ALREADY_REGISTER(100L, "The email is already registered"),
    WRONG_DATA(101L, "Wrong data"),
    IS_VERIFIED_PASSWORD(102L, "Check your email"),
    DATA_USER_ERROR(108L, "Email or password incorrect"),
    ERROR_USER(104L, "The user doesn't exist"),
    ALREADY_ACTIVATED(105L, "The email is already activated"),
    ACTIVATE_USER(106L, "The account is disabled"),
    UPDATE_PASSWORD(107L, "Email was send to update your password"),
    REGISTER_WAS_SUCCESSFUL(110L,"The register was successful"),
    NO_CONTENT(111L,"No content response"),
    SUCCESSFUL(200L, "The request was successful"),
    DONT_DELETE(205L, "This entity dont delete"),
    ALREADY_CITY_NAME(206L, "A city with that name has already been registered");

    private final Long code;
    private final String message;
}