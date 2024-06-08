package com.example.musala.services.users.dtos

fun UserEntity.toApiResponse(): UserApiResponse {
    return UserApiResponse(
        id = id,
        name = name,
        email = email,
    )
}