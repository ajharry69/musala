package com.example.musala.services.auth

import com.example.musala.BaseIntegrationTest
import com.example.musala.services.users.repositories.UserRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AuthenticationControllerTest(
    @Autowired private val userRepository: UserRepository,
) : BaseIntegrationTest(userRepository = userRepository) {

    @BeforeEach
    fun setUp() {
        RestAssured.port = RestAssured.DEFAULT_PORT
        getOrCreateUser(email = "test@example.org", password = "password")
    }

    @Test
    fun `successful authentication`() {
        given()
            .contentType(ContentType.JSON)
            .body(mapOf("email" to "test@example.org", "password" to "password"))
            .apply { log() }
            .post("/auth")
            .apply { prettyPrint() }
            .then().assertThat().statusCode(200)
            .header("Authorization", notNullValue())
            .body("accessToken", notNullValue())
    }

    @ParameterizedTest
    @CsvSource(
        "test@example.org,invalid-password,401",
        "unknown.user@example.org,password,401",
    )
    fun `failed authentication`(email: String, password: String) {
        given()
            .contentType(ContentType.JSON)
            .body(mapOf("email" to email, "password" to password))
            .apply { log() }
            .post("/auth")
            .apply { prettyPrint() }
            .then().assertThat().statusCode(401)
            .header("Authorization", nullValue())
            .body("accessToken", nullValue())
    }
}