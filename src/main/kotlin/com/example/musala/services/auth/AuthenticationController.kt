package com.example.musala.services.auth

import com.example.musala.services.auth.dtos.AuthenticationRequest
import com.example.musala.services.auth.dtos.AuthenticationResponse
import com.example.musala.services.auth.services.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthenticationController(private val service: AuthenticationService) {
    @PostMapping
    fun authenticate(@RequestBody request: AuthenticationRequest): AuthenticationResponse {
        return service.authenticate(request)
    }
}