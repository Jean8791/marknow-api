package br.com.codenest.marknow.support;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RestAssuredSupport {

    private RestAssuredSupport() {
    }

    public static RequestSpecification api() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addFilter(new ErrorLoggingFilter()) // loga request/response só quando dá erro
                .build();
    }

    public static void setupBasePath() {
        RestAssured.basePath = ""; // deixa explícito; se quiser versionar /api, muda aqui
    }
}
