package com.example.musala.services.users.services

import com.example.musala.services.users.dtos.UserApiRequest
import com.example.musala.services.users.dtos.UserEntity
import com.example.musala.services.users.repositories.UserRepository
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserServiceImplTest {
    @Test
    fun `create user`() {
        val repository = mock(UserRepository::class.java)
        val passwordEncoder = mock(PasswordEncoder::class.java)
        val service = UserServiceImpl(
            repository = repository,
            passwordEncoder = passwordEncoder,
        )
        `when`(passwordEncoder.encode(any()))
            .thenReturn("example hashed password")
        `when`(repository.save(any()))
            .thenReturn(UserEntity(id = UUID.randomUUID()))
        val request = UserApiRequest(
            name = "Test",
            email = "test@example.com",
            password = "example1",
        )

        val actual = service.createUser(request = request)

        assertNotNull(actual.id)
    }

    @Nested
    @DisplayName("load user by username")
    inner class LoadUserByUsername {
        @Test
        fun `for valid username`() {
            val repository = mock(UserRepository::class.java)
            val passwordEncoder = mock(PasswordEncoder::class.java)
            val service = UserServiceImpl(
                repository = repository,
                passwordEncoder = passwordEncoder,
            )
            `when`(repository.findByEmail(anyString()))
                .thenReturn(UserEntity(id = UUID.randomUUID(), email = "test.found@example.com"))

            val actual = service.loadUserByUsername(username = "test.found@example.com")

            assertAll(
                { assertEquals("test.found@example.com", actual.username) },
                { assertNotNull(actual.password) },
            )
        }

        @Test
        fun `for invalid username`() {
            val repository = mock(UserRepository::class.java)
            val passwordEncoder = mock(PasswordEncoder::class.java)
            val service = UserServiceImpl(
                repository = repository,
                passwordEncoder = passwordEncoder,
            )
            `when`(repository.findByEmail(anyString()))
                .thenReturn(null)

            assertThrows<UsernameNotFoundException> {
                service.loadUserByUsername(username = "test.absent@example.com")
            }
        }
    }

}
