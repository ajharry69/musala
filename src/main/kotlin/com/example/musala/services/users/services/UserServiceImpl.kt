package com.example.musala.services.users.services

import com.example.musala.services.users.dtos.UserApiRequest
import com.example.musala.services.users.dtos.UserApiResponse
import com.example.musala.services.users.dtos.UserEntity
import com.example.musala.services.users.dtos.toApiResponse
import com.example.musala.services.users.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserService, UserDetailsService {
    override fun createUser(request: UserApiRequest): UserApiResponse {
        val entity = UserEntity(
            name = request.name,
            email = request.email,
            encodedPassword = passwordEncoder.encode(request.password),
        )
        return repository.save(entity).toApiResponse()
    }

    override fun loadUserByUsername(username: String): UserDetails {
        return repository.findByEmail(email = username)
            ?: throw UsernameNotFoundException(null)
    }
}