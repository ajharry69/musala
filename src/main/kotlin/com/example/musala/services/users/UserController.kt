package com.example.musala.services.users

import com.example.musala.services.users.dtos.UserApiRequest
import com.example.musala.services.users.dtos.UserApiResponse
import com.example.musala.services.users.services.UserService
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val service: UserService) {
    @PostMapping
    fun createUser(@RequestBody @Validated request: UserApiRequest): ResponseEntity<EntityModel<UserApiResponse>> {
        val response = service.createUser(request)
        val assembler = UserAssembler()
        val entityModel = assembler.toModel(response)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(entityModel)
    }
}