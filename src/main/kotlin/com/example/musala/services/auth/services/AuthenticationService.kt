package com.example.musala.services.auth.services

import com.example.musala.services.auth.dtos.AuthenticationRequest
import com.example.musala.services.auth.dtos.AuthenticationResponse

interface AuthenticationService {
    fun authenticate(request: AuthenticationRequest): AuthenticationResponse
}
