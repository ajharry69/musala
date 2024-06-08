package com.example.musala

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.lifecycle.Startables
import org.testcontainers.utility.MountableFile

abstract class BaseIntegrationTest {
    protected fun getAccessToken(
        email: String = "test@example.org",
        password: String = "password",
    ): String {
        return given()
            .contentType(ContentType.JSON)
            .body(
                mapOf(
                    "email" to email,
                    "password" to password,
                )
            )
            .apply { log() }
            .post("/auth")
            .apply { prettyPrint() }
            .then().assertThat().statusCode(200)
            .extract().path("accessToken")
    }

    companion object {
        @JvmStatic
        val postgresContainer = Containers.POSTGRESQL_CONTAINER.apply {
            withCopyFileToContainer(
                MountableFile.forClasspathResource("test.sql"),
                "/docker-entrypoint-initdb.d/test.sql"
            )
        }

        init {
            Startables.deepStart(
                postgresContainer,
            ).join()
        }

        @Suppress("JUnitMalformedDeclaration")
        @JvmStatic
        @BeforeAll
        @DynamicPropertySource
        fun setupProperties(@Autowired registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)

            registry.add("spring.flyway.user", postgresContainer::getUsername)
            registry.add("spring.flyway.password", postgresContainer::getPassword)
        }
    }
}