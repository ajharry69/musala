package com.example.musala.services.tickets.services

import com.example.musala.MusalaException
import com.example.musala.services.events.dtos.EventEntity
import com.example.musala.services.events.services.EventService
import com.example.musala.services.tickets.dtos.TicketApiRequest
import com.example.musala.services.tickets.dtos.TicketApiResponse
import com.example.musala.services.tickets.dtos.toApiResponse
import com.example.musala.services.tickets.dtos.toEntity
import com.example.musala.services.tickets.repositories.TicketRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketServiceImpl(
    private val eventService: EventService,
    private val repository: TicketRepository,
) : TicketService {
    @Transactional
    override fun reserveTicket(eventId: Long, request: TicketApiRequest): TicketApiResponse {
        val event = eventService.findById(eventId = eventId)

        if (request.attendeesCount > event.availableAttendeesCount) {
            throw MusalaException(HttpStatus.PRECONDITION_FAILED, errorCode = "EVENT_FULLY_BOOKED")
        }

        val availableAttendeesCount = event.availableAttendeesCount - request.attendeesCount
        eventService.updateAvailableAttendeesCountById(
            eventId = eventId,
            availableAttendeesCount = availableAttendeesCount,
        )

        val entity = request.toEntity().apply {
            this.event = EventEntity(
                id = event.id,
                name = event.name,
                date = event.date,
                category = event.category,
                description = event.description,
                availableAttendeesCount = event.availableAttendeesCount,
            )
        }
        return repository.save(entity).toApiResponse()
    }

    override fun findById(eventId: Long, ticketId: Long): TicketApiResponse {
        return repository.findByEvent_IdAndId(
            eventId = eventId,
            ticketId = ticketId,
        )?.toApiResponse()
            ?: throw MusalaException(HttpStatus.NOT_FOUND, errorCode = "TICKET_NOT_FOUND")
    }

    override fun findAll(eventId: Long): List<TicketApiResponse> {
        return repository.findAllByEvent_IdOrderByDateReservedAsc(
            eventId = eventId,
        ).map { it.toApiResponse() }
    }
}