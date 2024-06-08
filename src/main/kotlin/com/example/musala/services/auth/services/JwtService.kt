package com.example.musala.services.auth.services

import org.springframework.security.core.userdetails.UserDetails

interface JwtService {
    fun extractUsername(token: String): String
    fun generateToken(userDetails: UserDetails): String
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean
}
