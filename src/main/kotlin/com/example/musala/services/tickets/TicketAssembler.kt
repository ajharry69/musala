package com.example.musala.services.tickets

import com.example.musala.services.tickets.dtos.TicketApiResponse
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo

class TicketAssembler(private val eventId: Long) :
    RepresentationModelAssembler<TicketApiResponse, EntityModel<TicketApiResponse>> {
    override fun toModel(entity: TicketApiResponse): EntityModel<TicketApiResponse> {
        return EntityModel.of(
            entity,
            linkTo<TicketController> {
                findById(eventId = eventId, ticketId = entity.id)
            }.withSelfRel(),
            linkTo<TicketController> {
                findAll(eventId = eventId, pageable = Pageable.unpaged())
            }.withRel("tickets"),
        )
    }
}