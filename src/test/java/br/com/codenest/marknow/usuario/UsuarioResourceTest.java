package br.com.codenest.marknow.usuario;

import br.com.codenest.marknow.dto.usuario.UsuarioCreateResquest;
import br.com.codenest.marknow.support.UsuarioTestData;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class UsuarioResourceTest {

    private static io.restassured.specification.RequestSpecification givenJson() {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    private static String criarUsuarioEObterOid() {
        UsuarioCreateResquest req = UsuarioTestData.novo().build();

        return givenJson()
                .body(req)
                .when()
                .post("/usuario")
                .then()
                .statusCode(201)
                .body("oid", notNullValue())
                .extract()
                .path("oid");
    }

    @Test
    void create201() {
        UsuarioCreateResquest req = UsuarioTestData.novo().build();

        givenJson()
                .body(req)
                .when()
                .post("/usuario")
                .then()
                .statusCode(201)
                .body("oid", notNullValue())
                .body("email", equalTo(req.getEmail()))
                .body("nome", equalTo(req.getNome()));
    }

    @Test
    void create400_emailInvalido() {
        String emailInvalido = "email_invalido";

        UsuarioCreateResquest req = UsuarioTestData.novo()
                .email(emailInvalido)
                .build();

        var onErro = givenJson()
                .body(req)
                .when()
                .post("/usuario")
                .then()
                .statusCode(400);

        onErro.body("fieldErrors[0].field", equalTo("create.req.email"));
        onErro.body("fieldErrors[0].message", equalTo("email inválido"));
        onErro.body("fieldErrors[0].rejectedValue", equalTo(emailInvalido));
    }

    @Test
    void create409_emailDuplicado() {
        UsuarioCreateResquest req = UsuarioTestData.novo().build();

        // cria 1x
        givenJson().body(req).post("/usuario").then().statusCode(201);

        // cria 2x com mesmo email -> conflito
        givenJson()
                .body(req)
                .when()
                .post("/usuario")
                .then()
                .statusCode(409);
    }

    @Test
    void get200() {
        String oid = criarUsuarioEObterOid();

        givenJson()
                .when()
                .get("/usuario/{oid}", oid)
                .then()
                .statusCode(200)
                .body("oid", equalTo(oid));
    }

    @Test
    void get404() {
        givenJson()
                .when()
                .get("/usuario/{oid}", "nao-existe")
                .then()
                .statusCode(404);
    }

    @Test
    void delete204() {
        String oid = criarUsuarioEObterOid();

        givenJson()
                .when()
                .delete("/usuario/{oid}", oid)
                .then()
                .statusCode(204);

        // garante que sumiu
        givenJson()
                .when()
                .get("/usuario/{oid}", oid)
                .then()
                .statusCode(404);
    }
}
