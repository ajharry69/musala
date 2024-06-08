package com.example.musala.services.users

import com.example.musala.BaseIntegrationTest
import com.example.musala.services.users.dtos.UserApiRequest
import com.example.musala.services.users.repositories.UserRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import java.util.stream.Stream
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserControllerTest(
    @Autowired private val userRepository: UserRepository,
) : BaseIntegrationTest(userRepository = userRepository) {

    @BeforeEach
    fun setUp() {
        RestAssured.port = RestAssured.DEFAULT_PORT
    }

    @Nested
    @DisplayName("POST - /users")
    inner class CreateUser {
        @Test
        fun `with valid request body`() {
            given()
                .contentType(ContentType.JSON)
                .body(
                    """{
                      "name": "Test",
                      "email": "new.user@xample.com",
                      "password": "secure-password"
                    }""".trimIndent(),
                )
                .post("/users")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo("Test"))
                .body("email", equalTo("new.user@xample.com"))
        }

        @ParameterizedTest
        @ArgumentsSource(InvalidUserApiRequestProvider::class)
        fun `with invalid request body`(data: Pair<UserApiRequest, Int>) {
            val (request, numberOfFieldValidationErrors) = data
            given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/users")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errorCode", equalTo("VALIDATION_ERROR"))
                .body("fieldErrors.size()", equalTo(numberOfFieldValidationErrors))
        }
    }

    object InvalidUserApiRequestProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
            return Stream.of(
                UserApiRequest(
                    name = "Test",
                    email = "test@example.com",
                    password = "",
                ) to 1,
                UserApiRequest(
                    name = "Test",
                    email = "test@example.com",
                    password = "    ",
                ) to 1,
                UserApiRequest(
                    name = "Test",
                    email = "test@example.com",
                    password = "P".repeat(Random.nextInt(1, 7)),
                ) to 1,
                UserApiRequest(
                    name = "Test",
                    email = "",
                    password = "P".repeat(8),
                ) to 1,
                UserApiRequest(
                    name = "Test",
                    email = "     ",
                    password = "P".repeat(8),
                ) to 1,
                UserApiRequest(
                    name = "Test",
                    email = "testexample.com",
                    password = "P".repeat(8),
                ) to 1,
                UserApiRequest(
                    name = "",
                    email = "test@example.com",
                    password = "P".repeat(8),
                ) to 1,
                UserApiRequest(
                    name = "    ",
                    email = "test@example.com",
                    password = "P".repeat(8),
                ) to 1,
                UserApiRequest(
                    name = "T".repeat(101),
                    email = "test @example.com",
                    password = "P".repeat(7),
                ) to 3,
            ).map(Arguments::of)
        }
    }
}