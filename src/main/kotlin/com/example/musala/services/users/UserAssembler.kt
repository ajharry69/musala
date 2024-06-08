package com.example.musala.services.users

import com.example.musala.services.users.dtos.UserApiResponse
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler

class UserAssembler : RepresentationModelAssembler<UserApiResponse, EntityModel<UserApiResponse>> {
    override fun toModel(entity: UserApiResponse): EntityModel<UserApiResponse> {
        return EntityModel.of(
            entity,
        )
    }
}