package com.example.musala.services.tickets

import com.example.musala.services.tickets.dtos.TicketApiRequest
import com.example.musala.services.tickets.dtos.TicketApiResponse
import com.example.musala.services.tickets.services.TicketService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/events")
class TicketController(
    private val service: TicketService,
    private val pagedResourcesAssembler: PagedResourcesAssembler<TicketApiResponse>,
) {
    @PostMapping("/{eventId}/tickets")
    fun reserveTicket(
        @PathVariable eventId: Long,
        // Nullable to allow URL building (HATEOAS)
        @RequestBody @Validated request: TicketApiRequest? = null,
    ): ResponseEntity<EntityModel<TicketApiResponse>> {
        val response = service.reserveTicket(
            eventId = eventId,
            request = request!!,
        )
        val assembler = TicketAssembler(eventId = eventId)
        val entityModel = assembler.toModel(response)
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(entityModel)
    }

    @GetMapping("/{eventId}/tickets/{ticketId}")
    fun findById(
        @PathVariable eventId: Long,
        @PathVariable ticketId: Long,
    ): ResponseEntity<EntityModel<TicketApiResponse>> {
        val entity = service.findById(
            eventId = eventId,
            ticketId = ticketId,
        )
        val assembler = TicketAssembler(eventId = eventId)
        return ResponseEntity.ok(assembler.toModel(entity))
    }

    @GetMapping("/{eventId}/tickets")
    fun findAll(
        @PathVariable eventId: Long,
        pageable: Pageable,
    ): PagedModel<EntityModel<TicketApiResponse>> {
        val shops = service.findAll(
            eventId = eventId,
            pageable = pageable,
        )
        return pagedResourcesAssembler.toModel(
            shops,
            TicketAssembler(eventId = eventId),
        )
    }
}