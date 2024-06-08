package com.example.musala.services.auth.dtos


data class AuthenticationRequest(
    val email: String,
    val password: String,
)