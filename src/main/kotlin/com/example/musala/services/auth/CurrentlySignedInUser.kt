package com.example.musala.services.auth

import com.example.musala.services.auth.exceptions.UserNotFoundException
import com.example.musala.services.users.dtos.UserEntity

fun interface CurrentlySignedInUser {
    @Throws(UserNotFoundException::class)
    fun get(): UserEntity
}