package br.com.codenest.marknow.support;

import io.restassured.response.ValidatableResponse;

import static org.hamcrest.Matchers.*;

public final class ApiAsserts {

    private ApiAsserts() {}

    public static void assertHasCorrelationAndMessage(ValidatableResponse then) {
        then.body("message", not(isEmptyOrNullString()))
                .body("correlationId", not(isEmptyOrNullString()))
                .body("status", notNullValue());
    }

    public static void assertErrorCode(ValidatableResponse then, String code) {
        then.body("code", equalTo(code));
    }
}
