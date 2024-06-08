package com.example.musala.services.users.services

import com.example.musala.services.users.dtos.UserApiRequest
import com.example.musala.services.users.dtos.UserApiResponse

interface UserService {
    fun createUser(request: UserApiRequest): UserApiResponse
}
