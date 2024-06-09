package com.example.musala

import com.example.musala.services.auth.services.JwtServiceImpl
import com.example.musala.services.users.dtos.UserEntity
import com.example.musala.services.users.repositories.UserRepository
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.lifecycle.Startables

abstract class BaseIntegrationTest(private val userRepository: UserRepository) {
    protected fun getOrCreateUser(
        email: String = DEFAULT_EMAIL,
        password: String = DEFAULT_PASSWORD,
        name: String = "Test"
    ): UserEntity {
        return userRepository.findByEmail(email = email) ?: userRepository.save(
            UserEntity(
                name = name,
                email = email,
                encodedPassword = BCryptPasswordEncoder().encode(password),
            )
        )
    }

    protected fun getAccessToken(
        email: String = DEFAULT_EMAIL,
        password: String = DEFAULT_PASSWORD,
    ): String {
        val user = getOrCreateUser(email = email, password = password)
        return JwtServiceImpl(
            secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
            jwtExpiration = 3_600_000,
        ).generateToken(user)
    }

    companion object {
        private const val DEFAULT_EMAIL = "test@example.org"
        private const val DEFAULT_PASSWORD = "password"
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