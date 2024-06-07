package com.example.musala.audit

/*
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*

@Component(value = "auditorAware")
class MusalaAuditorAwareImpl(private val service: UserService) : AuditorAware<User> {
    override fun getCurrentAuditor(): Optional<User> {
        val jwt = getJwtFromSecurityContext()
            ?: return Optional.empty()

        val user = service.findByTokenOrCreate(jwt)
        return Optional.of(user)
    }
}*/
