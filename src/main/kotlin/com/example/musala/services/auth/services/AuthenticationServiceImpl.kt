package com.example.musala.services.auth.services

import com.example.musala.services.auth.dtos.AuthenticationRequest
import com.example.musala.services.auth.dtos.AuthenticationResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl(
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
) : AuthenticationService {
    override fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )
        val user = userDetailsService.loadUserByUsername(request.email)
        val jwtToken = jwtService.generateToken(user)
        return AuthenticationResponse(accessToken = jwtToken)
    }
}