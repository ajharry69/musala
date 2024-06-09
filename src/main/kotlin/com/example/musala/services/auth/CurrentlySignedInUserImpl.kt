package com.example.musala.services.auth

import com.example.musala.services.auth.exceptions.UserNotFoundException
import com.example.musala.services.users.dtos.UserEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class CurrentlySignedInUserImpl : CurrentlySignedInUser {
    override fun get(): UserEntity {
        val authentication = SecurityContextHolder.getContext()
            .authentication

        if (
            authentication == null ||
            !authentication.isAuthenticated ||
            authentication is AnonymousAuthenticationToken
        ) throw UserNotFoundException()

        return authentication.principal as UserEntity
    }
}