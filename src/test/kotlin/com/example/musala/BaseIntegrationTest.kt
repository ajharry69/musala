package com.example.musala

import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.lifecycle.Startables

abstract class BaseIntegrationTest {
    companion object {
        @JvmStatic
        val postgresContainer = Containers.POSTGRESQL_CONTAINER

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