package com.example.musala.services.users.dtos

import java.io.Serializable
import java.util.*

/**
 * DTO for {@link com.example.musala.services.users.dtos.UserEntity}
 */
data class UserApiResponse(
    val id: UUID,
    val name: String,
    val email: String,
) : Serializable