package com.example.musala.audit

import com.example.musala.services.users.dtos.UserEntity
import org.springframework.data.domain.AuditorAware
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component(value = "auditorAware")
class MusalaAuditorAwareImpl : AuditorAware<UserEntity> {
    override fun getCurrentAuditor(): Optional<UserEntity> {
        val authentication = SecurityContextHolder.getContext()
            .authentication

        if (
            authentication == null ||
            !authentication.isAuthenticated ||
            authentication is AnonymousAuthenticationToken
        ) return Optional.empty()

        val user = authentication.principal as UserEntity

        return Optional.of(user)
    }
}
