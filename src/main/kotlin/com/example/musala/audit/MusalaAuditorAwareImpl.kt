package com.example.musala.audit

/*import com.example.musala.services.users.dtos.UserEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

        val user = authentication.principal as? UserEntity
        if (user == null) {
            logger.warn("Authentication object cannot be converted to `UserEntity`")
            return Optional.empty()
        }

        return Optional.of(user)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MusalaAuditorAwareImpl::class.java.simpleName)
    }
}*/
