package br.com.codenest.marknow.company;

import br.com.codenest.marknow.support.ApiAsserts;
import br.com.codenest.marknow.support.CompanyPayloads;
import br.com.codenest.marknow.support.RestAssuredSupport;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class CompanyResourceTest {

    @BeforeAll
    static void setup() {
        RestAssuredSupport.setupBasePath();
    }

    @Test
    void create_whenDuplicateLegalId_caseInsensitiveAndNormalized_then409() {

        // 1) cria a primeira empresa
        given()
                .spec(RestAssuredSupport.api())
                .body(CompanyPayloads.create(
                        "Empresa A", "BR", "TAX_ID", "12345678000199"
                ))
                .when()
                .post("/companies")
                .then()
                .statusCode(201)
                .body("oid", notNullValue())
                .body("legalCountry", equalTo("BR"))
                .body("legalIdType", equalTo("TAX_ID"))
                .body("legalIdValue", equalTo("12345678000199"));

        // 2) tenta duplicar com variações
        var then = given()
                .spec(RestAssuredSupport.api())
                .body(CompanyPayloads.create(
                        "Empresa B", " br ", " tax_id ", "12345678000199"
                ))
                .when()
                .post("/companies")
                .then()
                .statusCode(409);

        ApiAsserts.assertErrorCode(then, "LEGAL_ID_ALREADY_EXISTS");
        ApiAsserts.assertHasCorrelationAndMessage(then);
    }
}
