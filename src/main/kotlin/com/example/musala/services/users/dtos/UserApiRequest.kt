package com.example.musala.services.users.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length
import java.io.Serializable

/**
 * DTO for {@link com.example.musala.services.users.dtos.UserEntity}
 */
data class UserApiRequest(
    @field:NotNull
    @field:NotEmpty
    @field:NotBlank
    @field:Length(max = 100)
    val name: String,
    @field:NotNull
    @field:NotEmpty
    @field:NotBlank
    @field:Email
    val email: String,
    @field:NotNull
    @field:NotEmpty
    @field:NotBlank
    @field:Length(min = 8)
    val password: String,
) : Serializable