package com.example.musala.services.events

import com.example.musala.services.events.dtos.EventApiResponse
import com.example.musala.services.tickets.TicketController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo

class EventAssembler(private val query: String? = null) :
    RepresentationModelAssembler<EventApiResponse, EntityModel<EventApiResponse>> {
    override fun toModel(entity: EventApiResponse): EntityModel<EventApiResponse> {
        return EntityModel.of(
            entity,
            linkTo<EventController> {
                findById(eventId = entity.id)
            }.withSelfRel(),
            linkTo<EventController> {
                findAll(query = query)
            }.withRel("events"),
            linkTo<TicketController> {
                findAll(eventId = entity.id)
            }.withRel("tickets"),
            linkTo<TicketController> {
                reserveTicket(eventId = entity.id)
            }.withRel("reserveTicket"),
        )
    }
}