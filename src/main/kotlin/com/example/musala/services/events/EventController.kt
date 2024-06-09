package com.example.musala.services.events

import com.example.musala.services.auth.CurrentlySignedInUser
import com.example.musala.services.events.dtos.EventApiRequest
import com.example.musala.services.events.dtos.EventApiResponse
import com.example.musala.services.events.dtos.EventCategory
import com.example.musala.services.events.services.EventService
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/events")
class EventController(private val service: EventService, private val signedInUser: CurrentlySignedInUser) {
    @PostMapping
    fun createEvent(@RequestBody @Validated request: EventApiRequest): ResponseEntity<EntityModel<EventApiResponse>> {
        val response = service.createEvent(request)
        val assembler = EventAssembler()
        val entityModel = assembler.toModel(response)
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(entityModel)
    }

    @GetMapping("/{eventId}")
    fun findById(
        @PathVariable eventId: Long,
    ): ResponseEntity<EntityModel<EventApiResponse>> {
        val entity = service.findById(eventId = eventId)
        val assembler = EventAssembler()
        return ResponseEntity.ok(assembler.toModel(entity))
    }

    @GetMapping
    fun findAll(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        @RequestParam(required = false) category: EventCategory?,
    ): List<EntityModel<EventApiResponse>> {
        val filters = EventFilters(
            query = query,
            category = category,
            startDate = startDate,
            endDate = endDate,
        )
        val events = service.findAll(
            filters = filters,
        )
        val assembler = EventAssembler(filters = filters)
        return assembler.toCollectionModel(events).toList()
    }

    @GetMapping("/reserved-by-me")
    fun findEventsReservedByMe(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        @RequestParam(required = false) category: EventCategory?,
    ): List<EntityModel<EventApiResponse>> {
        val filters = EventFilters(
            query = query,
            category = category,
            startDate = startDate,
            endDate = endDate,
            reservedById = signedInUser.get().id,
        )
        val events = service.findAll(
            filters = filters,
        )
        val assembler = EventAssembler(filters = filters)
        return assembler.toCollectionModel(events).toList()
    }
}