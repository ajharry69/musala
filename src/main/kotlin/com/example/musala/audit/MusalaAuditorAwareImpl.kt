package com.example.musala.audit

import com.example.musala.services.auth.CurrentlySignedInUser
import com.example.musala.services.auth.exceptions.UserNotFoundException
import com.example.musala.services.users.dtos.UserEntity
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*

@Component(value = "auditorAware")
class MusalaAuditorAwareImpl(private val signedInUser: CurrentlySignedInUser) : AuditorAware<UserEntity> {
    override fun getCurrentAuditor(): Optional<UserEntity> {
        return try {
            Optional.of(signedInUser.get())
        } catch (ex: UserNotFoundException) {
            Optional.empty()
        }
    }
}
